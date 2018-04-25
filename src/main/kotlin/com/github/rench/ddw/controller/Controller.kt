package com.github.rench.ddw.controller

import com.github.rench.ddw.domain.Address
import com.github.rench.ddw.domain.Block
import com.github.rench.ddw.domain.Transaction
import com.github.rench.ddw.service.*
import com.github.rench.ddw.vo.FetchResponse
import org.apache.commons.lang3.math.NumberUtils
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigInteger
import javax.servlet.http.HttpServletRequest

/**
 * transaction api
 */
@RestController
@RequestMapping("/api/tx")
class TransactionController(private val service: TransactionService) {

    /**
     * return latest n transactions
     */
    @GetMapping("/latest/{n}")
    fun latest(@PathVariable("n") n: Int): Flux<Transaction> = service.latest(n)

    /**
     * return transactions from specified block number
     */
    @GetMapping("/block/{bn}")
    fun blockNumber(@PathVariable("bn") bn: BigInteger): Flux<Transaction> = service.blockNum(bn)

    /**
     * return transactions with specified block hash
     */

    @GetMapping("/hash/{h}")
    fun blockHash(@PathVariable("bh") bh: String): Flux<Transaction> = service.blockHash(bh)

    /**
     * return transaction with specified hash
     */
    @GetMapping("/{hash}")
    fun hash(@PathVariable("hash") hash: String): Mono<Transaction> = service.hash(hash)
}

/**
 * address api
 */
@RestController
@RequestMapping("/api/address")
class AddressController(private val service: IAddressService) {
    /**
     * return ddw address info
     */
    @RequestMapping("/{addr}")
    fun get(@PathVariable("addr") addr: String): Mono<Address> = service.address(addr)

    /**
     * return the top n balance address info
     */
    @RequestMapping("/top/{n}")
    fun top(@PathVariable("n") n: Int): Flux<Address> = service.top(n)

    /**
     * return the top n balance address info
     */
    @RequestMapping("/latest/{n}")
    fun latest(@PathVariable("n") n: Int): Flux<Address> = service.latest(n)
}

/**
 * block info
 */

@RestController
@RequestMapping("/api/block")
class BlockController(private val service: IBlockService) {
    /**
     * return the ddw block info with specified block number
     */
    @RequestMapping("/{num}")
    fun get(@PathVariable("num") num: BigInteger): Mono<Block> = service.block(num)

    /**
     * return the ddw block info with specified block number
     */
    @RequestMapping("/hash/{hash}")
    fun hash(@PathVariable("hash") hash: BigInteger): Mono<Block> = service.block(hash)

    /**
     * return the latest n block info
     */
    @RequestMapping("/latest/{n}")
    fun latest(@PathVariable("n") n: Int): Flux<Block> = service.latest(n)

    /**
     * fetch blocks from current max block
     */
    @RequestMapping("/fetch")
    fun fetch(): Mono<FetchResponse> = service.fetch()
}


@RestController
@RequestMapping("/api/statistics")
class StatisticsController(private val summary: ISummaryService) {
    @RequestMapping("/summary")
    fun summary() = summary.summary()
}

/**
 * page controller
 */
@Controller
class PageController(private val block: IBlockService, private val address: IAddressService, private val tx: ITransactionService) {
    @RequestMapping(value = ["", "/"])
    fun home(): String = "redirect:/index"

    @RequestMapping("/index")
    fun index(): String = "index"

    @RequestMapping("/address/{address}")
    fun address(@PathVariable("address") addr: String, req: HttpServletRequest): String {
        var save = address.address(addr).block() ?: return "404"
        req.setAttribute("addr", save)
        return "address"
    }

    @RequestMapping("/tx/{hash}")
    fun transaction(@PathVariable("hash") hash: String, req: HttpServletRequest): String {
        var save: Transaction? = tx.hash(hash).block() ?: return "404"
        req.setAttribute("tx", save)
        return "tx"
    }

    @RequestMapping("/block/{hash}")
    fun block(@PathVariable("hash") hash: String, req: HttpServletRequest): String {
        var save = block.hash(hash).block()
        if (save == null && NumberUtils.isCreatable(hash)) {
            save = block.block(BigInteger.valueOf(hash.toLong())).block()
        }
        if (save == null) {
            return "404"
        }
        save.txs = tx.blockNum(save.number!!).collectList().block()
        req.setAttribute("block", save)
        return "block"
    }
}
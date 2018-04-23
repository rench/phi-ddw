package com.github.rench.ddw.controller

import com.github.rench.ddw.domain.Address
import com.github.rench.ddw.domain.Block
import com.github.rench.ddw.domain.Transaction
import com.github.rench.ddw.service.IAddressService
import com.github.rench.ddw.service.IBlockService
import com.github.rench.ddw.service.TransactionService
import com.github.rench.ddw.vo.FetchResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigInteger

/**
 * transaction api
 */
@RestController
@RequestMapping("/tx")
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
@RequestMapping("/address")
class AddressController(private val service: IAddressService) {
    /**
     * return ddw address info
     */
    @RequestMapping("/{addr}")
    fun get(@PathVariable("addr") addr: String): Mono<Address> = Mono.empty()

    /**
     * return the top n balance address info
     */
    @RequestMapping("/top/{n}")
    fun top(@PathVariable("n") n: Int): Flux<Address> = Flux.empty()
}

/**
 * block info
 */

@RestController
@RequestMapping("/block")
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
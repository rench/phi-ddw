package com.github.rench.ddw.controller

import com.github.rench.ddw.rpc.RpcApi
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.web3j.protocol.core.methods.response.Transaction
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigInteger

/**
 * transaction interface
 */
@RestController
@RequestMapping("/trans")
class TransactionController {

    /**
     * return latest n transactions
     */
    @GetMapping("/latest/{n}")
    fun latest(@PathVariable("n") n: Integer): Flux<Transaction> {
        return Flux.empty()
    }

    /**
     * return transactions from specified block number
     */
    @GetMapping("/block/{bn}")
    fun blockNumber(@PathVariable("bn") bn: BigInteger): Flux<Transaction> {
        return RpcApi.getTransactionsFromBlock(bn)
    }

    /**
     * return transactions with specified hash
     */

    @GetMapping("/hash/{h}")
    fun blockHash(@PathVariable("bh") bh: String): Flux<Transaction> {
        return RpcApi.getTransactionsFromBlockHash(bh)
    }
    @GetMapping("/{hash}")
    fun hash(@PathVariable("hash") hash: String): Mono<Transaction> {
        return RpcApi.getTransactionFromTxHash(hash)
    }
}


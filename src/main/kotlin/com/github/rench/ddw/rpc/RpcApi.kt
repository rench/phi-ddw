package com.github.rench.ddw.rpc

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.EthBlock
import org.web3j.protocol.core.methods.response.Transaction
import org.web3j.protocol.http.HttpService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigInteger

/**
 * rpc api interface
 */
object RpcApi {
    val LOG: Logger = LoggerFactory.getLogger(RpcApi::class.java)
    val web3j: Web3j = Web3j.build(HttpService("http://192.168.1.123:8200"))
    /**
     * query the balance with specified address from  rpc client
     */
    fun getBalance(address: String) =
            Mono.justOrEmpty(
                    web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send().balance
            )

    /**
     * query the transaction count with specified address from rpc client
     */
    fun getTransactionCount(address: String) =
            Mono.just(web3j.ethGetTransactionCount(address, DefaultBlockParameterName.PENDING).send().transactionCount)

    /**
     * query the current block number from rpc client
     */
    fun getBlockNumber() = Mono.justOrEmpty(web3j.ethBlockNumber().send().blockNumber)

    /**
     * query the transactions in specified block number
     */
    fun getTransactionsFromBlock(number: BigInteger): Flux<Transaction> {
        val count: BigInteger = web3j.ethGetBlockTransactionCountByNumber(DefaultBlockParameter.valueOf(number)).send().transactionCount
        return Flux.create<Transaction> {
            for (i in 0 until count.longValueExact())
                it.next(
                        web3j.ethGetTransactionByBlockNumberAndIndex(
                                DefaultBlockParameter.valueOf(number),
                                BigInteger.valueOf(i)).send().transaction.get()
                )
            it.complete()
        }
    }

    /**
     * query the transactions in specified block hash
     */
    fun getTransactionsFromBlockHash(hash: String): Flux<Transaction> {
        val count: BigInteger = web3j.ethGetBlockTransactionCountByHash(hash).send().transactionCount
        return Flux.create<Transaction> {
            for (i in 0 until count.longValueExact())
                it.next(
                        web3j.ethGetTransactionByBlockHashAndIndex(
                                hash,
                                BigInteger.valueOf(i)).send().transaction.get()
                )
            it.complete()
        }
    }

    /**
     * query the transactions from specified transaction hash
     */
    fun getTransactionFromTxHash(hash: String): Mono<Transaction> {
        return Mono.just(
                web3j.ethGetTransactionByHash(hash).send().transaction.get()
        )
    }

    /**
     * query the block info with specified block number
     */
    fun getBlock(number: BigInteger): Mono<EthBlock.Block> {
        return Mono.just(
                web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(number), false).send().block
        )
    }

}


fun main2(args: Array<String>) {
    //RpcApi.getBalance("0x61f63ddd3ccbfcaaa56dda1e5aa22298e31f53af").subscribe(System.out::print)
    //RpcApi.getBlockNumber().log().subscribe(System.out::print)
    //RpcApi.web3j.blockObservable(true).subscribe {
    //    System.out.println(it.block.hash)
    //}
    RpcApi.getTransactionsFromBlock(BigInteger.valueOf(55293)).subscribe { System.out.println(it.hash) }
    RpcApi.getTransactionFromTxHash("0x3c01a1ea87daebbf4397f756a12cbe7e40ad42ced3ce9e4c23eb56d570a4519c").subscribe { System.out.println(it.hash) }
}
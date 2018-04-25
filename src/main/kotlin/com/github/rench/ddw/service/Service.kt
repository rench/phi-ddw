package com.github.rench.ddw.service

import com.github.rench.ddw.domain.Address
import com.github.rench.ddw.domain.Block
import com.github.rench.ddw.domain.Transaction
import com.github.rench.ddw.repository.AddressRepository
import com.github.rench.ddw.repository.BlockRepository
import com.github.rench.ddw.repository.TransactionRepository
import com.github.rench.ddw.rpc.RpcApi
import com.github.rench.ddw.vo.FetchResponse
import com.github.rench.ddw.vo.SummaryResponse
import org.apache.commons.lang3.time.DateUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.BeanUtils
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigInteger
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean
import java.util.stream.Collectors

/**
 * transaction service interface
 */
interface ITransactionService {
    fun latest(n: Int): Flux<Transaction>
    fun blockNum(num: BigInteger): Flux<Transaction>
    fun blockHash(hash: String): Flux<Transaction>
    fun hash(hash: String): Mono<Transaction>
}

/**
 * address service interface
 */
interface IAddressService {
    fun top(n: Int): Flux<Address>
    fun latest(n: Int): Flux<Address>
    fun address(addr: String): Mono<Address>
}

/**
 * block service interface
 */
interface IBlockService {
    fun latest(n: Int): Flux<Block>
    fun block(n: BigInteger): Mono<Block>
    fun hash(h: String): Mono<Block>
    fun fetch(): Mono<FetchResponse>
}

/**
 * summary service
 */
interface ISummaryService {
    fun summary(): Mono<SummaryResponse>
}

/**
 * transaction service impl
 */
@Service
class TransactionService(private val dao: TransactionRepository) : ITransactionService {
    override fun latest(n: Int): Flux<Transaction> {
        return Flux.create {
            when (n) {
                5 -> dao.findTop5ByOrderByTimestampDesc().forEach { x -> it.next(x) }
                else -> dao.findTop10ByOrderByTimestampDesc().forEach { x -> it.next(x) }
            }
            it.complete()
        }
    }

    override fun blockNum(num: BigInteger): Flux<Transaction> {
        return Flux.create {
            dao.findByBlockNumber(num).forEach { t -> it.next(t) }
            it.complete()
        }
    }

    override fun blockHash(hash: String): Flux<Transaction> {
        return Flux.create {
            dao.findByBlockHash(hash).forEach { t -> it.next(t) }
            it.complete()
        }
    }

    override fun hash(hash: String): Mono<Transaction> {
        return Mono.create {
            it.success(dao.findByHash(hash))
        }
    }
}

/**
 * address service impl
 */
@Service
class AddressService(private val dao: AddressRepository, private val txDao: TransactionRepository) : IAddressService {
    override fun top(n: Int): Flux<Address> {
        return Flux.create {
            when (n) {
                5 -> dao.findTop5ByOrderByBalanceDesc().forEach { a -> it.next(a) }
                else -> dao.findTop10ByOrderByBalanceDesc().forEach { a -> it.next(a) }
            }
            it.complete()
        }
    }

    override fun latest(n: Int): Flux<Address> {
        return Flux.create {
            when (n) {
                5 -> dao.findTop5ByOrderByLastModifiedDateDesc().forEach { a -> it.next(a) }
                else -> dao.findTop10ByOrderByLastModifiedDateDesc().forEach { a -> it.next(a) }
            }
            it.complete()
        }
    }

    override fun address(addr: String): Mono<Address> {
        return Mono.create {
            var address = dao.findById(addr).orElse(null)
            if (address != null) {
                address.txs = txDao.findTop30ByFromAddressOrToAddressOrderByTimestampDesc(address.address!!, address.address!!)
            }
            it.success(address)
        }
    }
}

/**
 * block service impl
 */
@Service
class BlockService(private val dao: BlockRepository, private val txDao: TransactionRepository, private val addrDao: AddressRepository) : IBlockService {
    private val log: Logger = LoggerFactory.getLogger(BlockService::class.java)
    private val isListen: AtomicBoolean = AtomicBoolean(false)
    private val isDoing: AtomicBoolean = AtomicBoolean(false)
    override fun latest(n: Int): Flux<Block> {
        return Flux.create {
            when (n) {
                5 -> dao.findTop5ByOrderByNumberDesc().forEach { b -> it.next(b) }
                else -> dao.findTop10ByOrderByNumberDesc().forEach { b -> it.next(b) }
            }
            it.complete()
        }
    }

    override fun block(n: BigInteger): Mono<Block> {
        return Mono.create {
            it.success(dao.findById(n).orElse(null))
        }
    }

    override fun hash(h: String): Mono<Block> {
        return Mono.create {
            it.success(dao.findByHash(h))
        }
    }

    override fun fetch(): Mono<FetchResponse> {
        if (!isListen.get() && isListen.compareAndSet(false, true)) {
            RpcApi.web3j.blockObservable(true).subscribe {
                log.info("new block {}", it.block.number)
                fetch().block()
            }
        }
        if (isDoing.get() || !isDoing.compareAndSet(false, true)) {
            log.info("other thread is fetching")
            return Mono.empty()
        }
        log.info("start fetching")
        var block = dao.findFirstByOrderByNumberDesc()
        var last = (block?.number ?: BigInteger.ZERO).longValueExact()
        var max = RpcApi.getBlockNumber().block().longValueExact()
        val cur = kotlin.math.min(last + 5000, max)
        return Mono.create {
            var fs = mutableListOf<CompletableFuture<Boolean>>()
            var set: MutableSet<String> = HashSet()
            (last + 1..cur).mapTo(fs) { i ->
                CompletableFuture.supplyAsync {
                    var block = RpcApi.getBlock(BigInteger.valueOf(i)).block()
                    var save = Block()
                    BeanUtils.copyProperties(block, save)
                    save.transactionCount = block.transactions.size.toLong()
                    dao.save(save)
                    var txs = RpcApi.getTransactionsFromBlock(BigInteger.valueOf(i)).map {
                        var tx = Transaction()
                        BeanUtils.copyProperties(it, tx)
                        tx.fromAddress = it.from
                        tx.toAddress = it.to
                        tx.timestamp = block.timestamp.toLong()
                        tx.input = null
                        if (tx.fromAddress != null) set.add(tx.fromAddress!!)
                        if (tx.toAddress != null) set.add(tx.toAddress!!)
                        tx
                    }.toStream().filter { !txDao.existsByHash(it.hash!!) }.collect(Collectors.toList())
                    txDao.saveAll(txs)
                    true
                }
            }
            if (fs.size > 0) {
                CompletableFuture.allOf(*fs.toTypedArray()).join()
            }
            fs.clear()
            set.forEach {
                var bal = RpcApi.getBalance(it).block()
                var addr: Address
                if (addrDao.existsById(it)) {
                    addr = addrDao.findById(it).get()
                    addr.balance = bal
                } else {
                    addr = Address()
                    addr.address = it
                    addr.balance = bal
                    addr.transactionCount = BigInteger.valueOf(0)
                }
                addrDao.save(addr)
            }
            val resp = FetchResponse(BigInteger.valueOf(cur), BigInteger.valueOf(last), BigInteger.valueOf(max))
            isDoing.set(false)
            log.info("fetch done")
            it.success(resp)
        }
    }

}

@Service
class SummaryService(private val blockDao: BlockRepository, private val txDao: TransactionRepository, private val addrDao: AddressRepository) : ISummaryService {
    override fun summary(): Mono<SummaryResponse> {
        return Mono.create {
            val tx = txDao.count()
            val addr = addrDao.count()
            val block = blockDao.count()
            val day = DateUtils.addDays(Date(), -7)
            val tx7Day = txDao.countByCreatedDateGreaterThan(day)
            val addr7Day = addrDao.countByCreatedDateGreaterThan(day)
            val block7Day = blockDao.countByCreatedDateGreaterThan(day)
            it.success(SummaryResponse(addr, tx, block, addr7Day, tx7Day, block7Day))
        }
    }

}
package com.github.rench.ddw.repository

import com.github.rench.ddw.domain.Address
import com.github.rench.ddw.domain.Block
import com.github.rench.ddw.domain.Transaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.math.BigInteger

@Repository
interface TransactionRepository : JpaRepository<Transaction, Long> {
    fun findTop5ByOrderByTimestampDesc(): List<Transaction>
    fun findTop10ByOrderByTimestampDesc(): List<Transaction>
    fun findByHash(hash: String): Transaction?
    fun findByBlockNumber(num: BigInteger): List<Transaction>
    fun findByBlockHash(hash: String): List<Transaction>
}

@Repository
interface AddressRepository : JpaRepository<Address, String> {
    fun findTop5ByOrderByBalanceDesc(): List<Address>
    fun findTop10ByOrderByBalanceDesc(): List<Address>
}

@Repository
interface BlockRepository : JpaRepository<Block, BigInteger> {
    fun findTop5ByOrderByNumberDesc(): List<Block>
    fun findTop10ByOrderByNumberDesc(): List<Block>
    fun findByHash(hash: String): Block?
    fun findFirstByOrderByNumberDesc(): Block?
}

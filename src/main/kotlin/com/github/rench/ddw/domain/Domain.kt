package com.github.rench.ddw.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigInteger
import java.util.*
import javax.persistence.*

/**
 * ddw address info
 */
@Entity
@Table(name = "address", indexes = [Index(columnList = "balance")])
@EntityListeners(value = AuditingEntityListener::class)
data class Address(
        @Id
        @Column(length = 42)
        var address: String? = null,
        var balance: BigInteger? = null,
        @Column(name = "transaction_count")
        var transactionCount: BigInteger? = null,
        @CreatedDate
        @Column(name = "created_date")
        var createdDate: Date? = null,
        @LastModifiedDate
        @Column(name = "last_modified_date")
        var lastModifiedDate: Date? = null,
        @Transient
        var txs: List<Transaction>? = null
)

/**
 * ddw chain transaction info
 */
@Entity
@Table(name = "transaction", indexes = [(Index(columnList = "from_address,timestamp")), (Index(columnList = "to_address,timestamp"))])
@EntityListeners(value = AuditingEntityListener::class)
data class Transaction(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,
        @Column(name = "block_hash", length = 66)
        var blockHash: String? = null,
        @Column(name = "block_number")
        var blockNumber: BigInteger? = null,
        @Column(name = "from_address", length = 42)
        var fromAddress: String? = null,
        var gas: BigInteger? = null,
        @Column(name = "gas_price")
        var gasPrice: BigInteger? = null,
        @Column(unique = true)
        var hash: String? = null,
        var input: String? = null,
        var nonce: String? = null,
        @Column(name = "to_address", length = 42)
        var toAddress: String? = null,
        var value: BigInteger? = null,
        var timestamp: Long? = null,
        @CreatedDate
        @Column(name = "created_date")
        var createdDate: Date? = null
)

/**
 * ddw chain block info
 */

@Entity
@Table(name = "block", indexes = [Index(columnList = "hash"), Index(columnList = "miner")])
@EntityListeners(value = AuditingEntityListener::class)
data class Block(
        @Id
        var number: BigInteger? = null,
        @Column(length = 66)
        var hash: String? = null,
        var nonce: String? = null,
        var size: Long? = null,
        @Column(length = 42)
        var miner: String? = null,
        var difficulty: BigInteger? = null,
        @Column(name = "gas_limit")
        var gasLimit: BigInteger? = null,
        @Column(name = "gas_used")
        var gasUsed: BigInteger? = null,
        @Column(name = "transaction_count")
        var transactionCount: Long? = null,
        @CreatedDate
        @Column(name = "created_date")
        var createdDate: Date? = null,
        @Transient
        var txs: List<Transaction>? = null
)


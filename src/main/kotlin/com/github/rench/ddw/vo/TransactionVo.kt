package com.github.rench.ddw.vo

import java.math.BigInteger

data class TransactionVo(val blockHash: String,
                         val blockNumber: BigInteger,
                         val from: String,
                         val gas: BigInteger,
                         val gasPrice: BigInteger,
                         val hash: String,
                         val input: String,
                         val nonce: String,
                         val to: String,
                         val value: BigInteger)
package com.github.rench.ddw.vo

import java.math.BigInteger

/**
 * base response
 */
open class Response(var status: Int = 200, var msg: String = "")

/**
 * fetch response
 */
data class FetchResponse(val current: BigInteger,
                         val start: BigInteger,
                         val max: BigInteger) : Response()

/**
 * summary response
 */
data class SummaryResponse(val address: Long,
                           val transaction: Long,
                           val block: Long,
                           val address7Day: Long,
                           val transaction7Day: Long,
                           val block7Day: Long) : Response()
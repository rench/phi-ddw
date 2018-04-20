import com.alibaba.fastjson.JSON
import com.github.rench.ddw.rpc.RpcApi.web3j
import com.sun.xml.internal.ws.util.CompletedFuture
import org.junit.Test
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Convert
import java.math.BigDecimal
import java.math.BigInteger
import java.util.concurrent.*

class FetchTransactionAddressTest {
    val map: MutableMap<String, BigDecimal> = ConcurrentHashMap()
    val pool: ExecutorService = Executors.newFixedThreadPool(200)
    val web3j: Web3j = Web3j.build(HttpService("http://localhost:8200"))
    @Test
    fun testFetch() {
        var currentBlockNum: Long = web3j.ethBlockNumber().send().blockNumber.longValueExact()
        var fs = mutableListOf<CompletableFuture<Boolean>>()
        for (i in 1..currentBlockNum) {
            var txCount = web3j.ethGetBlockTransactionCountByNumber(DefaultBlockParameter.valueOf(BigInteger.valueOf(i))).send().transactionCount.longValueExact()
            if (txCount != 0L) {
                (0 until txCount).mapTo(fs) {
                    CompletableFuture.supplyAsync {
                        var tx = web3j.ethGetTransactionByBlockNumberAndIndex(
                                DefaultBlockParameter.valueOf(BigInteger.valueOf(i)),
                                BigInteger.valueOf(it)
                        ).send().transaction
                        println("from ${tx.get()?.from} to ${tx.get()?.to} value ${Convert.fromWei(tx.get().value.toString(), Convert.Unit.ETHER)} in block ${tx.get().blockNumber}")
                        if (tx.get().from!=null && !map.containsKey(tx.get().from)) {
                            map[tx.get()?.from] = BigDecimal.ZERO
                        }
                        if (tx.get().to != null && !map.containsKey(tx.get().to)) {
                            map[tx.get().to] = BigDecimal.ZERO
                        } else {
                            println(JSON.toJSONString(tx.get()))
                        }
                        true
                    }
                }

            }
            Thread.yield()
        }
        val allOf = CompletableFuture.allOf(*fs.toTypedArray())
        allOf.join()
        val countDown = CountDownLatch(map.keys.size)
        for (k in map.keys) {
            pool.execute {
                map[k] = web3j.ethGetBalance(k, DefaultBlockParameterName.PENDING).send().balance.toBigDecimal()
                countDown.countDown()
            }
        }
        countDown.await()
        for ((k, v) in map) {
            println("address:$k balance:${Convert.fromWei(v, Convert.Unit.ETHER)}")
        }
    }


}
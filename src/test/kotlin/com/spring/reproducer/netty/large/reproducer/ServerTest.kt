package com.spring.reproducer.netty.large.reproducer

import io.netty.handler.logging.LogLevel.TRACE
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.netty.NettyInbound
import reactor.netty.NettyOutbound
import reactor.netty.tcp.TcpClient
import reactor.netty.tcp.TcpServer
import reactor.netty.transport.Transport
import reactor.netty.transport.logging.AdvancedByteBufFormat.TEXTUAL
import java.lang.Thread.sleep
import kotlin.random.Random.Default.nextInt

internal class ServerTest {

    companion object {
        private const val HOST = "localhost"
        private const val PORT = 0
        private const val THRESHOLD = 2048
        private const val OFFSET = 10
        private val smallMessage = message(THRESHOLD - OFFSET)
        private val longMessage = message(THRESHOLD + OFFSET)
        private fun message(len: Int) =
            (0 until len).map { nextInt(97, 122) }.map { Char(it) }.joinToString("").toByteArray()

        private fun <T : Transport<*, *>> T.enableLogging(name: String) =
            run { wiretap(true).wiretap(name, TRACE, TEXTUAL) as T }
    }

    @Test
    fun `should exchange small message`() {
        assertSendAndReceive(smallMessage)
    }

    @Test
    fun `should exchange long message`() {
        assertSendAndReceive(longMessage)
    }

    private fun assertSendAndReceive(msg: ByteArray) {
        var result: ByteArray = ByteArray(0)
        val server = TcpServer.create()
            .host(HOST)
            .port(PORT)
            .enableLogging("server")
            .handle(::serverHandle)
            .bindNow()
        TcpClient.create()
            .host(server.host())
            .port(server.port())
            .enableLogging("client")
            .handle { i, o ->
                o.sendByteArray(Mono.just(msg))
                    .then(i.receive().retain()
                        .asByteArray()
                        .doOnNext { result += it }
                        .then()
                    )
                    .then()
            }
            .connectNow()
        sleep(1000)
        assertThat(result).isEqualTo(msg)
    }

    private fun serverHandle(req: NettyInbound, resp: NettyOutbound) = resp.sendByteArray(req.handle()).then()

    private fun NettyInbound.handle() = receive().retain().asByteArray()
}
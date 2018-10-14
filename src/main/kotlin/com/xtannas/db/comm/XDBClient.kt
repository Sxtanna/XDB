package com.xtannas.db.comm

import com.sxtanna.korm.Korm
import com.sxtanna.korm.writer.KormWriter
import com.sxtanna.korm.writer.base.Options
import com.xtannas.db.comm.base.XDBCommand
import com.xtannas.db.comm.base.XDBRespond
import com.xtannas.db.data.XDBType
import simplenet.Client
import simplenet.packet.Packet
import java.lang.Exception
import java.util.*

class XDBClient(val host: String, val port: Int) {

    private val korm = Korm(writer = KormWriter(2, Options.none()))
    private val client = Client()


    init {
        korm.pushWith<XDBType.XDBInt> { writer, data ->
            data?.name?.let { writer.writeData(it) }
        }

        korm.pullWith<XDBType> { reader, types ->
            return@pullWith when (types.first().asBase()?.data.toString()) {
                "XDBInt" -> XDBType.XDBInt
                "XDBDec" -> XDBType.XDBDec
                "XDBLet" -> XDBType.XDBLet
                "XDBTxt" -> XDBType.XDBTxt
                "XDBBit" -> XDBType.XDBBit
                else -> null
            }
        }
    }

    fun start() {
        client.connect(host, port)
    }

    fun close() {
        client.close()
    }

    fun send(command: XDBCommand, responseListener: (XDBRespond) -> Unit) {
        wait(command, responseListener)
    }

    fun wait(command: XDBCommand, responseListener: (XDBRespond) -> Unit) {
        Packet.builder().putString(command.name).putString(korm.push(command)).writeAndFlush(client)

        client.readStringAlways {

            val responseUUID = try {
                UUID.fromString(it)
            } catch (ignored: Exception) {
                null
            } ?: return@readStringAlways

            if (responseUUID != command.uuid) return@readStringAlways

            val data = korm.pull(client.readString())

            val response: XDBRespond = when (command.name) {
                "select" -> {
                    data.to<XDBRespond.XDBSelect>()
                }
                "insert" -> {
                    data.to<XDBRespond.XDBInsert>()
                }
                "delete" -> {
                    data.to<XDBRespond.XDBDelete>()
                }
                else -> null
            } ?: return@readStringAlways

            println(response)
            responseListener.invoke(response)
        }
    }

}
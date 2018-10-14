package com.xtannas.db.comm

import com.sun.management.jmx.Trace.send
import com.sxtanna.korm.Korm
import com.xtannas.db.XDB
import com.xtannas.db.comm.base.XDBCommand
import com.xtannas.db.comm.base.XDBRespond
import com.xtannas.db.data.XDBData
import com.xtannas.db.data.XDBType
import simplenet.Client
import simplenet.Server
import simplenet.packet.Packet

class XDBServer(private val port: Int, val xdb: XDB) {

    private val korm = Korm()
    private val server = Server()

    init {
        korm.pushWith<XDBType.XDBInt> { writer, data ->
            data?.name?.let { writer.writeData(it) }
        }

        korm.pullWith<XDBType> { reader, types ->
            return@pullWith when(types.first().asBase()?.data.toString()) {
                "XDBInt" -> XDBType.XDBInt
                "XDBDec" -> XDBType.XDBDec
                "XDBLet" -> XDBType.XDBLet
                "XDBTxt" -> XDBType.XDBTxt
                "XDBBit" -> XDBType.XDBBit
                else -> null
            }
        }

        korm.pullWith<XDBData> { reader, types ->
            if (types.size == 1) { // none
                val text = types.first().asBase()?.data.toString()

                when(text) {
                    "empty" -> XDBData.DEF.EMPTY
                    "null" -> XDBData.DEF.NULL
                    else -> XDBData.DEF.NULL
                }
            }
            else { // some

                val data = types.first().asBase()?.data ?: return@pullWith null
                val type = reader.mapData(types[1].asBase()?: return@pullWith null, XDBType::class) ?: return@pullWith null

                XDBData.Some(data, type)
            }
        }
    }


    fun start() {
        server.bind("localhost", port)

        server.onConnect {
            it.readString { name ->
                val data = korm.pull(it.readString())

                when(name) {
                    "select" -> {
                        val select = data.to<XDBCommand.XDBSelect>() ?: return@readString
                        val result = xdb.select(select.xdbName)

                        send(XDBRespond.XDBSelect(select.uuid, result), it)
                    }
                    "insert" -> {
                        val insert = data.to<XDBCommand.XDBInsert>() ?: return@readString

                        val result = when(xdb.insert(insert.xdbName, insert.data)) {
                            is XDBData.None -> XDBRespond.Response.FAIL
                            is XDBData.Some -> XDBRespond.Response.PASS
                        }

                        println("INSERT $insert RESULT $result")

                        send(XDBRespond.XDBInsert(insert.uuid, result), it)
                    }
                    "delete" -> {
                        val delete = data.to<XDBCommand.XDBDelete>() ?: return@readString
                        val result = when(xdb.delete(delete.xdbName)) {
                            is XDBData.None -> XDBRespond.Response.FAIL
                            else -> XDBRespond.Response.PASS
                        }

                        send(XDBRespond.XDBDelete(delete.uuid, result), it)
                    }
                }
            }
        }

        while (server.channel.isOpen) {  }
    }

    fun close() {
        server.close()
    }


    fun send(response: XDBRespond, client: Client) {
        println(response)
        Packet.builder().putString(response.uuid.toString()).putString(korm.push(response)).writeAndFlush(client)
    }





}
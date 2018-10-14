package com.xtannas.db.comm.base

import com.xtannas.db.data.XDBData
import java.util.*

sealed class XDBRespond {

    abstract val uuid: UUID


    data class XDBSelect(override val uuid: UUID, val data: XDBData) : XDBRespond()

    data class XDBInsert(override val uuid: UUID, val response: Response) : XDBRespond()

    data class XDBDelete(override val uuid: UUID, val response: Response) : XDBRespond()


    enum class Response {

        PASS,
        FAIL;

    }

}
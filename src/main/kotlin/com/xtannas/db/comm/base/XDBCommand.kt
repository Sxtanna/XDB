package com.xtannas.db.comm.base

import com.xtannas.db.base.Named
import com.xtannas.db.data.XDBData
import com.xtannas.db.data.XDBName
import java.util.*

sealed class XDBCommand : Named {

    @Transient
    override val name = this::class.simpleName?.replace("XDB", "")?.toLowerCase() ?: ""

    val uuid = UUID.randomUUID()


    data class XDBSelect(val xdbName: XDBName) : XDBCommand()

    data class XDBInsert(val xdbName: XDBName, val data: XDBData) : XDBCommand()

    data class XDBDelete(val xdbName: XDBName) : XDBCommand()

}
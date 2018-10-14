package com.xtannas.db.main

import com.xtannas.db.data.XDBData
import com.xtannas.db.data.XDBName
import com.xtannas.db.data.XDBType
import com.xtannas.db.impl.file.XDBFileBase
import java.io.File

fun main(args: Array<String>) {

    val live = XDBFileBase(File("/home/camdenorrb/Documents/Programming/IntellijProjects/XDB"))

    println(live.select(XDBName("0:1:2")))
    live.insert(XDBName("0:1:2"), XDBData.Some(23, XDBType.XDBInt))
    println(live.select(XDBName("0:1:2")))
    live.delete(XDBName("0:1:2"))
    println(live.select(XDBName("0:1:2")))
}

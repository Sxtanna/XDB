package com.xtannas.db.data

sealed class XDBData {

    data class None(val text: String) : XDBData()

    data class Some(val data: Any, val type: XDBType) : XDBData()


    object DEF {

        val NULL = None("null")

        val EMPTY = None("empty")

    }

}
package com.xtannas.db.data

import com.xtannas.db.base.Named
import kotlin.reflect.jvm.jvmName

sealed class XDBType : Named {

    override val name by lazy { this::class.simpleName ?: this::class.jvmName }


    abstract fun verify(data: Any)

    abstract fun create(data: Any): Any


    override fun toString(): String {
        return name
    }

    // integer
    object XDBInt : XDBType() {

        override fun verify(data: Any) {
            require(data is Number || (data.toString().toLongOrNull() != null))
        }

        override fun create(data: Any): Any {
            return (data as? Number)?.toLong() ?: (data.toString().toLongOrNull() ?: 0L)
        }

    }

    // decimal
    object XDBDec : XDBType() {

        override fun verify(data: Any) {
            require(data is Number || (data.toString().toDoubleOrNull() != null))
        }

        override fun create(data: Any): Any {
            return (data as? Number)?.toDouble() ?: (data.toString().toDoubleOrNull() ?: 0.0)
        }

    }

    // character
    object XDBLet : XDBType() {

        override fun verify(data: Any) {
            require(data is Char || (data is String && data.length == 1))
        }

        override fun create(data: Any): Any {
            return (data as? Char) ?: (data as? String)?.get(0) ?: ' '
        }

    }

    // string
    object XDBTxt : XDBType() {

        override fun verify(data: Any) {
            require(data is Char || data is String)
        }

        override fun create(data: Any): Any {
            return "${(data as? Char) ?: data.toString()}"
        }

    }

    // boolean
    object XDBBit : XDBType() {

        override fun verify(data: Any) {
            require(data is Boolean)
        }

        override fun create(data: Any): Any {
            return (data as? Boolean) ?: false
        }

    }

}
package com.xtannas.db.impl

import com.xtannas.db.base.XDBBase
import com.xtannas.db.data.XDBData
import com.xtannas.db.data.XDBName

class XDBLiveBaseImpl : XDBBase {

    override val name = "Live"


    private val root = LiveData()


    override fun select(name: XDBName): XDBData {
        if (name.tree.isEmpty()) return root.data

        var live = root
        var data = root.data

        name.tree.forEach {
            live = live.tree[it] ?: return XDBData.DEF.NULL
            data = live.data
        }

        return data
    }

    override fun insert(name: XDBName, data: XDBData): XDBData {
        if (name.tree.isEmpty()) {
            root.data = data
            return data
        }

        var live = root
        name.tree.forEach {
            live = live.into(it)
        }

        live.data = data

        return data
    }

    override fun delete(name: XDBName): XDBData {
        if (name.tree.isEmpty()) {
            root.data = XDBData.DEF.EMPTY
            return root.data
        }

        val last = name.tree.last()
        var live = root

        name.tree.forEach {
            if (it == last) {
                live.tree.remove(it)
            }
            else {
                live = live.tree[it] ?: return XDBData.DEF.NULL
            }
        }

        return XDBData.DEF.EMPTY
    }


    data class LiveData(val tree: MutableMap<String, LiveData> = mutableMapOf()) {

        var data: XDBData = XDBData.DEF.EMPTY


        fun into(name: String): LiveData {
            return tree.computeIfAbsent(name) { LiveData() }
        }

    }

}
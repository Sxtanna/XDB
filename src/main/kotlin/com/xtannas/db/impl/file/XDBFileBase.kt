package com.xtannas.db.impl.file

import com.xtannas.db.base.XDBBase
import com.xtannas.db.data.XDBData
import com.xtannas.db.data.XDBName
import com.xtannas.db.impl.file.base.FileStore
import com.xtannas.db.impl.file.impl.KormFileStore
import com.xtannas.db.impl.live.XDBLiveBase
import java.io.File

class XDBFileBase(val mainDir: File) : XDBBase {

    override val name = "File"

    val cache = XDBLiveBase()

    val store: FileStore = KormFileStore(mainDir)


    override fun select(name: XDBName): XDBData {
        return cache.select(name).takeIf { it !== XDBData.DEF.NULL } ?: store.load(name).apply {
            if (this is XDBData.Some) cache.insert(name, this)
        }
    }

    override fun insert(name: XDBName, data: XDBData): XDBData {
        // TODO: Save in intervals
        store.save(name, data)
        return cache.insert(name, data)
    }

    override fun delete(name: XDBName): XDBData {
        // TODO: Save in intervals
        return cache.delete(name)
    }

}
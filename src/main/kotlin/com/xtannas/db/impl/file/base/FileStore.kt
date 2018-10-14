package com.xtannas.db.impl.file.base

import com.xtannas.db.data.XDBData
import com.xtannas.db.data.XDBName

interface FileStore {

    // TODO: Retrieve from the cache
    fun load(name: XDBName): XDBData

    // TODO: Store into the cache
    fun save(name: XDBName, data: XDBData)

}
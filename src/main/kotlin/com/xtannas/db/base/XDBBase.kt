package com.xtannas.db.base

import com.xtannas.db.data.XDBData
import com.xtannas.db.data.XDBName

interface XDBBase : Named {

    fun select(name: XDBName): XDBData

    fun insert(name: XDBName, data: XDBData): XDBData

    fun delete(name: XDBName): XDBData

}
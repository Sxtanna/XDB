package com.xtannas.db.impl.file.impl

import com.sxtanna.korm.Korm
import com.xtannas.db.data.XDBData
import com.xtannas.db.data.XDBName
import com.xtannas.db.data.XDBType
import com.xtannas.db.impl.file.base.FileStore
import java.io.File

class KormFileStore(val mainDir: File) : FileStore {

    val korm = Korm()


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

    }


    override fun load(name: XDBName): XDBData {

        val file = get(name)

        if (file.exists().not()) {
            return XDBData.DEF.NULL
        }

        return korm.pull(file).to<XDBData.Some>() ?: XDBData.DEF.NULL
    }

    override fun save(name: XDBName, data: XDBData) {
        println(get(name))
        korm.push(data, get(name).apply { createNewFile() })
    }


    operator fun get(name: XDBName): File {
        return File(mainDir, name.tree.joinToString(File.separator)).apply {
            mkdirs()
        }.resolve("data.korm")
    }

}
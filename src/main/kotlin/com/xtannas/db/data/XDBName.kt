package com.xtannas.db.data

data class XDBName(val tree: List<String>) {
    constructor(tree: String): this(tree.split(":"))
    constructor(vararg tree: String): this(tree.toList())
}
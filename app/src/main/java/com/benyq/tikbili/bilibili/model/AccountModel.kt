package com.benyq.tikbili.bilibili.model

data class AccountModel(
    val birthday: String,
    val mid: Long,
    val nick_free: Boolean,
    val rank: String,
    val sex: String,
    val sign: String,
    val uname: String,
    val userid: String
)
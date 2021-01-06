package com.sdzshn3.onesignalapi

fun log(any: Any?) {
    if (BuildConfig.DEBUG) {
        println(any)
    }
}

fun log(msg: String?) {
    if (BuildConfig.DEBUG) {
        println(msg)
    }
}

fun nlog(ignoreLog: Any?) {

}
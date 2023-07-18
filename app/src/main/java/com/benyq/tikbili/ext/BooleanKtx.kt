package com.benyq.tikbili.ext

/**
 *
 * @author benyq
 * @date 7/17/2023
 *
 */


inline fun Boolean?.ifTrue(block: () -> Unit) {
    if (this == true) {
        block()
    }
}

inline fun Boolean?.ifFalse(block: () -> Unit) {
    if (this == false) {
        block()
    }
}

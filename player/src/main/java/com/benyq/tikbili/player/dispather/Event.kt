package com.benyq.tikbili.player.dispather

/**
 *
 * @author benyq
 * @date 4/9/2024
 *
 */
open class Event(val code: Int) {

    private var _dispatcher: EventDispatcher? = null

    fun setDispatcher(dispatcher: EventDispatcher){
        _dispatcher = dispatcher
    }

    fun dispatch() {
        _dispatcher?.dispatchEvent(this)
    }

    open fun recycle() {
        _dispatcher = null
    }

    fun <E> cast(clazz: Class<E>): E {
        return clazz.cast(this)
    }
}
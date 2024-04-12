package com.benyq.tikbili.player.dispather

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.benyq.tikbili.player.PlayerConfig
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList

/**
 *
 * @author benyq
 * @date 4/9/2024
 * 事件分发
 */
class EventDispatcher(looper: Looper) {

    interface EventListener {
        fun onEvent(event: Event)
    }

    private val _subscribers = CopyOnWriteArrayList<EventListener>()
    private val handler = H(looper, this)

    fun <T : Event> obtain(clazz: Class<T>): T {
        val event = if (PlayerConfig.EVENT_POOL_ENABLE) Pool.acquire(clazz) else Factory.create(clazz)
        event.setDispatcher(this)
        return event
    }

    fun addSubscriber(subscriber: EventListener) {
        _subscribers.addIfAbsent(subscriber)
    }

    fun removeSubscriber(subscriber: EventListener?) {
        subscriber?.let { _subscribers.remove(it) }
    }

    fun removeAllListeners() {
        _subscribers.clear()
    }

    fun release() {
        handler.post {
            handler.removeCallbacksAndMessages(null)
            _subscribers.clear()
        }
    }

    fun dispatchEvent(event: Event) {
        if (Thread.currentThread() != handler.looper.thread) {
            handler.obtainMessage(0, event).sendToTarget()
        }else {
            dispatch(event)
        }
    }

    private fun dispatch(event: Event) {
        _subscribers.forEach {
            it.onEvent(event)
        }
    }


    class H(looper: Looper, dispatcher: EventDispatcher) : Handler(looper) {

        private val _ref = WeakReference(dispatcher)

        override fun dispatchMessage(msg: Message) {
            val dispatcher = _ref.get() ?: return
            if (msg.what == 0) {
                dispatcher.dispatch(msg.obj as Event)
                return
            }
            throw IllegalArgumentException()
        }
    }
}
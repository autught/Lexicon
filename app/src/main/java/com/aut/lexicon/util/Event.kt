package com.aut.lexicon.util

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 * 作为公开的数据的包装，提供一个事件
 *
 * For more information, see:
 * https://medium.com/google-developers/livedata-with-events-ac2622673150
 */
class Event<out T>(private val content: T) {
    // Allow external read but not write允许外部读但不允许写
    private var hasBeenHandled = false

    /**
     * Returns the content and prevents its use again.
     * 返回内容并防止其再次使用。
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}
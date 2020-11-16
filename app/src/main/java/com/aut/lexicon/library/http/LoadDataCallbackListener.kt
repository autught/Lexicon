package com.snow.sharker.http

/**
 * @description:
 * @author:  79120
 * @date :   2020/9/3 16:30
 */
interface LoadDataCallbackListener<T> {
    fun onSuccess(t: T?)
    fun onFailure()
}
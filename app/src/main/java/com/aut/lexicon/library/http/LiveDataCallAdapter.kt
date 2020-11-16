package com.snow.sharker.http

import androidx.lifecycle.LiveData
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean


/**
 * @description:
 * @author:  79120
 * @date :   2020/9/2 14:02
 */
class LiveDataCallAdapter<T>(private val responseType: Type) : CallAdapter<T, LiveData<T>> {

    override fun adapt(call: Call<T>) = object : LiveData<T>() {

        override fun onActive() {
            if (AtomicBoolean(false).compareAndSet(false, true)) {
                call.enqueue(object : Callback<T> {
                    override fun onFailure(call: Call<T>, t: Throwable) {
                        postValue(null)
                    }

                    override fun onResponse(call: Call<T>, response: Response<T>) {
                        postValue(response.body())
                    }
                })
            }
        }
    }

    override fun responseType() = responseType

}
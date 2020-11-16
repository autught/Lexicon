package com.snow.sharker.http

import androidx.lifecycle.LiveData
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @description:
 * @author:  79120
 * @date :   2020/9/2 14:36
 */
class LiveDataCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != LiveData::class.java) {
            return null
        }
        val observableType = getParameterUpperBound(0, returnType as ParameterizedType)
        if (getRawType(observableType) != ApiEntity::class.java) {
            throw IllegalArgumentException("type must be ApiEntity")
        }
        if (observableType !is ParameterizedType) {
            throw IllegalArgumentException("resource must be ParameterizedType")
        }
        return LiveDataCallAdapter<Any>(observableType)
    }
}
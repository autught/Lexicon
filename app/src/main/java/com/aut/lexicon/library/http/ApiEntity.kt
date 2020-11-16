package com.snow.sharker.http

/**
 * @description:
 * @author:  79120
 * @date :   2020/9/2 14:19
 */
class ApiEntity<T> {
    /**
     * 状态码
     */
     var code: Int

    /**
     * 信息
     */
    var msg: String

    /**
     * 数据
     */
    var result: T?

    constructor(code: Int, msg: String) {
        this.code = code
        this.msg = msg
        result = null
    }

    constructor(code: Int, msg: String, result: T) {
        this.code = code
        this.msg = msg
        this.result = result
    }

    companion object {
        /**
         * 微信登陆未绑定手机号
         */
        const val CODE_WX_BIND = 999

        /**
         * 请求成功状态码
         */
        const val CODE_SUCCESS = 1000

        /**
         * 请求出错
         */
        const val CODE_ERROR = 1001
    }
}
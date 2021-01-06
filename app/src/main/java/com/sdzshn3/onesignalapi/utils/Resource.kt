package com.sdzshn3.onesignalapi.utils

data class Resource<out T>(val status: Status, val data: T?, val message: String?, val isNotificationPreview: Boolean?) {
    companion object {
        fun <T> success(data: T?, isNotificationPreview: Boolean?): Resource<T> {
            return Resource(Status.SUCCESS, data, null, isNotificationPreview)
        }

        fun <T> error(msg: String, data: T?, isNotificationPreview: Boolean?): Resource<T> {
            return Resource(Status.ERROR, data, msg, isNotificationPreview)
        }

        fun <T> loading(data: T?, isNotificationPreview: Boolean?): Resource<T> {
            return Resource(Status.LOADING, data, null, isNotificationPreview)
        }
    }
}
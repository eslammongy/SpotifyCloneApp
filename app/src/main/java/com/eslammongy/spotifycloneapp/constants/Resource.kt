package com.eslammongy.spotifycloneapp.constants

data class Resource<out T>(val stats:Status , val data:T? , val message:String?) {

    companion object{
        fun <T> success(data:T?) = Resource(Status.SUCCESS , data , null)
        fun <T> error(message: String? , data:T?) = Resource(Status.ERROR , data , message)
        fun <T> loading(data: T?) = Resource(Status.LOADING , data , null)
    }
}

enum class Status{
    SUCCESS,
    ERROR,
    LOADING
}
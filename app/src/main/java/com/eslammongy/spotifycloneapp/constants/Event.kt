package com.eslammongy.spotifycloneapp.constants

open class Event<out T> (private val data: T){

    var hasBeenHandled = false
    private set

    fun getContentInfoNotHandled():T?{
        return if (hasBeenHandled){
            null
        }else{
            hasBeenHandled = true
            data
        }
    }

    fun peekContent() = data

}
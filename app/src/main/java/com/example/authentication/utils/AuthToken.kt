package com.example.authentication.utils

import android.content.Context
import android.content.SharedPreferences

class AuthToken private constructor(private val context:Context){
    companion object{
        private const val TOKEN = "TOKEN"
        private const val TOKEN_VALUE = "TOKEN+VALUE"

        private var instance : AuthToken ?= null
        private fun getInstance(context: Context):AuthToken  = Companion.instance?: synchronized(this){
            AuthToken(context).apply {instance = this }
        }
    }

    private val sharedPreferences:SharedPreferences = context.getSharedPreferences(TOKEN, Context.MODE_PRIVATE)

    val token:String? = null
        set(value) = sharedPreferences.edit().putString(TOKEN_VALUE,value).apply().also { field = value }
        get() = field?: sharedPreferences.getString(TOKEN_VALUE,null)
}
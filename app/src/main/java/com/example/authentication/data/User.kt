package com.example.authentication.data

import com.google.gson.annotations.SerializedName

data class User(@SerializedName("id") val id:String , val fullName :String , val email:String)

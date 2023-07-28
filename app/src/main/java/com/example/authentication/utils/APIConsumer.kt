package com.example.authentication.utils

import com.example.authentication.data.RegisterBody
import com.example.authentication.data.AuthResponse
import com.example.authentication.data.LoginBody
import com.example.authentication.data.UniqueEmailValidationResponse
import com.example.authentication.data.ValidateEmailBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface APIConsumer {

    @POST("users/validate-unique-email")
    suspend fun validateEmailAddress(@Body body : ValidateEmailBody) :Response<UniqueEmailValidationResponse>

    @POST("users/register")
    suspend fun registerUser(@Body body: RegisterBody) : Response<AuthResponse>

    @POST("users/login")
    suspend fun loginUser(@Body body: LoginBody) : Response<AuthResponse>

}

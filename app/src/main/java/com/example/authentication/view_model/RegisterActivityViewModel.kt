package com.example.authentication.view_model

import android.app.Application
import android.view.View
import androidx.lifecycle.ViewModel
import com.example.authentication.repository.AuthRepository

class RegisterActivityViewModel(val authRepository: AuthRepository, val application: Application):ViewModel() {
}
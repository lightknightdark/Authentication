package com.example.authentication.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.authentication.R
import com.example.authentication.data.LoginBody
import com.example.authentication.databinding.ActivityLoginBinding
import com.example.authentication.repository.AuthRepository
import com.example.authentication.utils.APIService
import com.example.authentication.utils.VibrateView
import com.example.authentication.view_model.LoginActivityViewModel
import com.example.authentication.view_model.LoginActivityViewModelFactory

class LoginActivity : AppCompatActivity(),View.OnClickListener,View.OnFocusChangeListener,View.OnKeyListener {
    private lateinit var mBinding:ActivityLoginBinding
    private lateinit var mViewModel: LoginActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityLoginBinding.inflate(LayoutInflater.from(this))
        mBinding.LoginWithGoogleBtn.setOnClickListener(this)
        mBinding.loginBtn.setOnClickListener(this)
        mBinding.registerBtn.setOnClickListener(this)
        mBinding.emailEt.onFocusChangeListener = this
        mBinding.passwordEt.onFocusChangeListener = this
        mBinding.passwordEt.setOnKeyListener(this)

        mViewModel = ViewModelProvider(this,
            LoginActivityViewModelFactory(AuthRepository(APIService.getService()),application)
        ).get(LoginActivityViewModel::class.java)

        setContentView(mBinding.root)
    }

    private fun validateEmail(shouldUpdateView: Boolean = true,shouldVibrateView: Boolean = true):Boolean{
        var errorMessage :String? = null
        val value:String = mBinding.emailEt.text.toString()
        if(value.isEmpty()){
            errorMessage = "Email is required"
        }else if (Patterns.EMAIL_ADDRESS.matcher(value).matches()){
            errorMessage = "Email is not validate"
        }


        if(errorMessage!=null && shouldUpdateView){
            mBinding.emailTil.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrateView) VibrateView.vibrate(this@LoginActivity,this)
            }
        }

        return errorMessage == null

    }

    private fun  validatePassword(shouldUpdateView: Boolean = true,shouldVibrateView: Boolean = true):Boolean{
        var errorMessage :String? = null
        val value:String = mBinding.passwordEt.text.toString()
        if(value.isEmpty()){
            errorMessage = "Password is required"
        }else if (value.length < 6){
            errorMessage = "Password must be 6 characters long"
        }

        if(errorMessage!=null && shouldUpdateView){
            mBinding.passwordTil.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrateView) VibrateView.vibrate(this@LoginActivity,this)
            }
        }


        return errorMessage == null
    }


    private fun validate():Boolean{
        var isValid  = true
        if(!validateEmail(shouldVibrateView = false)) isValid = false
        if(!validatePassword(shouldVibrateView = false)) isValid = false
        if(!isValid) VibrateView.vibrate(this,mBinding.cardView)

        return isValid
    }

    override fun onClick(view: View?) {
        if(view != null){
            when(view.id){
                R.id.loginBtn ->{submitForm()}
                R.id.registerBtn -> {startActivity(Intent(this,RegisterActivity::class.java))}
            }
        }
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if(view!= null){
            when(view.id){
                R.id.emailEt ->{
                    if(hasFocus){
                        if(mBinding.emailTil.isErrorEnabled){
                            mBinding.emailTil.isErrorEnabled = false
                        }

                    }else{
                        validateEmail()

                    }
                }
                R.id.passwordEt ->{
                    if(hasFocus){
                        if(mBinding.passwordTil.isErrorEnabled){
                            mBinding.passwordTil.isErrorEnabled = false
                        }

                    }else{
                   validatePassword()
                        }
                    }
                }
            }

        }

    private fun submitForm(){
        if(validate()){
            mViewModel.loginUser(LoginBody(mBinding.emailEt.text!!.toString(), mBinding.passwordEt.text!!.toString()))
        }
    }

    override fun onKey(view: View?, event: Int, keyEvent: KeyEvent?): Boolean {
        if(android.view.KeyEvent.KEYCODE_ENTER == keyEvent!!.keyCode && keyEvent.action == android.view.KeyEvent.ACTION_UP){
            submitForm()
        }
        return false
    }
}


}
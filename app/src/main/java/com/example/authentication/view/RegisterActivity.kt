package com.example.authentication.view

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.authentication.R
import com.example.authentication.data.RegisterBody
import com.example.authentication.data.ValidateEmailBody
import com.example.authentication.databinding.ActivityRegisterBinding
import com.example.authentication.repository.AuthRepository
import com.example.authentication.utils.APIService
import com.example.authentication.utils.VibrateView
import com.example.authentication.view_model.RegisterActivityViewModel
import com.example.authentication.view_model.RegisterActivityViewModelFactory
import java.lang.StringBuilder

class RegisterActivity : AppCompatActivity(), View.OnClickListener,View.OnFocusChangeListener,View.OnKeyListener,TextWatcher {

    private lateinit var mBinding: ActivityRegisterBinding
    private lateinit var mViewModel: RegisterActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRegisterBinding.inflate(LayoutInflater.from(this))
        setContentView(mBinding.root)
        mBinding.fullNameEt.onFocusChangeListener = this
        mBinding.emailEt.onFocusChangeListener = this
        mBinding.passwordEt.onFocusChangeListener = this
        mBinding.cPasswordEt.onFocusChangeListener = this
        mBinding.cPasswordEt.setOnKeyListener(this)
        mBinding.cPasswordEt.addTextChangedListener(this)
        mBinding.registerBtn.setOnClickListener(this)
        mViewModel = ViewModelProvider(this,RegisterActivityViewModelFactory(AuthRepository(APIService.getService()),application)).get(RegisterActivityViewModel::class.java)
        setupObservers()
    }

    private fun setupObservers() {
        mViewModel.getIsLoading().observe(this){
          mBinding.progressBar.isVisible = it

        }

        mViewModel.getIsUnique().observe(this){
            if(validateEmail(shouldUpdateView = false)){
                if(it){
                    mBinding.emailTil.apply {
                        if(isErrorEnabled) isErrorEnabled = false
                        setStartIconDrawable(R.drawable.baseline_check_circle_24)
                        setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
                    }
                }else{
                    mBinding.emailTil.apply {
                        if(startIconDrawable != null) startIconDrawable = null
                        isErrorEnabled = true
                        error = "Email is already taken"
                    }
                }
            }

        }

        mViewModel.getErrorMessage().observe(this){
            val formErrorKeys = arrayOf("fullName","email","password")
            val message = StringBuilder()
            it.map { entry ->
                if(formErrorKeys.contains(entry.key)){
                    when(entry.key){
                        "fullName" ->{
                            mBinding.fullNameTil.apply {
                                isErrorEnabled = true
                                error = entry.key
                            }

                        }

                        "email" ->{
                            mBinding.emailTil.apply {
                                isErrorEnabled = true
                                error = entry.key

                        }

                            }
                        "password" ->{
                            mBinding.passwordTil.apply {
                                isErrorEnabled = true
                                error = entry.key

                            }

                        }
                    }
                }else{
                    message.append(entry.value).append("\n")
                }

                if(message.isNotEmpty()){
                    AlertDialog.Builder(this)
                        .setIcon(R.drawable.info_24)
                        .setTitle("INFORMATION")
                        .setMessage(message)
                        .setPositiveButton("OK"){dialog,_ -> dialog!!.dismiss()}
                        .show()
                }
            }

        }

        mViewModel.getUser().observe(this){

            if(it != null){
                startActivity(Intent(this,HomeActivity::class.java))
            }
        }
    }

    private fun validateFullName( shouldVibrateView: Boolean = true):Boolean{
        var errorMessage :String? = null
        val value:String = mBinding.fullNameEt.text.toString()
        if(value.isEmpty()){
            errorMessage = "Full Name is required"
        }

        if(errorMessage!=null){
            mBinding.fullNameTil.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrateView) VibrateView.vibrate(this@RegisterActivity,this)
            }
        }
        return errorMessage == null
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
                if (shouldVibrateView) VibrateView.vibrate(this@RegisterActivity,this)
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
                if (shouldVibrateView) VibrateView.vibrate(this@RegisterActivity,this)
            }
        }


        return errorMessage == null
    }


    private fun validateConfirmPassword(shouldUpdateView: Boolean = true,shouldVibrateView: Boolean = true):Boolean{
        var errorMessage :String? = null
        val value:String = mBinding.cPasswordEt.text.toString()
        if(value.isEmpty()){
            errorMessage = "Confirm Password is required"
        }else if (value.length < 6){
            errorMessage = "Confirm Password must be 6 characters long"
        }

        if(errorMessage!=null && shouldUpdateView){
            mBinding.cPasswordTil.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrateView) VibrateView.vibrate(this@RegisterActivity,this)
            }
        }



        return errorMessage == null
    }

    private fun validatePasswordAndConfirmPassword(shouldUpdateView: Boolean = true,shouldVibrateView: Boolean = true):Boolean{
        var errorMessage :String? = null
        val password = mBinding.passwordEt.text.toString()
        val confirmPassowrd = mBinding.cPasswordEt.text.toString()
        if (password != confirmPassowrd){
            errorMessage = "confirm password does not match with password"
        }

        if(errorMessage!=null && shouldUpdateView){
            mBinding.cPasswordTil.apply {
                isErrorEnabled = true
                error = errorMessage
                if (shouldVibrateView) VibrateView.vibrate(this@RegisterActivity,this)
            }
        }

        return errorMessage == null

    }

    override fun onClick(view: View?) {
        if(view != null && view.id == R.id.registerBtn){
            onSubmit()
        }
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if(view!= null){
            when(view.id){
                R.id.fullNameEt ->{
                    if(hasFocus){
                        if(mBinding.fullNameTil.isErrorEnabled){
                            mBinding.fullNameTil.isErrorEnabled = false
                        }

                    }else{
                        validateFullName()
                    }
                }
                R.id.emailEt ->{
                    if(hasFocus){
                        if(mBinding.emailTil.isErrorEnabled){
                            mBinding.emailTil.isErrorEnabled = false
                        }

                    }else{
                        if(validateEmail()){
                            mViewModel.validateEmailAddress(ValidateEmailBody(mBinding.emailEt.text!!.toString()))
                        }

                    }
                }
                R.id.passwordEt ->{
                    if(hasFocus){
                        if(mBinding.passwordTil.isErrorEnabled){
                            mBinding.passwordTil.isErrorEnabled = false
                        }

                    }else{
                        if(validatePassword()  && mBinding.cPasswordEt.text!!.isNotEmpty() && validateConfirmPassword() && validatePasswordAndConfirmPassword()) {
                        if(mBinding.cPasswordTil.isErrorEnabled) {
                            mBinding.cPasswordTil.isErrorEnabled = false
                        }
                            mBinding.cPasswordTil.apply {
                                setStartIconDrawable(R.drawable.baseline_check_circle_24)
                                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
                            }
                        }
                        }
                    }
                }
            R.id.cPasswordEt -> run {
                if (hasFocus) {
                    if (mBinding.cPasswordTil.isErrorEnabled) {
                        mBinding.cPasswordTil.isErrorEnabled = false

                    } else {
                        if(validatePassword()  &&  validateConfirmPassword() && validatePasswordAndConfirmPassword()) {
                            if(mBinding.passwordTil.isErrorEnabled) {
                                mBinding.passwordTil.isErrorEnabled = false
                            }
                            mBinding.cPasswordTil.apply {
                                setStartIconDrawable(R.drawable.baseline_check_circle_24)
                                setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onKey(view: View?, event: Int, keyEvent: KeyEvent?): Boolean {
        if(KeyEvent.KEYCODE_ENTER == keyEvent!!.keyCode && keyEvent.action == KeyEvent.ACTION_UP){
            onSubmit()
        }
     return false
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if(validatePassword( shouldUpdateView = false) && validateConfirmPassword(shouldUpdateView = false) && validatePasswordAndConfirmPassword(shouldUpdateView = false)){
        mBinding.cPasswordTil.apply {
            if(isErrorEnabled) isErrorEnabled = false
            setStartIconDrawable(R.drawable.baseline_check_circle_24)
            setStartIconTintList(ColorStateList.valueOf(Color.GREEN))
        }
    }else {
        if(mBinding.cPasswordTil.startIconDrawable != null) mBinding.cPasswordTil.startIconDrawable = null
    }

    }

    override fun afterTextChanged(p0: Editable?) {

    }

    private fun onSubmit(){
        if (validate()){
            mViewModel.registerUser(RegisterBody(mBinding.fullNameEt.text!!.toString(),
                mBinding.emailEt.text!!.toString(),
                mBinding.passwordEt.text!!.toString()))
        }

    }

    private fun validate():Boolean{
        var isValid  = true
        if(!validateFullName(shouldVibrateView = false)) isValid = false
        if(!validateEmail(shouldVibrateView = false)) isValid = false
        if(!validatePassword(shouldVibrateView = false)) isValid = false
        if(!validateConfirmPassword(shouldVibrateView = false)) isValid =  false
        if(isValid && !validatePasswordAndConfirmPassword(shouldVibrateView = false)) isValid = false

        if(!isValid) VibrateView.vibrate(this,mBinding.cardView)

        return isValid
    }
}
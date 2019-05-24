package com.uncc.yfrempon.recreatingmessengerbasic.Login_Register

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_login.*
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.uncc.yfrempon.recreatingmessengerbasic.R
import com.uncc.yfrempon.recreatingmessengerbasic.messages.PrimaryScreenActivity

class LoginActivity: AppCompatActivity() {
    private fun loginAction()
    {
        val email = emailTextField.text.toString()
        val password = passwordTextField.text.toString()
        Log.d("LoginActivity", "Login Button Clicked")
        Log.d("LoginActivity", "Email is " + email)
        Log.d("LoginActivity", "Password is $password")

        if(email.isEmpty() || password.isEmpty())
        {
            textFieldAlert()
        }
        else
        {
            //****************************Create User in Firebase*****************************************************
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) {
                        loginAlert()
                        return@addOnCompleteListener
                    }
                    Log.d("LoginActivity", "Successfully created user with uid: ${it.result?.user?.uid}")

                    //go to primary screen and get rid of all the previous screens(login & register)
                    val intent = Intent(this, PrimaryScreenActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            //*******************************************************************************************************
        }
    }
    fun textFieldAlert()
    {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Oops")
        builder.setMessage("Make sure all the fields are complete.")
        builder.setPositiveButton("Ok", { dialogInterface: DialogInterface, i: Int -> })
        builder.show()
    }
    fun loginAlert()
    {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Oops")
        builder.setMessage("Failed to login.")
        builder.setPositiveButton("ok", { dialogInterface: DialogInterface, i: Int -> })
        builder.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton.setOnClickListener {
            loginAction()
        }
    }
}
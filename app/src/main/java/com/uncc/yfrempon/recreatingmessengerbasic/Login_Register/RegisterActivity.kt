package com.uncc.yfrempon.recreatingmessengerbasic.Login_Register

import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*
import android.support.v7.app.AlertDialog
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import android.app.Activity
import com.google.firebase.database.FirebaseDatabase
import com.uncc.yfrempon.recreatingmessengerbasic.R
import com.uncc.yfrempon.recreatingmessengerbasic.messages.PrimaryScreenActivity
import com.uncc.yfrempon.recreatingmessengerbasic.models.User

class RegisterActivity : AppCompatActivity() {

    var selectedPhotoURI: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        registerButton.setOnClickListener{
            registerAction()
        }
        alreadyHaveAnAccountButton.setOnClickListener {
            //log debug message
            Log.d("RegisterActivity", "go to login screen")

            //go to login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }
        selectPhotoButton.setOnClickListener {
            Log.d("RegisterActivity", "select photo button was clicked")
            val intent = Intent()
            intent.type = "image/*"                         //expected an image
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "SELECT PICTURE"), 1234)   //attempt to get image

        }
    }
    private fun registerAction()
    {
        val username = usernameTextField.text.toString()
        val email = emailTextField.text.toString()
        val password = passwordTextField.text.toString()

        //log debug message
        Log.d("RegisterActivity", "Username is " + username)
        Log.d("RegisterActivity", "Email is " + email)
        Log.d("RegisterActivity", "Password is $password")
        if(username.isEmpty() || email.isEmpty() || password.isEmpty())
        {
            textFieldAlert()
        }
        else if(password.length < 7)
        {
            passwordAlert()
        }
        else if(selectedPhotoURI == null)
        {
            photoAlert()
        }
        else
        {
            //****************************Create User in Firebase*****************************************************
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) {
                        registerAlert()
                        return@addOnCompleteListener
                    }
                    Log.d("RegisterActivity", "Successfully created user with uid: ${it.result?.user?.uid}")
                    uploadImageToFirebaseStorage()
                }
            //*******************************************************************************************************
        }
    }
    fun textFieldAlert()
    {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Oops")
        builder.setMessage("Make sure all the fields are complete.")
        builder.setPositiveButton("Ok", {dialogInterface: DialogInterface, i: Int -> })
        builder.show()
    }
    fun passwordAlert()
    {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Oops")
        builder.setMessage("Password must be 7 or more characters long.")
        builder.setPositiveButton("ok", {dialogInterface: DialogInterface, i: Int -> })
        builder.show()
    }
    fun photoAlert()
    {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Oops")
        builder.setMessage("Please select a profile picture.")
        builder.setPositiveButton("ok", {dialogInterface: DialogInterface, i: Int -> })
        builder.show()
    }
    fun registerAlert()
    {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Oops")
        builder.setMessage("Failed to register account.")
        builder.setPositiveButton("ok", {dialogInterface: DialogInterface, i: Int -> })
        builder.show()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1234 && resultCode == Activity.RESULT_OK && data != null && data.data != null)
        {
            Log.d("RegisterActivity", "Photo was selected")
            selectedPhotoURI = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoURI)
            imageviewRing.setImageBitmap(bitmap)    //circular image
            selectPhotoButton.alpha = 0f //make invisible
        }

    }

    private fun uploadImageToFirebaseStorage()
    {
        if(selectedPhotoURI == null)
        {
            return
        }
        val filename = UUID.randomUUID().toString()// generate unique random string for file name
        val ref = FirebaseStorage.getInstance().getReference("/recreate_messenger_Kotlin_images/$filename") //reference to Firebase Storage Folder

        ref.putFile(selectedPhotoURI!!)
            .addOnSuccessListener{
                Log.d("RegisterActivity", "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("RegisterActivity", "File Location: $it")//print profile image url. Will be stored in the database
                    saveUserData(it.toString())
                }
                    .addOnFailureListener {
                        Log.d("RegisterActivity", "Failed to download image url to store in Firebase")
                    }
            }
            .addOnFailureListener {
                Log.d("RegisterActivity", "Failed to uploaded image")
            }

    }
    private fun saveUserData(imageURL: String)
    {
        val uid = FirebaseAuth.getInstance().uid ?:""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")    //creates and stores users in this path
        val user = User(
            uid,
            usernameTextField.text.toString(),
            imageURL
        ) //create User object
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Saved user data to Firebase")

                //go to primary screen and get rid of all the previous screens(login & register)
                val intent = Intent(this, PrimaryScreenActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("RegisterActivity", "Failed to save user data to Firebase")
            }
    }
}


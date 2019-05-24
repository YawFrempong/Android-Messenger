package com.uncc.yfrempon.recreatingmessengerbasic.messages

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import android.util.Log
import com.squareup.picasso.Picasso
import com.uncc.yfrempon.recreatingmessengerbasic.R
import com.uncc.yfrempon.recreatingmessengerbasic.models.User
import kotlinx.android.synthetic.main.cell.view.*
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.uncc.yfrempon.recreatingmessengerbasic.cell.UserItem

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        supportActionBar?.title = "Select User"
        getUsers()
    }
    companion object
    {
        val USER_KEY = "USER_KEY"
    }
    private fun getUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {             //for any change in data refresh recycle view

            override fun onDataChange(p0: DataSnapshot) {                           //with new data. Snapshot is list of all users

                val adapter = GroupAdapter<ViewHolder>()
                p0.children.forEach{
                    Log.d("NewMessageActivity", it.toString())
                    val user = it.getValue(User::class.java)
                    if(user != null && user.uid != FirebaseAuth.getInstance().uid) {    //don't display yourself in contacts
                        adapter.add(UserItem(user))
                    }
                }
                adapter.setOnItemClickListener{ item, view ->
                    val userItem = item as UserItem

                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY, userItem.user) //sends user object to the next activity. Key is needed
                    startActivity(intent)                             //to retrieve data on other activity
                    finish()
                }
                messageRecycleView.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}


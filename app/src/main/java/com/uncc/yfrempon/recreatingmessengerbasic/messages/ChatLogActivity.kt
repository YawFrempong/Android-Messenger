package com.uncc.yfrempon.recreatingmessengerbasic.messages

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.uncc.yfrempon.recreatingmessengerbasic.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import com.uncc.yfrempon.recreatingmessengerbasic.models.User
import kotlinx.android.synthetic.main.chat_row.view.*
import kotlinx.android.synthetic.main.chat_row_2.view.*
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import java.sql.Timestamp
import android.text.Editable
import android.text.TextWatcher
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.squareup.picasso.Picasso
import android.view.inputmethod.InputMethodManager
import com.uncc.yfrempon.recreatingmessengerbasic.cell.ChatFromItem
import com.uncc.yfrempon.recreatingmessengerbasic.cell.ChatMessage
import com.uncc.yfrempon.recreatingmessengerbasic.cell.ChatToItem


class ChatLogActivity : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()
    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        chatLogRecycleView.adapter = adapter    //allows objects to be added to the adapter & refreshes the list

        //gets the username that was sent from other activity

        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        if(toUser != null){
            supportActionBar?.title = toUser?.username
        }
        listenForMessages()

        messageTextField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (messageTextField.text.toString() == "")
                {
                    sendButton.setBackgroundColor(Color.GRAY)
                    Log.d("ChatLogActivity", "message field is blank")
                }
                else
                {
                    sendButton.setBackgroundColor(Color.rgb(0,153,204))
                    sendButton.setOnClickListener {
                        sendMessage()
                    }
                }
            }

        })
    }



    private fun sendMessage() {

        val text = messageTextField.text.toString()
        val fromID = FirebaseAuth.getInstance().uid  //ID of the logged in user
        if(fromID == null)
        {
            return
        }
        val toID = toUser?.uid

        //each user now have a list of all the messages sent to/from them
        val fromRef = FirebaseDatabase.getInstance().getReference("/user-messages/$fromID/$toID").push()
        //you need to do the reverse so the other user can see the mirrored info
        val toRef = FirebaseDatabase.getInstance().getReference("/user-messages/$toID/$fromID").push()

        val chatMessage = ChatMessage(fromRef.key!!, text, fromID!!, toID!! , System.currentTimeMillis() / 1000)
        fromRef.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d("ChatLogActivity", "Successfully stored message to firebase")
                messageTextField.text.clear()
                //scroll to the last message
                chatLogRecycleView.scrollToPosition(adapter.itemCount - 1)
            }
            .addOnFailureListener {
                Log.d("ChatLogActivity", "Failed to store message to firebase")
            }
        toRef.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d("ChatLogActivity", "Successfully stored message to firebase")
            }
            .addOnFailureListener {
                Log.d("ChatLogActivity", "Failed to store message to firebase")
            }
        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromID/$toID")
        latestMessageRef.setValue(chatMessage)

        val latestMessageRefMirror = FirebaseDatabase.getInstance().getReference("/latest-messages/$toID/$fromID")
        latestMessageRefMirror.setValue(chatMessage)
    }
    private fun listenForMessages() {
        val fromID = FirebaseAuth.getInstance().uid  //ID of the logged in user
        if(fromID == null)
        {
            return
        }
        val toID = toUser?.uid
        //listen for changes in the node where current user has his list of messages stored
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromID/$toID")

        ref.addChildEventListener(object: ChildEventListener { //listener for all data add/remove/changed under the "message" branch in Firebase
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if(chatMessage != null)
                {
                    Log.d("ChatLogActivity", chatMessage?.text)
                    if(chatMessage.fromID == FirebaseAuth.getInstance().uid && chatMessage.toID == toUser?.uid) //did I send the message?
                    {
                        Log.d("ChatLogActivity", "Message from me")
                        val currentUser = PrimaryScreenActivity.currentUser
                        if(currentUser != null) {
                            adapter.add(ChatFromItem(chatMessage.text, currentUser!!)) //adds new message
                        }
                    }
                    if(chatMessage.fromID == toUser?.uid && chatMessage.toID == FirebaseAuth.getInstance().uid)
                    {
                        Log.d("ChatLogActivity", "Message to me")
                        if(toUser != null) {
                            adapter.add(ChatToItem(chatMessage.text, toUser!!))
                        }
                    }
                }
                chatLogRecycleView.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }
        })
    }
}

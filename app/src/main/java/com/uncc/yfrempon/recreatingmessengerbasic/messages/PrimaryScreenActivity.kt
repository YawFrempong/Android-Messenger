package com.uncc.yfrempon.recreatingmessengerbasic.messages

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import com.uncc.yfrempon.recreatingmessengerbasic.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.uncc.yfrempon.recreatingmessengerbasic.R
import com.uncc.yfrempon.recreatingmessengerbasic.Login_Register.RegisterActivity
import kotlinx.android.synthetic.main.activity_primary_screen.*
import android.util.Log
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.Item
import com.google.firebase.database.ChildEventListener
import com.squareup.picasso.Picasso
import com.uncc.yfrempon.recreatingmessengerbasic.cell.primaryRecyclerCell
import kotlinx.android.synthetic.main.primary_cell.view.*
import com.uncc.yfrempon.recreatingmessengerbasic.cell.ChatFromItem
import com.uncc.yfrempon.recreatingmessengerbasic.cell.ChatMessage
import com.uncc.yfrempon.recreatingmessengerbasic.cell.ChatToItem
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.primary_cell.*

class PrimaryScreenActivity : AppCompatActivity() {

    companion object{
        var currentUser: User? = null
    }
    val adapter = GroupAdapter<ViewHolder>()
    val latestMessagesHashMap = HashMap<String, ChatMessage>()          //dictionary

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_primary_screen)
        supportActionBar?.title = "Messages"
        verifyLoginStatus()
        getCurrentUser()
        listenForLatestMessages()
        primaryRecycleView.adapter = adapter
        primaryRecycleView.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        adapter.setOnItemClickListener{ item, view ->
            val intent = Intent(this, ChatLogActivity::class.java)      //what cell was clicked?
            val cell = item as primaryRecyclerCell                  //cast item as a primaryCell
            intent.putExtra(NewMessageActivity.USER_KEY,cell.partner)               //send the cell to new activity
            startActivity(intent)
        }
        newMessageButton.setOnClickListener {
            newMessageAction()
        }
    }
    private fun refreshRecyclerView() {
        adapter.clear()
        //Deletes all the old cells then looks in the Hashmap and loads the values in there into the recycler view
        latestMessagesHashMap.values.forEach {
            adapter.add(primaryRecyclerCell(it))
        }
    }
    private fun listenForLatestMessages(){
        val fromID = FirebaseAuth.getInstance().uid
        //listen for changes in the latest messages node in my branch
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromID")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?)
            {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                latestMessagesHashMap[p0.key!!] = chatMessage!!                     //looks at each toID node & reloads the data
                refreshRecyclerView()
            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?)
            {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                latestMessagesHashMap[p0.key!!] = chatMessage!!
                refreshRecyclerView()
            }
            override fun onChildRemoved(p0: DataSnapshot)
            {

            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?)
            {

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
    private fun getCurrentUser()
    {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                Log.d("PrimaryScreenActivity", "Current user ${currentUser?.imageURL}")
            }
            override fun onCancelled(p0: DatabaseError) {

            }


        })
    }
    private fun verifyLoginStatus()
    {
        //check if user is logged in
        val uid = FirebaseAuth.getInstance().uid
        if(uid == null) {
            //go to primary screen and get rid of all the previous screens(login & register)
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
    private fun newMessageAction(){
        val intent = Intent(this, NewMessageActivity::class.java)
        startActivity(intent)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

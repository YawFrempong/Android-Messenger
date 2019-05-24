package com.uncc.yfrempon.recreatingmessengerbasic.cell

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.uncc.yfrempon.recreatingmessengerbasic.R
import com.uncc.yfrempon.recreatingmessengerbasic.models.User
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.primary_cell.view.*

class primaryRecyclerCell(val chatMessage: ChatMessage): Item<ViewHolder>() {
    var partner: User? = null
    override fun bind(viewHolder: ViewHolder, position: Int) {
        val partnerID : String
        if(chatMessage.fromID == FirebaseAuth.getInstance().uid) //if I sent the message, show who it was sent to
        {
            partnerID = chatMessage.toID
        }
        else //if the message was sent to me, show who sent it to me
        {
            partnerID = chatMessage.fromID
        }
        val ref = FirebaseDatabase.getInstance().getReference("/users/$partnerID")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {      //find the user of newly added messages
            override fun onDataChange(p0: DataSnapshot) {
                partner = p0.getValue(User::class.java)
                viewHolder.itemView.primaryUsernameView.text = partner?.username
                Picasso.get().load(partner?.imageURL).into(viewHolder.itemView.primaryImageView) //loads the founded users image
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
        viewHolder.itemView.primaryMessageView.text = chatMessage.text
    }

    override fun getLayout(): Int {
        return R.layout.primary_cell
    }
}
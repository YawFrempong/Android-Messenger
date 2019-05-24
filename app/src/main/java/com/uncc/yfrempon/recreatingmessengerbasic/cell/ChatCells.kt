package com.uncc.yfrempon.recreatingmessengerbasic.cell

import com.squareup.picasso.Picasso
import com.uncc.yfrempon.recreatingmessengerbasic.R
import com.uncc.yfrempon.recreatingmessengerbasic.models.User
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_row.view.*
import kotlinx.android.synthetic.main.chat_row_2.view.*

class ChatMessage(val id: String, val text: String, val fromID: String, val toID: String, val timestamp: Long) {
    constructor(): this("", "", "", "", -1)
}

class ChatFromItem(val text: String, val user: User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView2.text = text
        Picasso.get().load(user.imageURL).into(viewHolder.itemView.profileView2)
    }

    override fun getLayout(): Int {
        return R.layout.chat_row_2
    }
}

class ChatToItem(val text: String, val user: User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView1.text = text
        //load user image
        Picasso.get().load(user.imageURL).into(viewHolder.itemView.profileView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_row
    }
}
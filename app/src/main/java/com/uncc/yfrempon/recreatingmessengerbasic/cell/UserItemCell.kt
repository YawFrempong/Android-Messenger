package com.uncc.yfrempon.recreatingmessengerbasic.cell

import com.squareup.picasso.Picasso
import com.uncc.yfrempon.recreatingmessengerbasic.R
import com.uncc.yfrempon.recreatingmessengerbasic.models.User
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.cell.view.*

class UserItem(val user: User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.primaryUsernameView.text = user.username
        Picasso.get().load(user.imageURL).into(viewHolder.itemView.userImageView) //loads url images into recycle view
    }

    override fun getLayout(): Int {
        return R.layout.cell
    }
}
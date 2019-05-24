package com.uncc.yfrempon.recreatingmessengerbasic.models

import android.os.Parcelable
import com.squareup.picasso.Picasso
import com.uncc.yfrempon.recreatingmessengerbasic.R
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.cell.view.*

@Parcelize
class User(val uid: String, val username: String, val imageURL: String): Parcelable {
    constructor() : this("","","") //default constructor
}

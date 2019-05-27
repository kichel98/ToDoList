package com.example.todolist

import android.content.Context
import android.graphics.Paint
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView


class MyArrayAdapter(context: Context, var data: ArrayList<ListItem>) :
    ArrayAdapter<ListItem>(context, R.layout.my_layout_item, data) {

    // ViewHolder!!

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.my_layout_item, parent, false)
        }
        view!!.findViewById<TextView>(R.id.textView).text = data[position].title

        (view!!.findViewById<TextView>(R.id.checkbox) as CheckBox).isChecked = data[position].isSelected
        if(data[position].isSelected) {
            view!!.findViewById<TextView>(R.id.textView).paintFlags =
                (view!!.findViewById<TextView>(R.id.textView).paintFlags or Paint.STRIKE_THRU_TEXT_FLAG)
        } else {
            view!!.findViewById<TextView>(R.id.textView).paintFlags =
                view!!.findViewById<TextView>(R.id.textView).paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        view!!.findViewById<TextView>(R.id.textView5).text = data[position].priority

        val path = "com.example.todolist:drawable/${data[position].image}"
        val id = context.resources.getIdentifier(path, null, null)
        (view!!.findViewById<TextView>(R.id.imageView) as ImageView).setImageResource(id)


        return view
    }
}
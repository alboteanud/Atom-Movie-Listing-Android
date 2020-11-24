package com.example.atommovielisting.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.atommovielisting.DetailActivity
import com.example.atommovielisting.DetailFragment
import com.example.atommovielisting.ListActivity
import com.example.atommovielisting.R
import com.example.atommovielisting.dummy.DummyContent
import com.example.atommovielisting.model.FeedEntry

class MyAdapter(val context: Context, var values: List<FeedEntry>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val onClickListener: View.OnClickListener

        init {
            onClickListener = View.OnClickListener { v ->
                val item = v.tag as DummyContent.DummyItem
//                if (twoPane) {
//                    val fragment = DetailFragment().apply {
//                        arguments = Bundle().apply {
//                            putString(DetailFragment.ARG_ITEM_ID, item.id)
//                        }
//                    }
//                    parentActivity.supportFragmentManager
//                        .beginTransaction()
//                        .replace(R.id.item_detail_container, fragment)
//                        .commit()
//                } else {
//                    val intent = Intent(v.context, DetailActivity::class.java).apply {
//                        putExtra(DetailFragment.ARG_ITEM_ID, item.id)
//                    }
//                    v.context.startActivity(intent)
//                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_list_content, parent, false)
            return ViewHolder(view)
        }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = values[position]
//        holder.idView.text = item.title
//            holder.contentView.text = item.overview

        with(holder.itemView) {
            tag = item
            setOnClickListener(onClickListener)
        }
    }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val idView: TextView = view.findViewById(R.id.id_text)
            val contentView: TextView = view.findViewById(R.id.content)
        }

    fun setUpdates(newUpdates: List<FeedEntry>) {
        // If there was no forecast data, then recreate all of the list
        if (values.isEmpty()) {
            values = newUpdates
            notifyDataSetChanged()

        }


    }

}
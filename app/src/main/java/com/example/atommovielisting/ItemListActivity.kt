package com.example.atommovielisting

import android.content.Intent
import android.os.Bundle
import androidx.core.widget.NestedScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider

import com.example.atommovielisting.dummy.DummyContent
import com.example.atommovielisting.dummy.DummyContent2
import com.example.atommovielisting.model.FeedEntry
import com.example.atommovielisting.ui.MyViewModel
import com.example.atommovielisting.utilities.InjectorUtils
import com.example.atommovielisting.utilities.LogUtils.log
import java.util.ArrayList

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [ItemDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class ItemListActivity : AppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false
    private lateinit var myViewModel: MyViewModel
    private var listEntries: Array<FeedEntry>? = null
    private lateinit var mAdapter: SimpleItemRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = title

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        getMovies()

        if (findViewById<NestedScrollView>(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }

//        setupRecyclerView(findViewById(R.id.item_list), entries)

        val factory = InjectorUtils.provideMainActivityViewModelFactory(this.applicationContext)
        myViewModel = ViewModelProvider(this@ItemListActivity, factory).get(MyViewModel::class.java)

        observeEntries(myViewModel)
    }

    private fun observeEntries(myViewModel: MyViewModel) {
        myViewModel.entries.observe(this, androidx.lifecycle.Observer { listEntries ->
            if (listEntries.isNullOrEmpty()) return@Observer
            this.listEntries = listEntries
//            updateAdapter()
        })
    }


    fun getMovies(){
        val networkDataSource = InjectorUtils.provideNetworkDataSource(this.applicationContext)
        networkDataSource.fetchMovies {entries ->
//log("feed entry received" + entries?.size)
//            val ITEMS: MutableList<DummyContent2.DummyItem> = ArrayList()
            entries?.let {
                setupRecyclerView(findViewById(R.id.item_list), entries)
            }

        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, entries: Array<FeedEntry>) {
        mAdapter = SimpleItemRecyclerViewAdapter(this, entries.toList(), twoPane)
        recyclerView.adapter = mAdapter
    }

    class SimpleItemRecyclerViewAdapter(private val parentActivity: ItemListActivity,
                                        private val values: List<FeedEntry>,
                                        private val twoPane: Boolean) :
            RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val onClickListener: View.OnClickListener

        init {
            onClickListener = View.OnClickListener { v ->
                val item = v.tag as DummyContent.DummyItem
                if (twoPane) {
                    val fragment = ItemDetailFragment().apply {
                        arguments = Bundle().apply {
                            putString(ItemDetailFragment.ARG_ITEM_ID, item.id)
                        }
                    }
                    parentActivity.supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit()
                } else {
                    val intent = Intent(v.context, ItemDetailActivity::class.java).apply {
                        putExtra(ItemDetailFragment.ARG_ITEM_ID, item.id)
                    }
                    v.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]
            holder.idView.text = item.title
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
    }
}
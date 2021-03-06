package com.example.atommovielisting

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.atommovielisting.model.Movie
import com.example.atommovielisting.network.NetworkDataSource.Companion.buildPosterPhotoUrl
import com.example.atommovielisting.ui.MyViewModel
import com.example.atommovielisting.utilities.InjectorUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar


/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [DetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class ListActivity : AppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false
    private lateinit var myViewModel: MyViewModel
    private var listEntries: List<Movie>? = null
    private lateinit var mAdapter: SimpleItemRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = title
        toolbar.setLogo(R.mipmap.ic_launcher)
        // todo: setup a custom toolbar

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        if (findViewById<NestedScrollView>(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }

        val factory = InjectorUtils.provideListActivityViewModelFactory(this.applicationContext)

        myViewModel = ViewModelProvider(this@ListActivity, factory).get(MyViewModel::class.java)

        setupRecyclerView(findViewById(R.id.item_list))
        observeEntries(myViewModel)
    }

    private fun observeEntries(myViewModel: MyViewModel) {
        myViewModel.entries.observe(this, androidx.lifecycle.Observer { listEntries ->
//            if (listEntries.isNullOrEmpty()) return@Observer
            this.listEntries = listEntries
//            update all entries Adapter
            mAdapter.values = listEntries
            mAdapter.notifyDataSetChanged()

        })
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        mAdapter = SimpleItemRecyclerViewAdapter(this, twoPane)
        recyclerView.adapter = mAdapter

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_all -> {
                alertDelete()
                true
            }
            R.id.add_more_movies -> {
                myViewModel.loadNewEntries()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun alertDelete(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Alert")
        builder.setMessage(R.string.delete_all_movies)

        builder.setPositiveButton("Delete") { dialog, which ->
            Toast.makeText(applicationContext, "Deleted", Toast.LENGTH_SHORT).show()
            myViewModel.deleteAllEntries()
        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, which ->

        }
        builder.show()
    }

    class SimpleItemRecyclerViewAdapter(private val parentActivity: ListActivity, private val twoPane: Boolean) :
            RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        var values: List<Movie> = listOf()
        private val onClickListener: View.OnClickListener

        init {
            onClickListener = View.OnClickListener { v ->
                val item = v.tag as Movie
                if (twoPane) {
                    val fragment = DetailFragment().apply {
                        arguments = Bundle().apply {
                            putInt(DetailFragment.ARG_ITEM_ID, item.id)
                        }
                    }
                    parentActivity.supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit()
                } else {
                    val intent = Intent(v.context, DetailActivity::class.java).apply {
                        putExtra(DetailFragment.ARG_ITEM_ID, item.id)
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
            holder.contentView.text = item.overview

            item.poster_path?.let {
                val posterUrl = buildPosterPhotoUrl(item.poster_path)
                Glide.with(parentActivity).load(posterUrl).into(holder.imageView)
            }

            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val idView: TextView = view.findViewById(R.id.id_text)
            val contentView: TextView = view.findViewById(R.id.content)
            val imageView: ImageView = view.findViewById(R.id.myImageView)
        }
    }
}
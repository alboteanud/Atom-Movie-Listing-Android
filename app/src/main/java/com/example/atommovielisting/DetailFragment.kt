package com.example.atommovielisting

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.CollapsingToolbarLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.atommovielisting.model.DetailsMovie
import com.example.atommovielisting.model.Movie
import com.example.atommovielisting.network.NetworkDataSource
import com.example.atommovielisting.utilities.InjectorUtils

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a [ListActivity]
 * in two-pane mode (on tablets) or a [DetailActivity]
 * on handsets.
 */
class DetailFragment : Fragment() {

    private var item: Movie? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                //  todo use a Loader to load content from a content provider until network return
                val id = it.getInt(ARG_ITEM_ID)

                activity?.let {
                    InjectorUtils.provideNetworkDataSource(it.applicationContext).downloadEntry(id) { movie ->
                        setupViews(it,  movie)
                    }
                }
            }
        }
    }

    private fun setupViews(activity: Activity, movie: DetailsMovie?) {
        if (movie == null) return
        activity.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)?.title = movie.title
        movie.poster_path?.let {
            val posterUrl = NetworkDataSource.buildPosterPhotoUrl(it)
            val imageView = activity.findViewById<ImageView>(R.id.detailsImageView)
            Glide.with(activity).load(posterUrl).placeholder(R.drawable.placeholder_movie).into(imageView)
        }
        activity.findViewById<TextView>(R.id.overviewTextView)?.text = movie.overview
        val textGenres = movie.genres.joinToString(separator = " | ")
        activity.findViewById<TextView>(R.id.genresTextView)?.text = textGenres
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.item_detail, container, false)

        // Show the dummy content as text in a TextView.
        item?.let {
            rootView.findViewById<TextView>(R.id.item_detail).text = "_"
        }

        return rootView
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}
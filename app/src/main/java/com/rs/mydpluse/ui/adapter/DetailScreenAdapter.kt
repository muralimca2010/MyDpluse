package com.rs.mydpluse.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.rs.mydpluse.Episodes
import com.rs.mydpluse.R

class DetailScreenAdapter(
    private var episodes: MutableList<Episodes>,
    private val onEpisodeClick: (episode: Episodes) -> Unit
) : RecyclerView.Adapter<DetailScreenAdapter.EpisodesViewHolder>() {

    companion object {
        lateinit var objEpisodesList: MutableList<Episodes>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodesViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_movie, parent, false)
        return EpisodesViewHolder(view)
    }

    override fun getItemCount(): Int = episodes.size

    override fun onBindViewHolder(holder: EpisodesViewHolder, position: Int) {
        objEpisodesList = episodes
        holder.bind(episodes[position])
    }

    /*fun updateMovies(movies: List<Movie>) {
        this.movies = movies
        notifyDataSetChanged()
    }*/
    fun appendMovies(episodes: List<Episodes>) {
        this.episodes.addAll(episodes)
        notifyItemRangeInserted(
            this.episodes.size,
                episodes.size - 1
        )
    }


    inner class EpisodesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val poster: ImageView = itemView.findViewById(R.id.item_movie_poster)
        private val posterTitle: TextView = itemView.findViewById(R.id.episode_title)

        fun bind(episode: Episodes) {
            var imgUrl: String? = episode.images?.horizontal?.url
            posterTitle.text = episode.title
            Glide.with(itemView)
                .load(Uri.parse(imgUrl))
//                .apply(RequestOptions.skipMemoryCacheOf(true))
//                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .transform(CenterCrop())
                .into(poster)
            itemView.setOnClickListener { onEpisodeClick.invoke(episode) }
        }
    }
}
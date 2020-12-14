package com.rs.mydpluse.ui.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.rs.mydpluse.Episodes
import com.rs.mydpluse.R
import com.rs.mydpluse.Seasons
import com.rs.mydpluse.ShowsDetailResponse
import com.rs.mydpluse.ui.adapter.DetailScreenAdapter
import java.io.IOException

const val MOVIE_BACKDROP = "extra_movie_backdrop"
//const val MOVIE_POSTER = "extra_movie_poster"
const val MOVIE_TITLE = "extra_movie_title"
const val MOVIE_RATING = "extra_movie_rating"
const val MOVIE_RELEASE_DATE = "extra_movie_release_date"
const val MOVIE_OVERVIEW = "extra_movie_overview"

class DetailsScreenActivity : AppCompatActivity() {

    private lateinit var popularMovies: RecyclerView
    private lateinit var popularEpisodesAdapter: DetailScreenAdapter
    private lateinit var popularMoviesLayoutMgr: LinearLayoutManager
    private lateinit var showsDetailResponse: ShowsDetailResponse
    private var popularMoviesPage = 1
    private var seasonNuber = 1


    private lateinit var backdrop: ImageView
    private lateinit var title: TextView
    private lateinit var releaseDate: TextView
    private lateinit var overview: TextView
    private lateinit var play_btn: TextView
    private lateinit var play_btn_ll: LinearLayout
    private lateinit var no_of_eps: TextView

    private lateinit var seasions: ArrayList<Seasons>
    private lateinit var backdropPath: String
    private lateinit var titleText: String
    private var titleListText = "Seasion1"
    private lateinit var releaseDateText: String
    private lateinit var overviewText: String
    private lateinit var seasonsTitle: ArrayList<String>

    private lateinit var season_head: TextView

    companion object {
        val MEDIA_URI = "media_uri"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (supportActionBar != null)
            supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_screen)

        popularMovies = findViewById(R.id.popular_movies)
        popularMoviesLayoutMgr = LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
        )

        backdrop = findViewById(R.id.movie_backdrop)
        title = findViewById(R.id.movie_title)
        releaseDate = findViewById(R.id.eps_year)
        overview = findViewById(R.id.movie_overview)
        season_head = findViewById(R.id.season_head)
        play_btn = findViewById(R.id.play_btn)
        no_of_eps = findViewById(R.id.no_of_eps)
        play_btn_ll = findViewById(R.id.play_btn_ll)

        popularMovies.layoutManager = popularMoviesLayoutMgr
        popularEpisodesAdapter = DetailScreenAdapter(mutableListOf()){ episode -> showEpisodeDetails(episode) }
        popularMovies.adapter = popularEpisodesAdapter

        getShowsDetailResponse();




    }
    private fun iniz(){

    }

    private fun populateDetails(episode: Episodes) {

        Glide.with(this)
                .load(Uri.parse(backdropPath))
//                .apply(RequestOptions.skipMemoryCacheOf(true))
//                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .transform(CenterCrop())
                .into(backdrop)
        backdrop.setOnClickListener( View.OnClickListener {
            episode.videoPlayback?.url?.let { it1 -> callPlayerView(it1) }
        })
        if (::titleText.isInitialized) {
            title.text = titleText
        }
        if (::releaseDateText.isInitialized) {
            releaseDate.text = releaseDateText
        }
//        overview.text = overviewText
        play_btn.text = episode.title
        play_btn_ll.setOnClickListener(View.OnClickListener {
            episode.videoPlayback?.url?.let { it1 -> callPlayerView(it1) }
        })
    }



    fun openSeasonList(view: View) {

//        val seasion_items = resources.getStringArray(R.array.seasons)
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
        var seasonsArr = arrayOfNulls<String>(seasonsTitle.size)
        with(builder)
        {
            setTitle(titleListText)
//            var inx: Int
            setItems(seasonsTitle.toArray(seasonsArr)) { dialog, which ->
                /*Toast.makeText(
                        applicationContext,
                        seasonsArr[which] + " is clicked ${seasions.get(which).episodes?.size}",
                        Toast.LENGTH_SHORT
                ).show()*/
//                inx = which
                titleListText = seasonsTitle.get(which)
                play_btn.text = seasions.get(which).episodes?.get(0)?.title
//                season_head.text = seasonsArr[which]

                backdropPath = seasions.get(which).episodes?.get(0)?.images?.hero?.url.toString()
                titleText = seasions.get(which).episodes?.get(0)?.title.toString()
                releaseDateText = seasions.get(which).episodes?.get(0)?.duration.toString()

                seasions.get(which).episodes?.get(0)?.let { populateDetails(it) }
                seasions.get(which).episodes?.let { referechEpisodes(it) }
                no_of_eps.text = seasions.get(which).episodes?.size.toString()
                overviewText = seasions.get(which).description.toString()
                overview.text = overviewText
            }

            season_head.text = titleListText
            show()
        }
    }

    private fun referechEpisodes(episodes: MutableList<Episodes>) {
        popularEpisodesAdapter = DetailScreenAdapter(mutableListOf()){ episode -> showEpisodeDetails(episode) }
        popularMovies.adapter = popularEpisodesAdapter
        if (episodes != null) {

            onPopularMoviesFetched(episodes)
        }
    }

    private fun showEpisodeDetails(episode: Episodes) {



        backdropPath = episode.images?.hero?.url.toString()
        titleText = episode.title.toString()
        releaseDateText = episode.duration.toString()
        overviewText = seasions?.get(0)?.description.toString()
//        season_head.text = seasions?.get(0)?.title.toString()
        season_head.text = titleListText
        seasonsTitle = ArrayList()
        for (seas in seasions){

            seas.title?.let { seasonsTitle.add(it) }
        }

        populateDetails(episode)

    }
    @Throws(Exception::class)
    fun getShowsDetailResponse() {


        try {
            val inputStream = resources.assets.open("ShowDetailsPagePerfTest.json")
            val streamArray = ByteArray(inputStream.available())
            inputStream.read(streamArray)
            inputStream.close()
            val jsonElement = JsonParser().parse(String(streamArray))
            val listType = object : TypeToken<ShowsDetailResponse>() {}.type
            showsDetailResponse = Gson().fromJson<ShowsDetailResponse>(jsonElement, listType)
//            var isSise = showsDetailResponse.seasons?.size
            seasions = (showsDetailResponse.seasons as ArrayList<Seasons>?)!!
            var isSeasonNumber = 0
            if(seasonNuber>0){
                isSeasonNumber = seasonNuber - 1
            } else {
                isSeasonNumber = seasonNuber
            }
            overviewText = seasions.get(isSeasonNumber).description.toString()
            overview.text = overviewText
            val episodes = showsDetailResponse.seasons?.get(isSeasonNumber)?.episodes

            val isSize = episodes?.size
            if (episodes != null) {

                onPopularMoviesFetched(episodes)
            }
            Log.d("MainActivity",": "+isSize)
        } catch (e: IOException) {
            e.printStackTrace()
        }
//        popularEpisodesAdapter = DetailScreenAdapter(mutableListOf()){ episode -> showEpisodeDetails(episode) }
        popularMovies.adapter = popularEpisodesAdapter
//        return showsDetailResponse
    }

    private fun variableInit(episode: Episodes){

        showEpisodeDetails(episode)
    }

    private fun onPopularMoviesFetched(episodes: MutableList<Episodes>) {
        popularEpisodesAdapter.appendMovies(episodes)
        attachPopularMoviesOnScrollListener()
//        variableInit(episodes.get(0))
        showEpisodeDetails(episodes.get(0))
        Log.d("MainActivity", "Movies: $episodes")
    }

    private fun attachPopularMoviesOnScrollListener() {
        popularMovies.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val totalItemCount = popularMoviesLayoutMgr.itemCount
                val visibleItemCount = popularMoviesLayoutMgr.childCount
                val firstVisibleItem = popularMoviesLayoutMgr.findFirstVisibleItemPosition()

//                if (firstVisibleItem + visibleItemCount >= totalItemCount / 2) {
//                    popularMovies.removeOnScrollListener(this)
//                    popularMoviesPage++
////                    getPopularList()
//                    getShowsDetailResponse();
//                }
            }
        })
    }

    fun callPlayerView(playURL: String){
        Log.d("DetailsScreenActivity :",playURL)
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra(MEDIA_URI, playURL)
        startActivity(intent)
    }

}
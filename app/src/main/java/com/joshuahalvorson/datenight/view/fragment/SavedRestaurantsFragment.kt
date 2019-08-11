package com.joshuahalvorson.datenight.view.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.joshuahalvorson.datenight.App
import com.joshuahalvorson.datenight.R
import com.joshuahalvorson.datenight.SharedPrefsHelper
import com.joshuahalvorson.datenight.adapter.SavedRestaurantsListAdapter
import com.joshuahalvorson.datenight.database.RestaurantDatabase
import com.joshuahalvorson.datenight.model.SavedRestaurant
import com.joshuahalvorson.datenight.toSavedRestaurant
import com.joshuahalvorson.datenight.viewmodel.YelpViewModel
import com.joshuahalvorson.datenight.viewmodel.YelpViewModelFactory
import kotlinx.android.synthetic.main.fragment_saved_restaurants.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class SavedRestaurantsFragment : Fragment() {
    @Inject
    lateinit var yelpViewModelFactory: YelpViewModelFactory
    private lateinit var yelpViewModel: YelpViewModel

    private var db: RestaurantDatabase? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_saved_restaurants, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        App.app.yelpComponent.inject(this)
        yelpViewModel =
            ViewModelProviders.of(this, yelpViewModelFactory).get(YelpViewModel::class.java)

        db = context?.let {
            Room.databaseBuilder(it,
                RestaurantDatabase::class.java, getString(R.string.database_playlist_name)).build()

        }
        displayRestaurants()
    }

    private fun displayRestaurants() {
        saved_restaurants_list.apply {
            layoutManager = LinearLayoutManager(context)
            GlobalScope.launch(Dispatchers.IO) {
                adapter = db?.savedRestaurantsDao()?.getAllPlaylists()?.let { restaurants ->
                    SavedRestaurantsListAdapter(
                        restaurants
                    )
                }
            }
        }
    }

    private fun getRestaurant(id: String) {
        yelpViewModel.getRestaurant(id).observe(this, Observer {
            it?.let { restaurant ->
                Log.i("businessResponse", restaurant.name)
            }
        })
    }
}

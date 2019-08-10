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
import com.joshuahalvorson.datenight.App
import com.joshuahalvorson.datenight.R
import com.joshuahalvorson.datenight.SharedPrefsHelper
import com.joshuahalvorson.datenight.adapter.SavedRestaurantsListAdapter
import com.joshuahalvorson.datenight.viewmodel.YelpViewModel
import com.joshuahalvorson.datenight.viewmodel.YelpViewModelFactory
import kotlinx.android.synthetic.main.fragment_saved_restaurants.*
import javax.inject.Inject

class SavedRestaurantsFragment : Fragment() {
    @Inject
    lateinit var yelpViewModelFactory: YelpViewModelFactory
    private lateinit var yelpViewModel: YelpViewModel
    private lateinit var sharedPrefsHelper: SharedPrefsHelper

    private var restaurantIds: ArrayList<String> = arrayListOf()
    private var restaurantNames: ArrayList<String> = arrayListOf()
    private var restaurantNameIdPairList: ArrayList<Pair<String, String>> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_saved_restaurants, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sharedPrefsHelper = SharedPrefsHelper(
            activity?.getSharedPreferences(
                SharedPrefsHelper.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE
            )
        )

        App.app.yelpComponent.inject(this)
        yelpViewModel =
            ViewModelProviders.of(this, yelpViewModelFactory).get(YelpViewModel::class.java)

        getRestaurants()
    }

    private fun getRestaurants() {
        sharedPrefsHelper.get(SharedPrefsHelper.RESTAURANT_ID_KEY, "")?.split(",")?.let { ids ->
            restaurantIds.addAll(
                ids
            )
        }

        sharedPrefsHelper.get(SharedPrefsHelper.RESTAURANT_NAME_KEY, "")?.split(",")?.let { names ->
            restaurantNames.addAll(
                names
            )
        }

        for (i in 0 until restaurantIds.size -1) {
            restaurantNameIdPairList.add(Pair(restaurantIds[i], restaurantNames[i]))
        }

        displayRestaurants()
    }

    private fun displayRestaurants() {
        saved_restaurants_list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = SavedRestaurantsListAdapter(restaurantNameIdPairList)
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

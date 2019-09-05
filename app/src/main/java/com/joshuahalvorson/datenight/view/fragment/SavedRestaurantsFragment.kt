package com.joshuahalvorson.datenight.view.fragment

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
import com.daimajia.androidanimations.library.Techniques
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.joshuahalvorson.datenight.App
import com.joshuahalvorson.datenight.R
import com.joshuahalvorson.datenight.adapter.RestaurantReviewsListAdapter
import com.joshuahalvorson.datenight.adapter.SavedRestaurantsListAdapter
import com.joshuahalvorson.datenight.animateViewWithYoYo
import com.joshuahalvorson.datenight.database.RestaurantDatabase
import com.joshuahalvorson.datenight.model.Businesses
import com.joshuahalvorson.datenight.model.SavedRestaurant
import com.joshuahalvorson.datenight.openUrlOnClick
import com.joshuahalvorson.datenight.viewmodel.YelpViewModel
import com.joshuahalvorson.datenight.viewmodel.YelpViewModelFactory
import kotlinx.android.synthetic.main.content_saved_restaurants.*
import kotlinx.android.synthetic.main.restaurant_details_bottom_sheet.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.android.material.bottomsheet.BottomSheetBehavior

class SavedRestaurantsFragment : Fragment(), OnMapReadyCallback {
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isScrollGesturesEnabled = false
    }

    @Inject
    lateinit var yelpViewModelFactory: YelpViewModelFactory
    private lateinit var yelpViewModel: YelpViewModel
    private lateinit var mMap: GoogleMap

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

        BottomSheetBehavior.from(bottom_sheet_restaurant_details).state = BottomSheetBehavior.STATE_HIDDEN

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.restaurant_map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        displayRestaurants()
    }

    private fun displayRestaurants() {
        saved_restaurants_list.apply {
            layoutManager = LinearLayoutManager(context)
            GlobalScope.launch(Dispatchers.IO) {
                adapter = db?.savedRestaurantsDao()?.getAllRestaurants()?.let { restaurants ->
                    SavedRestaurantsListAdapter(
                        restaurants,
                        object: SavedRestaurantsListAdapter.OnListItemClick {
                            override fun onListItemClick(restaurant: SavedRestaurant?) {
                                restaurant?.id?.let { id -> getRestaurant(id) }
                                BottomSheetBehavior.from(bottom_sheet_restaurant_details).state = BottomSheetBehavior.STATE_HIDDEN
                            }
                        }
                    )
                }
            }
        }
    }

    private fun getRestaurant(id: String) {
        yelpViewModel.getRestaurant(id).observe(this, Observer {
            it?.let { restaurant ->
                Log.i("businessResponse", restaurant.name)
                setBottomSheetContent(restaurant)
            }
        })
    }

    private fun setBottomSheetContent(businesses: Businesses) {
        //TODO("Fix crash when pressing multiple times on item, has to do with the network call for reviews")
        BottomSheetBehavior.from(bottom_sheet_restaurant_details).state = BottomSheetBehavior.STATE_COLLAPSED
        mMap.clear()
        bottom_sheet_restaurant_title.text = businesses.name
        bottom_sheet_restaurant_categories.text = ""
        var categories = 0
        businesses.categories?.size?.let {
            categories = it
        }
        for (i in 0 until categories) {
            bottom_sheet_restaurant_categories.text =
                "${bottom_sheet_restaurant_categories.text}${businesses.categories?.get(i)?.title} \n"
        }

        businesses.url?.let { url ->
            context?.let { context ->
                open_restaurant_on_yelp_imageview.openUrlOnClick(url, context)
            }
        }

        businesses.coordinates?.latitude?.let { latitude ->
            businesses.coordinates.longitude?.let { longitude ->
                val location = LatLng(latitude, longitude)
                mMap.addMarker(MarkerOptions().position(location).title(businesses.name))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
            }
        }

        businesses.alias?.let { alias ->
            yelpViewModel.getRestaurantReviews(alias).observe(this, Observer { response ->
                Log.i("reviewResponse", "${response.reviews?.size}")
                bottom_sheet_restaurant_reviews.apply {
                    layoutManager = LinearLayoutManager(context)
                    response.reviews?.let { reviews ->
                        adapter = RestaurantReviewsListAdapter(reviews)
                    }
                }
            })
        }
        save_for_later_button.visibility = View.GONE
    }
}

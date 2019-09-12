package com.joshuahalvorson.datenight.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.room.Room
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.joshuahalvorson.datenight.*
import com.joshuahalvorson.datenight.adapter.RestaurantReviewsListAdapter
import com.joshuahalvorson.datenight.adapter.SavedRestaurantsListAdapter
import com.joshuahalvorson.datenight.database.RestaurantDatabase
import com.joshuahalvorson.datenight.model.Businesses
import com.joshuahalvorson.datenight.model.SavedRestaurant
import com.joshuahalvorson.datenight.viewmodel.YelpViewModel
import com.joshuahalvorson.datenight.viewmodel.YelpViewModelFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.content_saved_restaurants.*
import kotlinx.android.synthetic.main.restaurant_details_bottom_sheet.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.blockstack.android.sdk.BlockstackSession
import org.blockstack.android.sdk.model.GetFileOptions
import org.blockstack.android.sdk.model.PutFileOptions
import org.blockstack.android.sdk.model.toBlockstackConfig
import javax.inject.Inject

class SavedRestaurantsFragment : Fragment(), OnMapReadyCallback {
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isScrollGesturesEnabled = false
    }

    private var _blockstackSession: BlockstackSession? = null

    @Inject
    lateinit var yelpViewModelFactory: YelpViewModelFactory
    private lateinit var yelpViewModel: YelpViewModel
    private lateinit var mMap: GoogleMap

    private var db: RestaurantDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_saved_restaurants, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        App.app.yelpComponent.inject(this)
        yelpViewModel =
            ViewModelProviders.of(this, yelpViewModelFactory).get(YelpViewModel::class.java)

        db = context?.let {
            Room.databaseBuilder(
                it,
                RestaurantDatabase::class.java, getString(R.string.database_playlist_name)
            ).build()

        }

        val config = "https://joshhalvorson.github.io/blockstack-android-web-app/public/"
            .toBlockstackConfig(arrayOf(org.blockstack.android.sdk.Scope.StoreWrite))

        _blockstackSession = BlockstackSession(context, config)

        BottomSheetBehavior.from(bottom_sheet_restaurant_details).state =
            BottomSheetBehavior.STATE_HIDDEN

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.restaurant_map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        displayRestaurants()
    }

    private fun displayRestaurants() {
        val savedRestaurants = arrayListOf<SavedRestaurant>()
        val adapter = SavedRestaurantsListAdapter(
            savedRestaurants,
            object : SavedRestaurantsListAdapter.OnListItemClick {
                override fun onListItemClick(restaurant: SavedRestaurant?) {
                    restaurant?.id?.let { id -> getRestaurant(id) }
                    BottomSheetBehavior.from(bottom_sheet_restaurant_details).state =
                        BottomSheetBehavior.STATE_HIDDEN
                }
            }
        )
        saved_restaurants_list.layoutManager = LinearLayoutManager(context)
        saved_restaurants_list.adapter = adapter
        (saved_restaurants_list.itemAnimator as SimpleItemAnimator)
            .supportsChangeAnimations = false
        val swipeHandler = object : SwipeToDeleteCallback(context!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = savedRestaurants[viewHolder.adapterPosition]
                if (!blockstackSession().isUserSignedIn()) {
                    //delete from roomdb
                    GlobalScope.launch(Dispatchers.IO) {
                        db?.savedRestaurantsDao()?.deleteRestaurantById(item.id)?.let {
                            withContext(Dispatchers.Main) {
                                savedRestaurants.remove(item)
                                adapter.notifyDataSetChanged()
                                if (savedRestaurants.size <= 0) {
                                    no_restaurants_saved_text.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                } else {
                    //delte from gaia file
                    removeRestaurantFromRemoteList(adapter, savedRestaurants, item)
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(saved_restaurants_list)

        saved_restaurants_progress_circle.visibility = View.VISIBLE

        if (!blockstackSession().isUserSignedIn()) {
            GlobalScope.launch(Dispatchers.IO) {
                db?.savedRestaurantsDao()?.getAllRestaurants()?.let {
                    savedRestaurants.clear()
                    savedRestaurants.addAll(it)
                    withContext(Dispatchers.Main) {
                        saved_restaurants_progress_circle.visibility = View.GONE
                        adapter.notifyDataSetChanged()
                        if (savedRestaurants.size <= 0) {
                            no_restaurants_saved_text.visibility = View.VISIBLE
                        }
                    }
                }
            }
        } else {
            val getOptions = GetFileOptions()
            blockstackSession().getFile(
                RandomRestaurantFragment.IDS_FILE_NAME,
                getOptions
            ) { getFileResult ->
                val result: String = getFileResult.value.toString()
                if (result == "null") {
                    no_restaurants_saved_text.visibility = View.VISIBLE
                } else {
                    val listIds = result.split(",").toMutableSet()
                    listIds.remove("")
                    GlobalScope.launch(Dispatchers.IO) {
                        listIds.forEach {
                            Thread.sleep(200)
                            yelpViewModel.getRestaurant(it)
                                ?.subscribeOn(Schedulers.io())
                                ?.observeOn(AndroidSchedulers.mainThread())
                                ?.subscribe({ savedRes ->
                                    savedRes.toSavedRestaurant()
                                        ?.let { it1 ->
                                            savedRestaurants.add(it1)
                                            adapter.notifyDataSetChanged()
                                        }
                                },
                                    { error ->
                                        Toast.makeText(
                                            context,
                                            error.localizedMessage,
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                )
                        }
                        withContext(Dispatchers.Main) {
                            saved_restaurants_progress_circle?.let { it.visibility = View.GONE }
                            if (savedRestaurants.size <= 0) {
                                no_restaurants_saved_text.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

    private fun removeRestaurantFromRemoteList(adapter: SavedRestaurantsListAdapter, savedRestaurants: ArrayList<SavedRestaurant>, item: SavedRestaurant) {
        val putOptions = PutFileOptions()
        val getOptions = GetFileOptions()
        blockstackSession().getFile(
            RandomRestaurantFragment.IDS_FILE_NAME,
            getOptions
        ) { getFileResult ->
            var result = getFileResult.value
            if (result == null) {
                result = ""
            }
            val list = result.toString().split(",").toMutableList()
            list.remove(item.id)
            val listIds = list.joinToString().replace("\\s".toRegex(), "")
            blockstackSession().putFile(
                RandomRestaurantFragment.IDS_FILE_NAME, "$listIds,", putOptions
            ) { readURLResult ->
                if (readURLResult.hasValue) {
                    val readURL = readURLResult.value!!
                    activity?.runOnUiThread {
                        savedRestaurants.remove(item)
                        adapter.notifyDataSetChanged()
                        if (savedRestaurants.size <= 0) {
                            no_restaurants_saved_text.visibility = View.VISIBLE
                        }
                        Toast.makeText(
                            context,
                            "Restaurant removed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Log.i(RandomRestaurantFragment.TAG + " putFile", readURLResult.error)
                }
            }
        }

    }

    @SuppressLint("CheckResult")
    private fun getRestaurant(id: String) {
        yelpViewModel.getRestaurant(id)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({ restaurant ->
                setBottomSheetContent(restaurant)
            },
                { error ->
                    Toast.makeText(
                        context,
                        error.localizedMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
    }

    private fun setBottomSheetContent(businesses: Businesses) {
        BottomSheetBehavior.from(bottom_sheet_restaurant_details).state =
            BottomSheetBehavior.STATE_COLLAPSED
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
                bottom_sheet_restaurant_reviews.apply {
                    if (response != null) {
                        layoutManager = LinearLayoutManager(context)
                        response.reviews?.let { reviews ->
                            adapter = RestaurantReviewsListAdapter(reviews)
                        }
                    }
                }
            })
        }
        save_for_later_button.visibility = View.GONE
    }

    private fun blockstackSession(): BlockstackSession {
        val session = _blockstackSession
        if (session != null) {
            return session
        } else {
            throw IllegalStateException("No session.")
        }
    }
}

package com.joshuahalvorson.datenight.view.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.daimajia.androidanimations.library.Techniques
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.joshuahalvorson.datenight.*
import com.joshuahalvorson.datenight.R
import com.joshuahalvorson.datenight.adapter.RestaurantReviewsListAdapter
import com.joshuahalvorson.datenight.database.RestaurantDatabase
import com.joshuahalvorson.datenight.model.Businesses
import com.joshuahalvorson.datenight.view.MainActivity
import com.joshuahalvorson.datenight.viewmodel.YelpViewModel
import com.joshuahalvorson.datenight.viewmodel.YelpViewModelFactory
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.content_random_restaurant.*
import kotlinx.android.synthetic.main.restaurant_details_bottom_sheet.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.blockstack.android.sdk.BlockstackSession
import org.blockstack.android.sdk.model.GetFileOptions
import org.blockstack.android.sdk.model.PutFileOptions
import org.blockstack.android.sdk.model.toBlockstackConfig
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject
import kotlin.random.Random

class RandomRestaurantFragment : Fragment(), OnMapReadyCallback {
    companion object {
        const val IDS_FILE_NAME = "saved_res_ids.txt"
        const val TAG = "RandomRestFrag"
    }

    @Inject
    lateinit var yelpViewModelFactory: YelpViewModelFactory
    private lateinit var yelpViewModel: YelpViewModel
    private lateinit var mMap: GoogleMap

    private var _blockstackSession: BlockstackSession? = null
    private var disposable: Disposable? = null
    private var deviceLocation: Location? = null
    private var db: RestaurantDatabase? = null
    private var lastIndex = 0
    private var restaurantsList: ArrayList<Businesses> = arrayListOf()

    private var mLocationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_random_restaurant, container, false)
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

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.restaurant_map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        BottomSheetBehavior.from(bottom_sheet_restaurant_details).state =
            BottomSheetBehavior.STATE_HIDDEN

        setUpLocationUpdates()
        getLocation()

        new_restaurant_button.setOnClickListener {
            displayRestaurant()
        }

        show_restaurant_info.setOnClickListener {
            if (BottomSheetBehavior.from(bottom_sheet_restaurant_details).state == BottomSheetBehavior.STATE_COLLAPSED) {
                BottomSheetBehavior.from(bottom_sheet_restaurant_details).state =
                    BottomSheetBehavior.STATE_EXPANDED
            } else if (BottomSheetBehavior.from(bottom_sheet_restaurant_details).state == BottomSheetBehavior.STATE_HIDDEN) {
                BottomSheetBehavior.from(bottom_sheet_restaurant_details).state =
                    BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    private fun setUpLocationUpdates() {
        mLocationRequest = LocationRequest()
        mLocationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest?.interval = 1000
        mLocationRequest?.fastestInterval = 500
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        val locationSettingsRequest = builder.build()
        val settingsClient =
            LocationServices.getSettingsClient(activity as MainActivity)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                updateDeviceLocation(p0?.lastLocation)
            }
        }
    }

    private fun updateDeviceLocation(location: Location?) {
        deviceLocation = location
        getFusedLocationProviderClient(activity as MainActivity).removeLocationUpdates(locationCallback)
        getRestaurants()
    }

    @SuppressLint("CheckResult")
    private fun getRestaurants() {
        disposable = deviceLocation?.latitude?.let { lat ->
            deviceLocation?.longitude?.let { lon ->
                yelpViewModel.getLocalRestaurants(
                    "restaurant",
                    lat,
                    lon
                )
                    ?.subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe({ list ->
                        restaurantsList.addAll(list.businesses)
                        displayRestaurant()
                    },
                        { error ->
                            Log.i(TAG + " getRest", error.message)
                        }
                    )
            }
        }
    }

    private fun displayRestaurant() {
        restaurant_image_progress_circle.visibility = View.VISIBLE
        val index = Random.nextInt(0, restaurantsList.size - 1)
        if (lastIndex != index) {
            Picasso.get()
                .load(restaurantsList[index].image_url)
                .noFade()
                .into(restaurant_image, object : Callback {
                    override fun onSuccess() {
                        restaurant_image_progress_circle.visibility = View.GONE
                    }

                    override fun onError(e: Exception?) {
                        Log.i(TAG + " picasso", e?.localizedMessage)
                        //Toast.makeText(context, e?.localizedMessage, Toast.LENGTH_LONG).show()
                    }

                })
            restaurant_name.text = restaurantsList[index].name
            restaurant_location.text = "${restaurantsList[index].location?.display_address?.get(0)}"
            restaurant_price.text =
                if (restaurantsList[index].price == null) "No price given" else "${restaurantsList[index].price}"
            num_ratings.text = "Based on ${restaurantsList[index].review_count} reviews"
            restaurantsList[index].rating?.let { restaurant_rating.loadRatingImageWithPicasso(it) }
            setBottomSheetContent(restaurantsList[index])
            bottom_sheet_restaurant_details.animateViewWithYoYo(Techniques.SlideInUp, 500, 0)
            restaurant_name.animateViewWithYoYo(Techniques.FadeIn, 500, 0)
            restaurant_location.animateViewWithYoYo(Techniques.FadeIn, 500, 0)
            restaurant_price.animateViewWithYoYo(Techniques.FadeIn, 500, 0)
            restaurant_rating.animateViewWithYoYo(Techniques.FadeIn, 500, 0)
            restaurant_image.animateViewWithYoYo(Techniques.FadeIn, 500, 0)
            lastIndex = index
            if (restaurant_card.visibility == View.GONE) {
                animateRestaurantCardIn()
            }
        } else {
            displayRestaurant()
        }
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
                Log.i("reviewResponse", "${response.reviews?.size}")
                bottom_sheet_restaurant_reviews.apply {
                    layoutManager = LinearLayoutManager(context)
                    response.reviews?.let { reviews ->
                        adapter = RestaurantReviewsListAdapter(reviews)
                    }
                }
            })
        }

        save_for_later_button.setOnClickListener {
            save_for_later_button.isEnabled = false
            if (blockstackSession().isUserSignedIn()) {
                val putOptions = PutFileOptions()
                val getOptions = GetFileOptions()
                businesses.id?.let { it1 ->
                    blockstackSession().getFile(IDS_FILE_NAME, getOptions) { getFileResult ->
                        var result = getFileResult.value
                        if (result == null) {
                            result = ""
                        }
                        blockstackSession().putFile(
                            IDS_FILE_NAME, "$result$it1,", putOptions
                        ) { readURLResult ->
                            if (readURLResult.hasValue) {
                                val readURL = readURLResult.value!!
                                activity?.runOnUiThread {
                                    Toast.makeText(
                                        context,
                                        "Saved ${businesses.name} for later!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    save_for_later_button.isEnabled = true
                                }
                            } else {
                                Log.i(TAG + " putFile", readURLResult.error)
                            }
                        }
                    }
                }
                //TODO("Play animation for saved for later button")
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    businesses.toSavedRestaurant()?.let { savedRestaurant ->
                        db?.savedRestaurantsDao()?.insertAll(savedRestaurant)
                    }
                }
                Toast.makeText(
                    context,
                    "Saved ${businesses.name} for later!",
                    Toast.LENGTH_LONG
                ).show()
                save_for_later_button.isEnabled = true
            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isScrollGesturesEnabled = false
    }

    private fun animateRestaurantCardIn() {
        progress_circle.animateViewWithYoYo(Techniques.FadeOut, 500, 0)
        progress_circle.visibility = View.GONE
        restaurant_card.visibility = View.VISIBLE
        restaurant_card.animateViewWithYoYo(Techniques.SlideInUp, 500, 0)
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (!hasLocationPermission()) {
            EasyPermissions.requestPermissions(
                this,
                "This application needs access to your location to display restaurants in your area.",
                874,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            getLocation()
        } else {
            val fusedLocationClient =
                context?.let { getFusedLocationProviderClient(it) }
            fusedLocationClient?.lastLocation?.addOnSuccessListener { location: Location? ->
                Log.i("LastLocation", location.toString())
                location?.let { deviceLocation = location }
                if (deviceLocation == null) {
                    fusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper())
                } else {
                    getRestaurants()
                }
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        return EasyPermissions.hasPermissions(context!!, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun blockstackSession(): BlockstackSession {
        val session = _blockstackSession
        if (session != null) {
            return session
        } else {
            throw IllegalStateException("No session.")
        }
    }

    override fun onDetach() {
        super.onDetach()
        disposable?.dispose()
    }
}

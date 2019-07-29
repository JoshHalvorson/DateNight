package com.joshuahalvorson.datenight.view.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.daimajia.androidanimations.library.Techniques
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.joshuahalvorson.datenight.App
import com.joshuahalvorson.datenight.R
import com.joshuahalvorson.datenight.animateViewWithYoYo
import com.joshuahalvorson.datenight.loadImageWithPicasso
import com.joshuahalvorson.datenight.model.Businesses
import com.joshuahalvorson.datenight.viewmodel.YelpViewModel
import com.joshuahalvorson.datenight.viewmodel.YelpViewModelFactory
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.content_random_restaurant.*
import kotlinx.android.synthetic.main.fragment_random_restaurant.*
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject
import kotlin.random.Random

class RandomRestaurantFragment : Fragment(), OnMapReadyCallback {

    @Inject
    lateinit var yelpViewModelFactory: YelpViewModelFactory
    private lateinit var yelpViewModel: YelpViewModel
    private lateinit var deviceLocation: Location
    private lateinit var mMap: GoogleMap

    private var lastIndex = 0
    private var restaurantsList: ArrayList<Businesses> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_random_restaurant, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        App.app.yelpComponent.inject(this)
        yelpViewModel = ViewModelProviders.of(this, yelpViewModelFactory).get(YelpViewModel::class.java)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.restaurant_map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        getLocation()

        new_restaurant_button.setOnClickListener {
            displayRestaurant()
        }

    }

    private fun getRestaurants() {
        yelpViewModel.getLocalRestaurants("restaurant", deviceLocation.latitude, deviceLocation.longitude)
            .observe(this, Observer { responseBase ->
                restaurantsList.addAll(responseBase.businesses)
                displayRestaurant()
            })
    }

    private fun displayRestaurant() {
        restaurant_image_progress_circle.visibility = View.VISIBLE
        bottom_sheet_restaurant_details.visibility = View.VISIBLE
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
                        Toast.makeText(context, e?.localizedMessage, Toast.LENGTH_LONG).show()
                    }

                })
            restaurant_name.text = restaurantsList[index].name
            restaurant_location.text = "${restaurantsList[index].location?.display_address?.get(0)}" +
                    " ${restaurantsList[index].location?.display_address?.get(1)}"
            restaurant_price.text =
                if (restaurantsList[index].price == null) "No price given" else "${restaurantsList[index].price}"
            num_ratings.text = "Based on ${restaurantsList[index].review_count} reviews"
            loadRatingImage(restaurantsList[index])
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

        businesses.coordinates?.latitude?.let { latitude ->
            businesses.coordinates.longitude?.let { longitude ->
                val location = LatLng(latitude, longitude)
                mMap.addMarker(MarkerOptions().position(location).title(businesses.name))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isScrollGesturesEnabled = false
    }

    private fun loadRatingImage(businesses: Businesses) {
        when (businesses.rating) {
            0.0 -> restaurant_rating.loadImageWithPicasso(R.drawable.stars_small_0)
            1.0 -> restaurant_rating.loadImageWithPicasso(R.drawable.stars_small_1)
            1.5 -> restaurant_rating.loadImageWithPicasso(R.drawable.stars_small_1_half)
            2.0 -> restaurant_rating.loadImageWithPicasso(R.drawable.stars_small_2)
            2.5 -> restaurant_rating.loadImageWithPicasso(R.drawable.stars_small_2_half)
            3.0 -> restaurant_rating.loadImageWithPicasso(R.drawable.stars_small_3)
            3.5 -> restaurant_rating.loadImageWithPicasso(R.drawable.stars_small_3_half)
            4.0 -> restaurant_rating.loadImageWithPicasso(R.drawable.stars_small_4)
            4.5 -> restaurant_rating.loadImageWithPicasso(R.drawable.stars_small_4_half)
            5.0 -> restaurant_rating.loadImageWithPicasso(R.drawable.stars_small_5)
        }
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
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            getLocation()
        } else {
            val fusedLocationClient = context?.let { LocationServices.getFusedLocationProviderClient(it) }
            fusedLocationClient?.lastLocation?.addOnSuccessListener { location: Location? ->
                Log.i("LastLocation", location.toString())
                location?.let { deviceLocation = location }
                getRestaurants()
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        return EasyPermissions.hasPermissions(context!!, Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}

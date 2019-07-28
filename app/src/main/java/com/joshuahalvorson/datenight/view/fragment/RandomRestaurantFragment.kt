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
import com.daimajia.androidanimations.library.YoYo
import com.google.android.gms.location.LocationServices
import com.joshuahalvorson.datenight.App
import com.joshuahalvorson.datenight.R
import com.joshuahalvorson.datenight.model.Restaurants
import com.joshuahalvorson.datenight.viewmodel.ZomatoViewModel
import com.joshuahalvorson.datenight.viewmodel.ZomatoViewModelFactory
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_random_restaurant.*
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject
import kotlin.random.Random

class RandomRestaurantFragment : Fragment() {

    @Inject
    lateinit var zomatoViewModelFactory: ZomatoViewModelFactory
    private lateinit var zomatoViewModel: ZomatoViewModel
    private lateinit var restaurantsList: List<Restaurants>
    private lateinit var deviceLocation: Location

    private var lastIndex = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_random_restaurant, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        App.app.zomatoComponent.inject(this)
        zomatoViewModel = ViewModelProviders.of(this, zomatoViewModelFactory).get(ZomatoViewModel::class.java)

        getLocation()

        new_restaurant_button.setOnClickListener {
            displayRestaurant(restaurantsList)
        }

    }

    private fun getRestaurants() {
        zomatoViewModel.getLocalRestaurants(deviceLocation.latitude, deviceLocation.longitude, 50, 1)
            .observe(this, Observer {
                it?.let { restaurantResponse ->
                    Log.i("restaurantResponse", " ${restaurantResponse.restaurants?.size.toString()} restaurants")
                    restaurantResponse.restaurants?.let { list ->
                        restaurantsList = list
                        displayRestaurant(restaurantsList)
                    }
                }
            })
    }

    private fun displayRestaurant(list: List<Restaurants>) {
        restaurant_image_progress_circle.visibility = View.VISIBLE
        val index = Random.nextInt(0, list.size - 1)
        if (lastIndex != index) {
            Picasso.get()
                .load(
                    if (list[index].restaurant?.featured_image != "")
                        list[index].restaurant?.featured_image
                    else
                        list[index].restaurant?.photos?.get(0)?.photo?.url
                )
                .into(restaurant_image, object : Callback {
                    override fun onSuccess() {
                        restaurant_image_progress_circle.visibility = View.GONE
                    }

                    override fun onError(e: Exception?) {
                        Toast.makeText(context, e?.localizedMessage, Toast.LENGTH_LONG).show()
                    }

                })
            restaurant_name.text = list[index].restaurant?.name
            restaurant_location.text = "${list[index].restaurant?.location?.address}"
            restaurant_price.text = "Average cost for two: $${list[index].restaurant?.average_cost_for_two}"
            restaurant_rating.text = "Rating: ${list[index].restaurant?.user_rating?.aggregate_rating}/5"
            lastIndex = index
            if (restaurant_card.visibility == View.GONE) {
                animateRestaurantCardIn()
            }
        } else {
            displayRestaurant(list)
        }
    }

    private fun animateRestaurantCardIn() {
        YoYo.with(Techniques.FadeOut)
            .duration(500)
            .repeat(0)
            .playOn(progress_circle)
        progress_circle.visibility = View.GONE
        restaurant_card.visibility = View.VISIBLE
        YoYo.with(Techniques.FadeIn)
            .duration(500)
            .repeat(0)
            .playOn(restaurant_card)
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

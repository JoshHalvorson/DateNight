package com.joshuahalvorson.datenight.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.joshuahalvorson.datenight.App
import com.joshuahalvorson.datenight.R
import com.joshuahalvorson.datenight.viewmodel.ZomatoViewModel
import com.joshuahalvorson.datenight.viewmodel.ZomatoViewModelFactory
import kotlinx.android.synthetic.main.fragment_random_restaurant.*
import javax.inject.Inject

class RandomRestaurantFragment : Fragment() {

    @Inject
    lateinit var zomatoViewModelFactory: ZomatoViewModelFactory
    private lateinit var zomatoViewModel: ZomatoViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_random_restaurant, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        App.app.zomatoComponent.inject(this)
        zomatoViewModel = ViewModelProviders.of(this, zomatoViewModelFactory).get(ZomatoViewModel::class.java)

        zomatoViewModel.getLocalRestaurants(47.0, 41.0, 1, 1).observe(this, Observer { restaurantResponse ->
            restaurantResponse?.let {
                Log.i("restaurantResponse", it.restaurants?.get(0)?.restaurant?.name)
                restaurant_name.text = it.restaurants?.get(0)?.restaurant?.name
            }
        })
    }
}

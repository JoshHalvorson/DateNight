package com.joshuahalvorson.datenight.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.joshuahalvorson.datenight.R
import com.joshuahalvorson.datenight.adapter.RestaurantsViewedHistoryRecyclerViewAdapter
import com.joshuahalvorson.datenight.model.Businesses
import kotlinx.android.synthetic.main.fragment_restaurant_viewed_history_dialog.*
import android.view.WindowManager

private const val RESTAURANT_HISTORY = "restaurant_history"

class RestaurantViewedHistoryDialogFragment : DialogFragment() {
    private var restaurants: List<Businesses> = listOf()
    var onResult: ((restaurant: Businesses) -> Unit)? = null

    private lateinit var adapter: RestaurantsViewedHistoryRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            restaurants = it.getSerializable(RESTAURANT_HISTORY) as List<Businesses>
        }
        restaurants = restaurants.asReversed()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.fragment_restaurant_viewed_history_dialog,
            container,
            false
        )
    }

    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = RestaurantsViewedHistoryRecyclerViewAdapter(restaurants, object: RestaurantsViewedHistoryRecyclerViewAdapter.OnListItemClick {
            override fun onListItemClick(restaurant: Businesses?) {
                restaurant?.let { onResult?.invoke(it) }
                dismiss()
            }
        })
        restaurants_viewed_history_list.layoutManager = LinearLayoutManager(context)
        restaurants_viewed_history_list.adapter = adapter

    }

    companion object {
        @JvmStatic
        fun newInstance(restaurants: ArrayList<Businesses>) =
            RestaurantViewedHistoryDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(RESTAURANT_HISTORY, restaurants)
                }
            }
    }
}

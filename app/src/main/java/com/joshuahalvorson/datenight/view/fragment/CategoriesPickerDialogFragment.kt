package com.joshuahalvorson.datenight.view.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CheckBox
import androidx.core.view.get
import androidx.fragment.app.DialogFragment
import com.joshuahalvorson.datenight.R
import com.joshuahalvorson.datenight.util.RestaurantCategories
import com.joshuahalvorson.datenight.util.SharedPrefsHelper
import kotlinx.android.synthetic.main.fragment_categories_picker_dialog.*
import kotlinx.coroutines.selects.select

class CategoriesPickerDialogFragment : DialogFragment() {
    private val selectedCategories = mutableListOf<String>()
    var onResult: ((categories: String) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_categories_picker_dialog, container, false)
    }

    override fun onResume() {
        super.onResume()
        setWindowSize()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        populateLinearLayout()

        confirm_button.setOnClickListener {
            val commaSeperatedString = selectedCategories.joinToString { it }
            Log.i("categoriesResult", commaSeperatedString)
            SharedPrefsHelper(
                context?.getSharedPreferences(
                    SharedPrefsHelper.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE
                )
            ).put(SharedPrefsHelper.CATEGORIES_CSV_KEY, commaSeperatedString)
            onResult?.invoke(commaSeperatedString)
            dismiss()
        }
    }

    private fun getSelectedCategories() {
        val selectedCategoriesString = SharedPrefsHelper(
            context?.getSharedPreferences(
                SharedPrefsHelper.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE
            )
        ).get(SharedPrefsHelper.CATEGORIES_CSV_KEY, "")

        if (selectedCategoriesString != "") {
            val listCategories = selectedCategoriesString?.split(",")
            listCategories?.forEach {  category ->
                (categories_list_linear_layout[RestaurantCategories.RESTAURANT_CATEGORIES_CODES.indexOf(category.trim())] as CheckBox).isChecked = true
            }
        }

    }

    private fun populateLinearLayout() {
        RestaurantCategories.RESTAURANT_CATEGORIES_TITLES.forEach {
            categories_list_linear_layout.addView(constructCheckbox(it))
        }
        getSelectedCategories()
    }

    private fun constructCheckbox(title: String): CheckBox {
        val checkBox = CheckBox(context)
        checkBox.text = title
        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            when (isChecked) {
                true -> selectedCategories.add(
                    RestaurantCategories.RESTAURANT_CATEGORIES_CODES[
                            RestaurantCategories.RESTAURANT_CATEGORIES_TITLES.indexOf(checkBox.text.toString())
                    ]
                )
                false -> selectedCategories.remove(
                    RestaurantCategories.RESTAURANT_CATEGORIES_CODES[
                            RestaurantCategories.RESTAURANT_CATEGORIES_TITLES.indexOf(checkBox.text.toString())
                    ]
                )
            }
        }
        return checkBox
    }

    private fun setWindowSize() {
        val params = dialog?.window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }
}

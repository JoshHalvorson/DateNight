package com.joshuahalvorson.datenight.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.joshuahalvorson.datenight.network.ZomatoRepository
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class ZomatoViewModelFactory @Inject constructor(var zomatoRepository: ZomatoRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ZomatoViewModel(zomatoRepository) as T
    }

}
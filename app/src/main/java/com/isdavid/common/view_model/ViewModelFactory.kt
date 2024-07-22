package com.isdavid.common.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras


typealias VMF<T> = ViewModelFactory<T>

@Suppress("UNCHECKED_CAST")
abstract class ViewModelFactory<T : ViewModel> : ViewModelProvider.Factory {
    override fun <V : ViewModel> create(modelClass: Class<V>, extras: CreationExtras): V =
        build() as V

    abstract fun build(): T
}


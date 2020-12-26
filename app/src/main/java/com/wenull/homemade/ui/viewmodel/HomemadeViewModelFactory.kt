package com.wenull.homemade.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wenull.homemade.repositories.HomemadeRepository

class HomemadeViewModelFactory(private val repository: HomemadeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomemadeViewModel::class.java)){
                return HomemadeViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown View Model class")
    }
}
package com.pcforgeek.audiophile.home.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pcforgeek.audiophile.data.StorageMediaSource
import com.pcforgeek.audiophile.data.model.Category
import kotlinx.coroutines.launch
import javax.inject.Inject

class CategoryFeedViewModel @Inject constructor(private val storage: StorageMediaSource) :
    ViewModel() {

    private val _categoryItemLiveData = MutableLiveData<List<Category>>()
    val categoryItemLiveData: LiveData<List<Category>> = _categoryItemLiveData

    private lateinit var categoryId: String
    fun setCateoryId(categoryId: String) {
        this.categoryId = categoryId
        viewModelScope.launch {
            println("DEBUG - categoryId - $categoryId")
            _categoryItemLiveData.value = storage.getCategoryForParenId(categoryId)
        }
    }


}
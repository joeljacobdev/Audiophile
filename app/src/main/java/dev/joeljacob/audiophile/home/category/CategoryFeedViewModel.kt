package dev.joeljacob.audiophile.home.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.joeljacob.audiophile.data.StorageMediaSource
import dev.joeljacob.audiophile.data.model.Category
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class CategoryFeedViewModel @Inject constructor(private val storage: StorageMediaSource) :
    ViewModel() {

    private val _categoryItemLiveData = MutableLiveData<List<Category>>()
    val categoryItemLiveData: LiveData<List<Category>> = _categoryItemLiveData

    private lateinit var categoryId: String
    fun setCategoryId(categoryId: String) {
        this.categoryId = categoryId
        viewModelScope.launch {
            storage.getCategoryForParentId(categoryId).collect {
                _categoryItemLiveData.value = it
            }
        }
    }


}
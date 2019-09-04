package com.pcforgeek.audiophile.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pcforgeek.audiophile.data.model.CategoryItem

@Dao
interface CategoryDao {

    @Query("SELECT * FROM CategoryItem")
    suspend fun getAllCategoryItem(): List<CategoryItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(categoryItem: CategoryItem)

    @Query("SELECT * FROM CategoryItem WHERE type = 1")
    suspend fun getAllAlbums(): List<CategoryItem>

    @Query("SELECT * FROM CategoryItem WHERE type = 2")
    suspend fun getAllArtists(): List<CategoryItem>
}
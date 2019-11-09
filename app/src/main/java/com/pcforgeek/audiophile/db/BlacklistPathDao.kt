package com.pcforgeek.audiophile.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pcforgeek.audiophile.data.model.BlacklistPath

@Dao
interface BlacklistPathDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBlacklistPath(blacklistPath: BlacklistPath)

    @Delete
    suspend fun deleteBlacklistPath(blacklistPath: BlacklistPath)

    @Query("select * from BlacklistPath")
    fun getAllBlacklistPathLiveData(): LiveData<List<BlacklistPath>>

    @Query("select * from BlacklistPath")
    suspend fun getAllBlacklistPath(): List<BlacklistPath>
}
package com.sdzshn3.onesignalapi.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OneSignalAppsDao {

    @Query("SELECT * FROM onesignalapp")
    fun getAllLive(): LiveData<List<OneSignalApp>>

    @Query("SELECT * FROM onesignalapp")
    fun getAll(): List<OneSignalApp>

    @Insert
    suspend fun insert(oneSignalApp: OneSignalApp)

    @Delete
    suspend fun delete(oneSignalApp: OneSignalApp)
}
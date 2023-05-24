package com.sdzshn3.onesignalapi.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [OneSignalApp::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun oneSignalAppsDao(): OneSignalAppsDao
}
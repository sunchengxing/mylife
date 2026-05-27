package com.mylife.app

import android.app.Application
import com.mylife.app.data.AppDatabase

class MyLifeApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
}

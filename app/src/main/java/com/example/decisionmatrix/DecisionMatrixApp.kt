package com.example.decisionmatrix

import android.app.Application
import com.example.decisionmatrix.di.AppContainer

class DecisionMatrixApp : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

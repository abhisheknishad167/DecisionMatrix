package com.example.decisionmatrix.di

import android.content.Context
import androidx.room.Room
import com.example.decisionmatrix.data.db.AppDatabase
import com.example.decisionmatrix.data.repository.DecisionRepository

class AppContainer(context: Context) {

    // ✅ Database
    private val database: AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "decisions.db"
    ).build()

    // ✅ Repository
    val decisionRepository: DecisionRepository = DecisionRepository(
        database.decisionDao(),
        database.optionDao(),
        database.criterionDao(),
        database.scoreDao()
    )
}
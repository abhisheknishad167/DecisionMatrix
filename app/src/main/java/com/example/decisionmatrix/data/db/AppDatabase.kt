package com.example.decisionmatrix.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

import com.example.decisionmatrix.data.db.dao.CriterionDao
import com.example.decisionmatrix.data.db.dao.DecisionDao
import com.example.decisionmatrix.data.db.dao.OptionDao
import com.example.decisionmatrix.data.db.dao.ScoreDao
import com.example.decisionmatrix.data.model.Criterion
import com.example.decisionmatrix.data.model.Decision
import com.example.decisionmatrix.data.model.Option
import com.example.decisionmatrix.data.model.Score

@Database(
    entities = [
        Decision::class,
        Option::class,
        Criterion::class,
        Score::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun decisionDao(): DecisionDao
    abstract fun optionDao(): OptionDao
    abstract fun criterionDao(): CriterionDao
    abstract fun scoreDao(): ScoreDao
}
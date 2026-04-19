package com.example.decisionmatrix.data.db.dao

import androidx.room.Dao
import androidx.room.Upsert
import com.example.decisionmatrix.data.model.Score

@Dao
interface ScoreDao {

    // ✅ Insert or update automatically (based on primary key / unique constraint)
    @Upsert
    suspend fun upsert(score: Score)
}
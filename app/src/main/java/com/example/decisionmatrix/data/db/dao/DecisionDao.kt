package com.example.decisionmatrix.data.db.dao

import androidx.room.*
import com.example.decisionmatrix.data.db.relation.DecisionWithDetails
import com.example.decisionmatrix.data.model.Decision
import kotlinx.coroutines.flow.Flow

@Dao
interface DecisionDao {

    // ✅ Observe single decision with full details
    @Transaction
    @Query("SELECT * FROM Decision WHERE id = :id")
    fun observe(id: Long): Flow<DecisionWithDetails>

    // ✅ Observe all decisions (for list screen)
    @Transaction
    @Query("SELECT * FROM Decision ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<DecisionWithDetails>>

    // ✅ Insert returns Long (Room native behavior)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(decision: Decision): Long

    // ✅ Update existing decision
    @Update
    suspend fun update(decision: Decision)

    @Delete
    suspend fun delete(decision: Decision)
}
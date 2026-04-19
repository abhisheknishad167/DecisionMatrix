package com.example.decisionmatrix.data.db.dao

import androidx.room.*
import com.example.decisionmatrix.data.model.Criterion

@Dao
interface CriterionDao {

    // ✅ Insert single criterion
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(criterion: Criterion): Long

    // ✅ Insert multiple criteria
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(criteria: List<Criterion>)

    // ✅ Update (mainly weight or name)
    @Update
    suspend fun update(criterion: Criterion)

    // ✅ Delete
    @Delete
    suspend fun delete(criterion: Criterion)
}
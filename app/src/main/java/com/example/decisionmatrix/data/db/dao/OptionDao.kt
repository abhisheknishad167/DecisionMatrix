package com.example.decisionmatrix.data.db.dao

import androidx.room.*
import com.example.decisionmatrix.data.model.Option

@Dao
interface OptionDao {

    // ✅ Insert single option
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(option: Option): Long

    // ✅ Insert multiple options (used when creating decision)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(options: List<Option>)

    // ✅ Update option name
    @Update
    suspend fun update(option: Option)

    // ✅ Delete option
    @Delete
    suspend fun delete(option: Option)

    @Query("DELETE FROM Option WHERE decisionId = :decisionId")
    suspend fun deleteByDecisionId(decisionId: Long)
}
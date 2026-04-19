package com.example.decisionmatrix.data.repository

import com.example.decisionmatrix.data.db.dao.CriterionDao
import com.example.decisionmatrix.data.db.dao.DecisionDao
import com.example.decisionmatrix.data.db.dao.OptionDao
import com.example.decisionmatrix.data.db.dao.ScoreDao
import com.example.decisionmatrix.data.db.relation.DecisionWithDetails
import com.example.decisionmatrix.data.model.Criterion
import com.example.decisionmatrix.data.model.Decision
import com.example.decisionmatrix.data.model.Option
import com.example.decisionmatrix.data.model.Score
import kotlinx.coroutines.flow.Flow

class DecisionRepository(
    private val decisionDao: DecisionDao,
    private val optionDao: OptionDao,
    private val criterionDao: CriterionDao,
    private val scoreDao: ScoreDao
) {

    fun observeDecision(decisionId: Long): Flow<DecisionWithDetails> =
        decisionDao.observe(decisionId)

    fun observeAllDecisions(): Flow<List<DecisionWithDetails>> =
        decisionDao.observeAll()

    suspend fun insertDecision(decision: Decision): Long =
        decisionDao.insert(decision)

    suspend fun updateDecision(decision: Decision) =
        decisionDao.update(decision)

    suspend fun deleteDecision(decision: Decision) =
        decisionDao.delete(decision)

    suspend fun insertOptions(options: List<Option>) =
        optionDao.insertAll(options)

    suspend fun replaceOptions(decisionId: Long, names: List<String>) {
        optionDao.deleteByDecisionId(decisionId)
        optionDao.insertAll(names.map { Option(decisionId = decisionId, name = it) })
    }

    suspend fun insertCriteria(criteria: List<Criterion>) =
        criterionDao.insertAll(criteria)

    suspend fun updateCriterion(criterion: Criterion) =
        criterionDao.update(criterion)

    suspend fun deleteCriterion(criterion: Criterion) =
        criterionDao.delete(criterion)

    suspend fun upsertScore(score: Score) =
        scoreDao.upsert(score)
}
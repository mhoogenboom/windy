package com.robinfinch.windy.db

import java.io.*

internal class ExerciseIndex(dataDir: File) {

    companion object {

        val idComparator = compareBy<ScoreCard> { it.exerciseId }

        val scoreComparator = compareBy<ScoreCard> { it.passCount + it.failCount }
                    .thenByDescending { it.failCount }
                    .thenBy { it.exerciseId }
    }

    private val indexFile = File(dataDir, "exercise_index.ser")

    private val index: MutableList<ScoreCard>

    init {
        index =
                if (indexFile.exists()) {
                    ObjectInputStream(FileInputStream(indexFile)).use { ois ->
                        ois.readObject() as MutableList<ScoreCard>
                    }
                } else {
                    ArrayList<ScoreCard>()
                }
    }

    fun insert(id: Long) {

        index.add(ScoreCard(id))
        flush()
    }

    fun countPass(id: Long) {

        val i = index.binarySearch(ScoreCard(id), idComparator)
        if (i >= 0) {
            index[i].passCount++
            flush()
        }
    }

    fun countFail(id: Long) {

        val i = index.binarySearch(ScoreCard(id), idComparator)
        if (i >= 0) {
            index[i].failCount++
            flush()
        }
    }

    private fun flush() {
        ObjectOutputStream(FileOutputStream(indexFile)).use { oos ->
            oos.writeObject(index)
        }
    }

    fun find(count: Int) = index.sortedWith(scoreComparator).take(count).map(ScoreCard::exerciseId)
}

internal class ScoreCard(val exerciseId: Long) {
    var passCount = 0
    var failCount = 0
}
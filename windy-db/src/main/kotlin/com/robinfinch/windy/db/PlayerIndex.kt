package com.robinfinch.windy.db

import java.io.*

internal class PlayerIndex(dataDir: File) {

    private val indexFile = File(dataDir, "player_index.ser")

    private val index: MutableMap<String, GamesPlayed>

    init {
        index =
                if (indexFile.exists()) {
                    ObjectInputStream(FileInputStream(indexFile)).use { ois ->
                        ois.readObject() as MutableMap<String, GamesPlayed>
                    }
                } else {
                    HashMap<String, GamesPlayed>()
                }
    }

    fun insert(white: String, black: String, id: Long) {

        find(white).withWhite.add(id)

        find(black).withBlack.add(id)

        flush()
    }

    private fun flush() {

        ObjectOutputStream(FileOutputStream(indexFile)).use { oos ->
            oos.writeObject(index)
        }
    }

    fun find(name: String): GamesPlayed {
        return index.getOrPut(name, { GamesPlayed(mutableListOf(), mutableListOf()) })
    }
}

internal class GamesPlayed(val withWhite: MutableList<Long>, val withBlack: MutableList<Long>) : Serializable {

}
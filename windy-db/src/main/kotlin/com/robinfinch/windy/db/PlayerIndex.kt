package com.robinfinch.windy.db

import org.apache.commons.lang.StringUtils
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

    fun findFuzzy(name: String, maxDistance: Int = 20): GamesPlayed {

        val nearestPlayers = Array<Player?>(3, { null })

        for (key in index.keys) {

            val distance = StringUtils.getLevenshteinDistance(key, name)

            if (distance <= maxDistance) {
                var i = nearestPlayers.size

                while ((i > 0) && (nearestPlayers[i - 1] further distance)) {
                    if (i < nearestPlayers.size) {
                        nearestPlayers[i] = nearestPlayers[i - 1]
                    }
                    i--
                }
                if (i < nearestPlayers.size) {
                    nearestPlayers[i] = Player(key, distance)
                }
            }
        }

        val gamesPlayed = GamesPlayed(mutableListOf(), mutableListOf())

        nearestPlayers.map { p -> if (p == null) GamesPlayed(mutableListOf(), mutableListOf()) else find(p.key) }
                .forEach {
                    gamesPlayed.withWhite.addAll(it.withWhite)
                    gamesPlayed.withBlack.addAll(it.withBlack)
                }

        return gamesPlayed
    }

    infix fun Player?.further(distance: Int) =
            if (this == null) true else (this.distance > distance)
}

internal class GamesPlayed(val withWhite: MutableList<Long>, val withBlack: MutableList<Long>) : Serializable

internal class Player(val key: String, val distance: Int)
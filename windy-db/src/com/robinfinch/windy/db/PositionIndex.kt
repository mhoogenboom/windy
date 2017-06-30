package com.robinfinch.windy.db

import com.robinfinch.windy.core.position.Position
import java.io.*

class PositionIndex(dataDir: File) {

    private val indexFile = File(dataDir, "position_index.ser")

    private val root: Node

    init {
        root =
                if (indexFile.exists()) {
                    ObjectInputStream(FileInputStream(indexFile)).use { ois ->
                        ois.readObject() as Node
                    }
                } else {
                    Tree(arrayOfNulls(5))
                }
    }

    fun insert(position: Position, id: Long) {

        find(position).add(id)

        ObjectOutputStream(FileOutputStream(indexFile)).use { oos ->
            oos.writeObject(root)
        }
    }

    fun find(position: Position): MutableList<Long> {

        var node = root

        for (square in 0..50) {
            node = node.child(position.index(square), {
                if (square == 50) Leaf(mutableListOf()) else Tree(arrayOfNulls(5))
            })
        }

        return node.games()
    }

}

interface Node {

    fun child(i: Int, createChild: () -> Node): Node

    fun games(): MutableList<Long>

}

class Tree(private val children: Array<Node?>) : Node {

    override fun child(i: Int, createChild: () -> Node): Node {
        if (children[i] == null) {
            children[i] = createChild()
        }
        return children[i] as Node
    }

    override fun games(): MutableList<Long> {
        throw UnsupportedOperationException("Trees don't store games")
    }
}

class Leaf(private val games: MutableList<Long>) : Node {

    override fun child(i: Int, createChild: () -> Node): Node {
        throw UnsupportedOperationException("Leafs don't have children")
    }

    override fun games() = games
}
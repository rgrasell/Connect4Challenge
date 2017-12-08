package com.grasell

import kotlinx.collections.immutable.*

/**
 * This represents the state of a Connect 4 "board".
 * It is immutable, and will return a new game board with 1 move applied.  Call withMove().
 */
class Connect4Board(val width: Int, val height: Int, val columns: ImmutableList<ImmutableList<Piece?>>, val players: ImmutableSet<Player>, val turnsTaken: Int, val winningSequenceLength: Int, val gameState: GameState) {

    /**
     * Returns an independent copy of the this board, but with 1 move applied to it.
     * Throws IllegalMoveException if a column is full or if a nonexistent column is selected.
     */
    fun withMove(x: Int, player: Player): Connect4Board {
        if (x >= width) throw IllegalMoveException("Column $x doesn't exist.")
        val newColumns =  columns.asSequence().zip((0 until columns.size).asSequence())
                .map { (column, index) ->
                    if (index == x) {
                        val newPieceIndex = column.indexOf(null)
                        if (newPieceIndex == -1) throw IllegalMoveException("Column $x is full.")
                        column.set(newPieceIndex, Piece(player))
                    } else {
                        column
                    }
                }.toImmutableList()

        val newGameState = when (gameState) {
            is Won -> gameState
            is Tie -> gameState
            is InProgress -> calculateGameStateOptimized(newColumns, x, turnsTaken + 1, winningSequenceLength)
            else -> throw IllegalMoveException("GameState got into weird state: $gameState") // This is a strange condition that should never happen
        }

        return Connect4Board(width, height, newColumns, players + player, turnsTaken + 1, winningSequenceLength, newGameState)
    }

    /**
     * This method allows easy retrieval of game piece: board[x][y]
     */
    operator fun get(x: Int) = columns[x]

    /**
     * Search only the latest placed piece for a winner.
     * Only works if called after every move, but is fast.
     */
    private fun calculateGameStateOptimized(columns: List<List<Piece?>>, x: Int, turnsTaken: Int, winningSequenceLength: Int): GameState {
        // Find the last piece placed
        val y = columns[x].indexOfLast { it != null }
        val piece = columns[x][y]
        val player = piece!!.owner

        // TODO: more elegant combination of directions potentially like:
        // sequenceOf(-1, 0, 1).zip(sequenceOf(-1, 0))

        val hasUpLeftSequence = searchforSequence(x, y, -1, 1, columns, winningSequenceLength, player)
        val hasUpRightSequence = searchforSequence(x, y, 1, 1, columns, winningSequenceLength, player)
        val hasDownLeftSequence = searchforSequence(x, y, -1, -1, columns, winningSequenceLength, player)
        val hasDownRightSequence = searchforSequence(x, y, 1, -1, columns, winningSequenceLength, player)
        val hasLeftSequence = searchforSequence(x, y, -1, 0, columns, winningSequenceLength, player)
        val hasRightSequence = searchforSequence(x, y, 1, 0, columns, winningSequenceLength, player)
        val hasDownSequence = searchforSequence(x, y, 0, -1, columns, winningSequenceLength, player)

        val hasSequence = hasUpLeftSequence || hasUpRightSequence || hasDownLeftSequence || hasDownRightSequence || hasLeftSequence || hasRightSequence || hasDownSequence

        if (hasSequence) {
            return Won(player)
        }

        if (turnsTaken == columns.size * columns[0].size) {
            return Tie()
        }

        return InProgress()
    }

    tailrec private fun searchforSequence(x: Int, y: Int, xDirection: Int, yDirection: Int, columns: List<List<Piece?>>, winningSequenceLength: Int, player: Player): Boolean {
        if (winningSequenceLength == 0) return true

        if (x < 0 || x > columns.lastIndex) return false
        if (y < 0 || y > columns[0].lastIndex) return false
        if (columns[x][y]?.owner != player) return false

        return searchforSequence(x + xDirection, y + yDirection, xDirection, yDirection, columns, winningSequenceLength - 1, player)
    }

    /**
     * Looks at a sequence of pieces and determines if winningLength of them in a row belong to player
     */
    private fun hasXInARow(input: Sequence<Piece?>, player: Player, winningLength: Int): Boolean {
        var inARow = 0

        input.forEach {
            when (it?.owner) {
                player -> inARow++
                else -> inARow = 0
            }
            if (inARow == winningLength) return true
        }

        return false
    }

    // Class to conveniently store the column and row of a piece
    data class Cell(val piece: Piece?, val column: Int, val row: Int)

    /**
     * Creates a sequence of the board, with each element being a Cell object described above.
     */
    fun cellSequence(): Sequence<Cell> {
        return object : Iterator<Cell> {
            private var columnIndex = 0
            private var rowIndex = 0

            override fun hasNext(): Boolean = (columnIndex < columns.size)

            override fun next(): Cell {
                val cell = Cell(columns[columnIndex][rowIndex], columnIndex, rowIndex)
                rowIndex++
                if (rowIndex >= columns[columnIndex].size) {
                    columnIndex++
                    rowIndex = 0
                }
                return cell
            }

        }.asSequence()
    }

     fun humanRepresentation(xPlayer: Player): String {
        return cellSequence().groupBy { it.row }.asSequence()
                .sortedByDescending { it.key }
                .map { it.value.joinToString(prefix = "| ", postfix = " |", separator = " | ") { if (it.piece?.owner == xPlayer) "x" else if (it.piece?.owner == null) "_" else "o" } }
                .joinToString(separator = "\n") { it }
    }

    open class GameState
    class InProgress : GameState()
    class Won(val winner: Player) : GameState()
    class Tie : GameState()
}

/**
 * Creates an empty game board with the specified dimensions.
 */
fun initializeBoard(width: Int, height: Int, winningSequenceLength: Int): Connect4Board {
    val backingLists = (0 until width).asSequence()
            .map { (0 until height).asSequence().map { null as Piece? }.toImmutableList() }
            .toImmutableList()

    return Connect4Board(width, height, backingLists, immutableSetOf(), 0, winningSequenceLength, Connect4Board.InProgress())
}

data class Piece(val owner: Player)

class IllegalMoveException(explanation: String) : Exception(explanation)


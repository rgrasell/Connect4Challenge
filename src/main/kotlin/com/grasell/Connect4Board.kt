package com.grasell

import kotlinx.collections.immutable.*

/**
 * This represents the state of a Connect 4 "board".
 * It is immutable, but will return a new game board with 1 move applied.  Call withMove().
 */
class Connect4Board(val width: Int, val height: Int, val columns: ImmutableList<ImmutableList<Piece?>>, val players: ImmutableList<Player>, val turnsTaken: Int) {
    /**
     * Returns an independent copy of the this board, but with 1 move applide to it.
     * Throws IllegalMoveException if a column is full or if a nonexistant column is selected.
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

        return Connect4Board(width, height, newColumns, players + player, turnsTaken + 1)
    }

    /**
     * This method allows easy retrieval of game piece: board[x][y]
     */
    operator fun get(x: Int) = columns[x]

    fun getGameState(winningSequenceLength: Int): GameState {

        players.forEach { currentPlayer ->
            val winsByColumns = this.columns.asSequence()
                    .map { hasXInARow(it.asSequence(), currentPlayer, winningSequenceLength) }
                    .filter { it }
                    .any()

            val winsByRows = this.columns.asSequence()
                    .flatMap { it.asSequence().zip((0 until it.size).asSequence()) }
                    .groupBy { it.second }.asSequence()
                    .map { hasXInARow(it.value.asSequence().map { it.first }, currentPlayer, winningSequenceLength) }
                    .filter { it }
                    .any()

            // Class to conveniently store the column and row of a piece
            data class Cell(val piece: Piece?, val column: Int, val row: Int)

            val winsByUpwardDiagonal = this.columns.asSequence().zip((0 until columns.size).asSequence())
                    .flatMap { (column, columnIndex) ->
                        column.asSequence()
                                .zip((0 until column.size).asSequence())
                                .map { (piece, rowIndex) ->
                                    Cell(piece, columnIndex, rowIndex)
                                }
                    }
                    .groupBy { it.column - it.row }.asSequence()
                    .map { hasXInARow(it.value.asSequence().map { it.piece }, currentPlayer, winningSequenceLength) }
                    .any()

            val winsByDownwardDiagonal = this.columns.asSequence().zip((0 until columns.size).asSequence())
                    .flatMap { (column, columnIndex) ->
                        column.asSequence()
                                .zip((0 until column.size).asSequence())
                                .map { (piece, rowIndex) ->
                                    Cell(piece, columnIndex, rowIndex)
                                }
                    }
                    .groupBy { it.column + it.row }.asSequence()
                    .map { hasXInARow(it.value.asSequence().map { it.piece }, currentPlayer, winningSequenceLength) }
                    .any()

            if (winsByRows || winsByColumns || winsByUpwardDiagonal || winsByDownwardDiagonal) return Won(currentPlayer)
        }

        // check for a tie
        if (turnsTaken == width * height) {
            return Tie()
        }

        // No winner on this board
        return InProgress()
    }

    private fun hasXInARow(input: Sequence<Piece?>, forPlayer: Player, winningLength: Int): Boolean {
        var inARow = 0

        input.forEach {
            when (it?.owner) {
                forPlayer -> inARow++
                else -> inARow = 0
            }
            if (inARow == winningLength) return true
        }

        return false
    }

    open class GameState
    class InProgress : GameState()
    class Won(val winner: Player) : GameState()
    class Tie : GameState()

}

/**
 * Creates an empty game board with the specified dimensions.
 */
fun initiateBoard(width: Int, height: Int): Connect4Board {
    val backingLists = (0 until width).asSequence()
            .map { (0 until height).asSequence().map { null as Piece? }.toImmutableList() }
            .toImmutableList()

    return Connect4Board(width, height, backingLists, immutableListOf(), 0)
}

data class Piece(val owner: Player)

class IllegalMoveException(explanation: String) : Exception(explanation)


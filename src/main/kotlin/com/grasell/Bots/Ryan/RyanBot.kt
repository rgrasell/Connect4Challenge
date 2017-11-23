package com.grasell.Bots.Ryan

import com.grasell.Connect4Board
import com.grasell.Piece
import com.grasell.Player

class RyanBot : Player {
    override fun takeTurn(board: Connect4Board, opponent: Player, turnCallback: (Int) -> Unit) {
        // TODO: pass in a real winningSequenceLength value
        val winningSequenceLength = 4

        val boardToGameStates = { gameState: Connect4Board, ourTurn: Boolean ->
            (0 until gameState.width).asSequence()
                    .filter { gameState[it].last() == null }
                    .map { it to gameState.withMove(it, if (ourTurn) this else opponent) }
        }

        // TODO: A real static analysis algorithm
        val staticAnalysis = { gameState: Connect4Board ->
            countExposedPieces(gameState)
        }

        val checkGameResolution = { gameState: Connect4Board ->
            when (gameState.getGameState(winningSequenceLength)) {
                is Connect4Board.Won -> GameResolution.WIN
                is Connect4Board.Tie -> GameResolution.TIE
                else -> null
            }
        }

        (0 .. Int.MAX_VALUE).forEach {
            val bestMove = calculateBestMove(board, boardToGameStates, staticAnalysis, checkGameResolution, it)!!
            turnCallback(bestMove)
            println("$name got to depth $it")
        }


    }

    override val name = "Ryan's bot"

    private fun countExposedPieces(board: Connect4Board): Int {
        return board.cellSequence()
                .filter { it.piece?.owner == this }
                .map { neighbors(it, board) }
                .filter {
                    it.filter { it != null }.count() > 0
                }
                .count()

    }

    private fun neighbors(cell: Connect4Board.Cell, board: Connect4Board): Sequence<Piece?> {
        // pairs of X to Y
        return sequenceOf(0 to 1, 1 to 1, 1 to 0 )
                .flatMap { sequenceOf(it, -it.first to -it.second) }
                .map { cell.column + it.first to cell.row + it.second }
                .filter { it.first > 0 && it.first < board.width }
                .filter { it.second > 0 && it.second < board.height }
                .map { board[it.first][it.second] }
    }

}
package com.grasell.Bots.Ryan

import com.grasell.Connect4Board
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
            (0 until winningSequenceLength).asSequence()
                    .map { it to gameState.getGameState(it) }
                    .sumBy { it.first }
        }

        val checkGameResolution = { gameState: Connect4Board ->
            when (board.getGameState(winningSequenceLength)) {
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
}
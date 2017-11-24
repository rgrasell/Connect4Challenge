package com.grasell.Bots.Example

import com.grasell.Connect4Board
import com.grasell.Player

class ExampleBot : Player {
    override fun takeTurn(board: Connect4Board, opponent: Player, winningSequenceLength: Int, turnCallback: (Int) -> Unit) {
        // Feel free to use the withMoves method on the board to simulate the outcome of moves
        // Note that this has no effect on the game at large. Until turnCallback() is called, the official game board is unaltered.
        val newBoard = board.withMove(3, this) // Simulates the outcome of placing a piece at column index 3

        // Do whatever calculations you want, then call turnCallback() with your move
        turnCallback(3) // Just pass in what column you want to drop a piece into.

        // You can even call the callback multiple times, and only the last one is taken by the system
        turnCallback(4)

        // In our example, the outcome is only that we place a piece at column 4.
        // It is impossible to take more than 1 move, no matter how many times we call the callback.
    }

    override val name = "Example player"

}
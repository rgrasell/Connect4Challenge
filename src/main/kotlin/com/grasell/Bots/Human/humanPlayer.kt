package com.grasell.Bots.Human

import com.grasell.Connect4Board
import com.grasell.Player
import java.util.*

class HumanPlayer : Player {
    override fun takeTurn(board: Connect4Board, opponent: Player, winningSequenceLength: Int, turnCallback: (Int) -> Unit) {
        println("Current board: ")
        println(board.humanRepresentation(this))
        print("Enter column number to drop piece: ")

        with(Scanner(System.`in`)) {
            turnCallback(nextInt())
        }
    }

    override val name = "human player"

}
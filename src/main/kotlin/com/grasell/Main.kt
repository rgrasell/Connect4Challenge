package com.grasell

fun main(args: Array<String>) {
    println("Hello, World")

    val board = initiateBoard(4, 4)
    val player1 = object : Player {
        override val name = "player1"
        override fun takeTurn(board: Connect4Board, turnCallback: (Int) -> Unit) = turnCallback(1)
    }
    val player2 = object : Player {
        override val name = "player2"
        override fun takeTurn(board: Connect4Board, turnCallback: (Int) -> Unit) = turnCallback(2)
    }

    val winner = Connect4GameRunner(player1, player2).run()

    println(winner)

}
package com.grasell

fun main(args: Array<String>) {
    println("Hello, World")

    val board = initializeBoard(4, 4)
    val player1 = object : Player {
        override val name = "player1"
        override fun takeTurn(board: Connect4Board, opponent: Player, turnCallback: (Int) -> Unit) = turnCallback(1)
    }
    val player2 = object : Player {
        override val name = "player2"
        override fun takeTurn(board: Connect4Board, opponent: Player, turnCallback: (Int) -> Unit) {
            Thread.sleep(100_000)
            turnCallback(2)
        }
    }

    val winner = simulateGame(player1, player2, 10_000)

    println("Winner: ${winner?.name}")

}
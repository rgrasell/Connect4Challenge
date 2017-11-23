package com.grasell

import com.grasell.Bots.Human.HumanPlayer

fun main(args: Array<String>) {
    println("Hello, World")

    val board = initializeBoard(4, 4)
    val player1 = HumanPlayer()
    val player2 = HumanPlayer()

    val winner = simulateGame(player1, player2, 10_000)

    println("Winner: ${winner?.name}")

}
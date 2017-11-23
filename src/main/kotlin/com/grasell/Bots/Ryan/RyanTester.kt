package com.grasell.Bots.Ryan

import com.grasell.Bots.Ryan.RyanBot
import com.grasell.Player
import com.grasell.initializeBoard
import com.grasell.simulateGame

fun main(args: Array<String>) {
    println("Hello, World")

    val board = initializeBoard(4, 4)
    val player1 = RyanBot()
    val player2 = RyanBot()

    val winner = simulateGame(player1, player2)

    println("Winner: ${winner?.name}")

}
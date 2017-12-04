package com.grasell.Bots.Ryan

import com.grasell.simulateGame
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    println("Hello, World")

    val player1 = RyanBot("x")
    val player2 = RyanBot("o")

    val time = measureTimeMillis {
        val winner = simulateGame(player1, player2, 5_000)
        println("Winner: ${winner?.name}")
    }

    println("Completed in $time")
}
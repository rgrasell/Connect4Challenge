package com.grasell

interface Player {
    fun takeTurn(board: Connect4Board, opponent: Player, turnCallback: (Int) -> Unit)
    val name: String
}
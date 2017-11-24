package com.grasell

interface Player {
    fun takeTurn(board: Connect4Board, opponent: Player, winningSequenceLength: Int, turnCallback: (Int) -> Unit)
    val name: String
}
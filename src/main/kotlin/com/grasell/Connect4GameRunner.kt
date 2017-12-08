package com.grasell

/**
 * Run a game by executing each players turn until a winner is found, a player forfeits by throwing an exception,
 * or the board fills up and draws.
 * Returns the player who won, or null for a draw.
 */
fun simulateGame(player1: Player, player2: Player, turnLength: Long, width: Int = 7, height: Int = 6, winningSequenceLength: Int = 4): Player? {
    var board = initializeBoard(width, height, winningSequenceLength)
    var player1Turn = true

    // Give each player a turn until the game ends by a win or draw
    while (true) {
        val currentPlayer = if (player1Turn) player1 else player2
        val waitingPlayer = if (player1Turn) player2 else player1

        // Execute the player's turn in a new thread
        var playersChosenColumn: Int? = null
        val playingThread = Thread({
                    currentPlayer.takeTurn(board, waitingPlayer, winningSequenceLength) {
                        playersChosenColumn = it
                    }
                })

        playingThread.start()
        playingThread.join(turnLength)
        playingThread.stop() // Forcibly kill the thread

        try {
            if (playersChosenColumn != null) {
                board = board.withMove(playersChosenColumn!!, currentPlayer)
            } else {
                return waitingPlayer
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return waitingPlayer
        }

        println("${currentPlayer.name} took their turn and produced: (${board.hashCode()})")
        println(board.humanRepresentation(player1))

        // Check endgame conditions
        val resolution = board.gameState
        when (resolution) {
            is Connect4Board.Won -> return resolution.winner
            is Connect4Board.Tie -> return null
        }

        player1Turn = !player1Turn
    }
}
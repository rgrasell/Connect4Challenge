package com.grasell

import org.junit.Test

import org.junit.Assert.*

class Connect4BoardTest {

    private val testPlayer1 = object : Player {
        override fun takeTurn(board: Connect4Board, opponent: Player, winningSequenceLength: Int, turnCallback: (Int) -> Unit) = TODO("not neccessary for these tests") //To change body of created functions use File | Settings | File Templates.
        override val name = "testPlayer1"
    }

    private val testPlayer2 = object : Player {
        override fun takeTurn(board: Connect4Board, opponent: Player, winningSequenceLength: Int, turnCallback: (Int) -> Unit) = TODO("not neccessary for these tests") //To change body of created functions use File | Settings | File Templates.
        override val name = "testPlayer1"
    }

    @Test
    fun initializeBoard() {
        val board = initializeBoard(5, 10, 4)

        assertEquals(5, board.columns.size)

        board.columns.forEach {
            assertEquals(10, it.size)
        }
    }

    @Test
    fun withMove() {
        val board = initializeBoard(5, 5, 4)
                .withMove(0, testPlayer1)
                .withMove(0, testPlayer1)
                .withMove(1, testPlayer1)

        assertEquals(testPlayer1, board[0][0]?.owner)
        assertEquals(testPlayer1, board[0][1]?.owner)
        assertEquals(testPlayer1, board[1][0]?.owner)
    }

    @Test(expected = IllegalMoveException::class)
    fun withMove_NoSuchColumn() {
        val board = initializeBoard(5, 5, 4)

        board.withMove(5, testPlayer1)
    }

    @Test(expected = IllegalMoveException::class)
    fun withMove_FullColumn() {
        initializeBoard(5, 5, 4)
                .withMove(0, testPlayer1)
                .withMove(0, testPlayer1)
                .withMove(0, testPlayer1)
                .withMove(0, testPlayer1)
                .withMove(0, testPlayer1)
                .withMove(0, testPlayer1)
    }

    @Test()
    fun getGameState_SimpleColumn() {
        val board = initializeBoard(10, 10, 4)
                .withMove(0, testPlayer1)
                .withMove(0, testPlayer1)
                .withMove(0, testPlayer1)
                .withMove(0, testPlayer1)

        val state = board.gameState as Connect4Board.Won
        assertTrue(state.winner == testPlayer1)
    }

    @Test()
    fun getGameState_BigColumn() {
        val board = initializeBoard(1000, 1000, 4)
                .withMove(999, testPlayer1)
                .withMove(999, testPlayer1)
                .withMove(999, testPlayer1)
                .withMove(999, testPlayer1)

        val state = board.gameState as Connect4Board.Won
        assertTrue(state.winner == testPlayer1)
    }

    // TODO: Why is this test so slow? About 50x slower than the other tests
    // Working theory: JVM warmup.  A copy of this testcase runs in the usual 1-2ms.
    @Test()
    fun getGameState_SimpleRow() {
        val board = initializeBoard(10, 10, 4)
                .withMove(0, testPlayer1)
                .withMove(1, testPlayer1)
                .withMove(2, testPlayer1)
                .withMove(3, testPlayer1)

        val state = board.gameState as Connect4Board.Won
        assertTrue(state.winner == testPlayer1)
    }

    @Test()
    fun getGameState_SimpleUpwardDiagonal() {
        val board = initializeBoard(10, 10, 4)
                .withMove(0, testPlayer1)
                .withMove(1, testPlayer2)
                .withMove(1, testPlayer1)
                .withMove(2, testPlayer2)
                .withMove(2, testPlayer2)
                .withMove(2, testPlayer1)
                .withMove(3, testPlayer2)
                .withMove(3, testPlayer1)
                .withMove(3, testPlayer1)
                .withMove(3, testPlayer1)

        val state = board.gameState as Connect4Board.Won
        assertTrue(state.winner == testPlayer1)
    }

}
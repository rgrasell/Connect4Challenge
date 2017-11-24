package com.grasell.Bots.Ryan

fun <GameState, Move> calculateBestMove(
        gameState: GameState,
        enumerateOptions: (GameState, Boolean) -> Sequence<Pair<Move, GameState>>,
        staticAnalysis: (GameState) -> Int,
        checkGameResolution: (GameState) -> GameResolution?,
        maxDepth: Int): Move? {

    return enumerateOptions(gameState, true)
            .maxBy { calculateUtilityRecursive(it.second, enumerateOptions, staticAnalysis, checkGameResolution, true, 0, maxDepth, Int.MIN_VALUE, Int.MAX_VALUE) }?.first
}


private fun <GameState, Move> calculateUtilityRecursive(
        gameState: GameState,
        enumerateOptions: (GameState, Boolean) -> Sequence<Pair<Move, GameState>>,
        staticAnalysis: (GameState) -> Int,
        checkGameResolution: (GameState) -> GameResolution?,
        maximizingPlayer: Boolean,
        depth: Int,
        maxDepth: Int,
        alpha: Int,
        beta: Int): Int {

    checkGameResolution(gameState)?.let {
        return when (it) {
            GameResolution.WIN -> Int.MAX_VALUE
            GameResolution.LOSS -> Int.MIN_VALUE
            GameResolution.TIE -> 0
        }
    }

    if (depth == maxDepth) return staticAnalysis(gameState)

    var mutableAlpha = alpha
    var mutableBeta = beta
    var v = 0

    if (maximizingPlayer) {
        v = Int.MIN_VALUE
        for (move in enumerateOptions(gameState, maximizingPlayer)) {
            v = Math.max(v, calculateUtilityRecursive(move.second, enumerateOptions, staticAnalysis, checkGameResolution, !maximizingPlayer, depth + 1, maxDepth, mutableAlpha, mutableBeta))
            mutableAlpha = Math.max(mutableAlpha, v)
            if (mutableBeta <= mutableAlpha) break
        }
    } else {
        v = Int.MAX_VALUE
        for (move in enumerateOptions(gameState, false)) {
            v = Math.min(v, calculateUtilityRecursive(move.second, enumerateOptions, staticAnalysis, checkGameResolution, !maximizingPlayer, depth + 1, maxDepth, mutableAlpha, mutableBeta))
            mutableBeta = Math.min(mutableBeta, v)
            if (mutableBeta <= mutableAlpha) break
        }
    }

    return v

}

enum class GameResolution {
    WIN, LOSS, TIE
}
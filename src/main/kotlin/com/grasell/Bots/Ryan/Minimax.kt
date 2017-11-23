package com.grasell.Bots.Ryan

fun <GameState, Move> calculateBestMove(
        gameState: GameState,
        enumerateOptions: (GameState, Boolean) -> Sequence<Pair<Move, GameState>>,
        staticAnalysis: (GameState) -> Int,
        checkGameResolution: (GameState) -> GameResolution?,
        maxDepth: Int): Move? {

    return enumerateOptions(gameState, true)
            .maxBy { calculateUtilityRecursive(it.second, enumerateOptions, staticAnalysis, checkGameResolution, true, 0, maxDepth) }?.first
}


private fun <GameState, Move> calculateUtilityRecursive(
        gameState: GameState,
        enumerateOptions: (GameState, Boolean) -> Sequence<Pair<Move, GameState>>,
        staticAnalysis: (GameState) -> Int,
        checkGameResolution: (GameState) -> GameResolution?,
        ourTurn: Boolean,
        depth: Int,
        maxDepth: Int): Int {

    checkGameResolution(gameState)?.let {
        return when (it) {
            GameResolution.WIN -> Int.MAX_VALUE
            GameResolution.LOSS -> Int.MIN_VALUE
            GameResolution.TIE -> 0
        }
    }

    if (depth == maxDepth) return staticAnalysis(gameState)

    val subtrees = enumerateOptions(gameState, ourTurn)
            .map { it.second }
            .map { calculateUtilityRecursive(it, enumerateOptions, staticAnalysis, checkGameResolution, !ourTurn, depth + 1, maxDepth) }

    return (if (ourTurn) subtrees.max() else subtrees.min()) ?: staticAnalysis(gameState)

}

enum class GameResolution {
    WIN, LOSS, TIE
}
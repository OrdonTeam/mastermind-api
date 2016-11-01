package mastermind

import java.util.*

class MastermindService(val randomGameGenerator: () -> Mastermind, val repository: MastermindRepository) {
    val gamesStatuses = HashMap<String, List<Move>>()

    fun makeMove(id: String, guess: List<Int>): GameStatus {
        return GameStatus(addMoveToProperGame(id, guess))
    }

    private fun addMoveToProperGame(id: String, guess: List<Int>): List<Move> {
        val oldMoves = gamesStatuses.getOrDefault(id, emptyList<Move>())
        val moves = oldMoves + createMove(id, guess)
        gamesStatuses.put(id, moves)
        return moves
    }

    private fun createMove(id: String, guess: List<Int>): Move {
        val match = repository.getOrNewGame(id, randomGameGenerator).findMatch(ColorSet.from(guess))
        return Move(guess, match)
    }
}

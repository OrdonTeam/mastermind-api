package mastermind

class MastermindService(val randomGameGenerator: () -> Mastermind, val repository: MastermindRepository) {

    fun makeMove(id: String, guess: List<Int>): GameStatus {
        return GameStatus(addMoveToProperGame(id, guess))
    }

    private fun addMoveToProperGame(id: String, guess: List<Int>): List<Move> {
        return repository.addMove(id, createMove(id, guess))
    }

    private fun createMove(id: String, guess: List<Int>): Move {
        val match = repository.getOrNewGame(id, randomGameGenerator).findMatch(ColorSet.from(guess))
        return Move(guess, match)
    }
}

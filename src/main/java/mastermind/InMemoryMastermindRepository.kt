package mastermind

import java.util.*

class InMemoryMastermindRepository : MastermindRepository {

    val gamesStatuses = HashMap<String, List<Move>>()
    val games = HashMap<String, Mastermind>()

    override fun getOrNewGame(id: String, randomGameGenerator: () -> Mastermind): Mastermind {
        return games.getOrPut(id, randomGameGenerator)
    }

    override fun addMove(id: String, newMove: Move): List<Move> {
        val oldMoves = gamesStatuses.getOrDefault(id, emptyList<Move>())
        val moves = oldMoves + newMove
        gamesStatuses.put(id, moves)
        return gamesStatuses[id]!!
    }
}
package mastermind

import java.util.*

class InMemoryMastermindRepository : MastermindRepository {


    val games = HashMap<String, Mastermind>()

    override fun getOrNewGame(id: String, randomGameGenerator: () -> Mastermind): Mastermind {
        return games.getOrPut(id, randomGameGenerator)
    }
}
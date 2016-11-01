package mastermind

interface MastermindRepository {

    fun getOrNewGame(id: String, randomGameGenerator: () -> Mastermind): Mastermind
}
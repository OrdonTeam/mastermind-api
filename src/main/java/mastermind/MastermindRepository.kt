package mastermind

interface MastermindRepository {

    fun getOrNewGame(id: String, randomGameGenerator: () -> Mastermind): Mastermind

    fun addMove(id: String, newMove: Move): List<Move>
}
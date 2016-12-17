package mastermind

data class GameStatus(val moves: List<Move>)

data class Move(val guess: List<Int>, val match: MatchResult)

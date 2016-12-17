package mastermind

import org.junit.Ignore
import org.junit.Test
import java.util.*

class FirebaseMastermindRepositoryTest {

    @Test
    @Ignore
    fun shouldCreateNewGame() {
        println(FirebaseMastermindRepository().getOrNewGame("id", RandomGameGenerator({ Random().nextInt() })))
    }

    @Test
    @Ignore
    fun shouldAddMove() {
        println(FirebaseMastermindRepository().addMove("id", Move(listOf(2, 3, 4, 5), MatchResult(0, 1))))
    }
}

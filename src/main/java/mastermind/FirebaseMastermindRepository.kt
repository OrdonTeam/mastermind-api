package mastermind

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.reflect.TypeToken
import io.reactivex.Single
import java.io.FileInputStream
import java.util.*

class FirebaseMastermindRepository : MastermindRepository {

    private val options = FirebaseOptions.Builder()
            .setServiceAccount(FileInputStream("service-account-key.json"))
            .setDatabaseUrl("https://mastermind-a83ea.firebaseio.com")
            .build()
    private val app = FirebaseApp.initializeApp(options)
    private val ref = FirebaseDatabase.getInstance(app)
    private val movesTypeToken: TypeToken<HashMap<String, Move>> = object : TypeToken<HashMap<String, Move>>() {}

    override fun getOrNewGame(id: String, randomGameGenerator: () -> Mastermind): Mastermind {
        return getOldGame(id)
                .onErrorResumeNext{ putNewGame(id, randomGameGenerator()) }
                .blockingGet()
    }

    private fun getOldGame(id: String): Single<Mastermind> {
        return getGamesRef(id).getValue()
    }

    private fun putNewGame(id: String, game: Mastermind): Single<Mastermind> {
        return putValue(game, getGamesRef(id))
    }

    override fun addMove(id: String, newMove: Move): List<Move> {
        return putMove(id, newMove)
                .flatMap { getMoves(id) }
                .blockingGet()
    }

    private fun putMove(id: String, newMove: Move): Single<Move> {
        return putValue(newMove, getNewMoveRef(id))
    }

    private fun getMoves(id: String): Single<List<Move>> {
        return getMovesRef(id).getValue(movesTypeToken)
                .map { it.toSortedMap(Comparator<String>(String::compareTo)).map { it.value } }
    }

    private fun getNewMoveRef(id: String) = getMovesRef(id).child(System.currentTimeMillis().toString())

    private fun getGamesRef(id: String) = ref.getReference(id).child("game")

    private fun getMovesRef(id: String) = ref.getReference(id).child("moves")
}
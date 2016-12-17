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

    override fun getOrNewGame(id: String, randomGameGenerator: () -> Mastermind): Mastermind {
        return getGame(id)
                .onErrorResumeNext(putNewGame(id, randomGameGenerator))
                .blockingGet()
    }

    private fun getGame(id: String): Single<Mastermind> {
        return ref.getReference(id).child("game").getValue<ColorSet>().map(::Mastermind)
    }

    private fun putNewGame(id: String, randomGameGenerator: () -> Mastermind): Single<Mastermind> {
        val mastermind = randomGameGenerator.invoke()
        return putValue(mastermind.code, ref.getReference(id).child("game")).map { mastermind }
    }

    override fun addMove(id: String, newMove: Move): List<Move> {
        return putMove(id, newMove)
                .flatMap { getMoves(id) }
                .blockingGet()
    }

    private fun putMove(id: String, newMove: Move): Single<Unit> {
        return putValue(newMove, ref.getReference(id).child("moves").child(System.currentTimeMillis().toString()))
    }

    private fun getMoves(id: String): Single<List<Move>> {
        return ref.getReference(id).child("moves").getValue(object : TypeToken<HashMap<String, Move>>() {})
                .map { it.toSortedMap(Comparator<String>(String::compareTo)).map { it.value } }
    }
}
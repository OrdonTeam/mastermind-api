package mastermind

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.*
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
        return getValue<ColorSet>(ref.getReference(id).child("game")).map(::Mastermind)
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
        return Single.create({ e ->
            ref.getReference(id)
                    .child("moves")
                    .child(System.currentTimeMillis().toString())
                    .setValue(newMove.toHashMap(), DatabaseReference.CompletionListener { error, p1 ->
                        if (error != null) {
                            e.onError(error.toException())
                        } else {
                            e.onSuccess(Unit)
                        }
                    })
        })
    }

    private fun getMoves(id: String): Single<List<Move>> {
        return Single.create({ e ->
            ref.getReference(id).child("moves").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val value = dataSnapshot.getHashMapStringStringInt()
                    if (value != null) {
                        e.onSuccess(value.toMoves())
                    } else {
                        e.onSuccess(emptyList())
                    }
                }

                override fun onCancelled(error: DatabaseError) = e.onError(error.toException())
            })
        })
    }

    private fun Move.toHashMap() = mapOf("first" to guess[0], "second" to guess[1], "third" to guess[2], "fourth" to guess[3], "positionMatch" to match.positionMatch, "colorMatch" to match.colorMatch)

    private fun HashMap<String, HashMap<String, Int>>.toMoves() = this
            .toSortedMap(Comparator<String>(String::compareTo))
            .map {
                Move(listOf(it.value["first"]!!, it.value["second"]!!, it.value["third"]!!, it.value["fourth"]!!), MatchResult(it.value["positionMatch"]!!, it.value["colorMatch"]!!))
            }

    private fun Mastermind.toHashMap() = mapOf("first" to code.first, "second" to code.second, "third" to code.third, "fourth" to code.fourth)

    private fun DataSnapshot.getHashMapStringStringInt() = getValue(object : GenericTypeIndicator<HashMap<String, HashMap<String, Int>>>() {})
}
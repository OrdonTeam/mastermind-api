package mastermind

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.*
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.SingleEmitter
import org.junit.Test
import java.io.FileInputStream

class RxFirebaseTest {

    @Test
    fun name() {
        println(RxFirebase().getValue<ColorSet> { getReference("1").child("game") }.blockingGet())
    }
}

data class ColorSet(val first: Int, val second: Int, val third: Int, val fourth: Int)

class RxFirebase {
    private val options = FirebaseOptions.Builder()
            .setServiceAccount(FileInputStream("service-account-key.json"))
            .setDatabaseUrl("https://mastermind-a83ea.firebaseio.com")
            .build()
    private val app = FirebaseApp.initializeApp(options)
    val ref: FirebaseDatabase = FirebaseDatabase.getInstance(app)

    inline fun <reified T : Any> getValue(crossinline location: FirebaseDatabase.() -> DatabaseReference): Single<T> {
        return getValue(ref.location())
    }
}

inline fun <reified T : Any> getValue(databaseReference: DatabaseReference, gson: Gson = Gson()): Single<T> {
    return Single.create({ e ->
        val valueEventListener = createRxValueEventListener(e, gson)
        databaseReference.addListenerForSingleValueEvent(valueEventListener)
        e.setCancellable { databaseReference.removeEventListener(valueEventListener) }
    })
}

inline fun <reified T : Any> createRxValueEventListener(emitter: SingleEmitter<T>, gson: Gson): ValueEventListener {
    return object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) = emitter.onError(error.toException())

        override fun onDataChange(snapshot: DataSnapshot) {
            emitter.onSuccess(gson.fromJson(gson.toJson(snapshot.value), T::class.java))
        }
    }
}

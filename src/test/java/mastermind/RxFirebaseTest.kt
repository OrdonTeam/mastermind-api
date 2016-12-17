package mastermind

import com.google.common.reflect.TypeToken
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.Single
import org.junit.Test
import java.io.FileInputStream
import java.util.*

class RxFirebaseTest {

    @Test
    fun get() {
        println(getValue<HashMap<String, Move>>(RxFirebase().ref.getReference("2").child("moves"), object : TypeToken<HashMap<String, Move>>() {}.type).map { it.map { it.value } }.blockingGet())
    }

    @Test
    fun put() {
        println(RxFirebase().putValue(Move(listOf(1, 2, 3, 4), MatchResult(4, 0))) { getReference("2").child("moves").child(System.currentTimeMillis().toString()) }.blockingGet())
    }
}

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

    inline fun <reified T : Any> putValue(value: T, crossinline location: FirebaseDatabase.() -> DatabaseReference): Single<Unit> {
        return putValue(value, ref.location())
    }
}

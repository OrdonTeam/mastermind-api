package mastermind

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.Single
import org.junit.Test
import java.io.FileInputStream

class RxFirebaseTest {

    @Test
    fun get() {
        println(RxFirebase().getValue<ColorSet> { getReference("1").child("game") }.blockingGet())
    }

    @Test
    fun put() {
        println(RxFirebase().putValue(ColorSet(1, 2, 3, 4)) { getReference("2").child("game") }.blockingGet())
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

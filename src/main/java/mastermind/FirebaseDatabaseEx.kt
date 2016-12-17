package mastermind

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Single
import io.reactivex.SingleEmitter
import java.lang.reflect.Type


fun <T> putValue(value: T, databaseReference: DatabaseReference): Single<Unit> {
    return Single.create({ e ->
        val completionListener = createDatabaseReferenceCompletionListener(e)
        databaseReference.setValue(value, completionListener)
    })
}

fun createDatabaseReferenceCompletionListener(emitter: SingleEmitter<Unit>): DatabaseReference.CompletionListener {
    return DatabaseReference.CompletionListener { error, p1 ->
        if (!emitter.isDisposed) {
            if (error != null) {
                emitter.onError(error.toException())
            } else {
                emitter.onSuccess(Unit)
            }
        }
    }
}

inline fun <reified T : Any> DatabaseReference.getValue(gson: Gson = Gson()): Single<T> {
    return Single.create({ e ->
        val valueEventListener = createRxValueEventListener(e, gson)
        addListenerForSingleValueEvent(valueEventListener)
        e.setCancellable { removeEventListener(valueEventListener) }
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

inline fun <reified T : Any> DatabaseReference.getValue(type: TypeToken<T>, gson: Gson = Gson()): Single<T> {
    return Single.create({ e ->
        val valueEventListener = createRxValueEventListener(e, type.type, gson)
        addListenerForSingleValueEvent(valueEventListener)
        e.setCancellable { removeEventListener(valueEventListener) }
    })
}

inline fun <reified T : Any> createRxValueEventListener(emitter: SingleEmitter<T>, type: Type, gson: Gson): ValueEventListener {
    return object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) = emitter.onError(error.toException())

        override fun onDataChange(snapshot: DataSnapshot) {
            emitter.onSuccess(gson.fromJson(gson.toJson(snapshot.value), type))
        }
    }
}

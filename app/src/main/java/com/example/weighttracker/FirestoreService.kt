package com.example.weighttracker

import android.util.Log
import com.example.weighttracker.models.Progress
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.example.weighttracker.models.User
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.lang.Exception

const val USERS_COLLECTION_NAME = "users"
const val PROGRESS_COLLECTION_NAME = "progress"

class FirestoreService {
    private val db = FirebaseFirestore.getInstance()

    companion object{
        private const val TAG = "DocSnippets"
    }
    private val firestoreSettings = FirebaseFirestoreSettings.Builder()
        .setPersistenceEnabled(true)
        .build();

    init{
        db.firestoreSettings = firestoreSettings
    }

    fun createUser(id:String, user: User){
        db.collection(USERS_COLLECTION_NAME).document(id).set(user)
            .addOnSuccessListener {
                Log.d(TAG, "Document successfully added!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun getUserById(id: String, response: IResult<User>){
        db.collection(USERS_COLLECTION_NAME).document(id).get()
            .addOnSuccessListener {document ->
                run{
                    val user = document.toObject<User>()
                    response.onSuccess(user)
                }
            }
            .addOnFailureListener{ex ->
                run{
                    response.onError(ex)
                }
            }
    }

    fun updateUser(id: String, user: User, response: IResult<User>){
        db.collection(USERS_COLLECTION_NAME).document(id).update(
            mapOf(
                "name" to user.name,
                "lastName" to user.lastName,
                "email" to user.email,
                "startWeight" to user.startWeight,
                "goalWeight" to user.goalWeight
            )
        )
            .addOnSuccessListener {
                run {
                    getUserById(id, response)
                }
            }
            .addOnFailureListener{
                run{
                    response.onError(it)
                }
            }
    }

    fun saveProgress(progress:Progress){
        db.collection(PROGRESS_COLLECTION_NAME).add(progress)
            .addOnSuccessListener {
                Log.d(TAG, "Document successfully added!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun getProgresses(id: String, response: IResult<List<Progress>>){
        db.collection(PROGRESS_COLLECTION_NAME).whereEqualTo("userId", id).orderBy("date", Query.Direction.DESCENDING).get()
            .addOnSuccessListener {result ->
                run {
                    val progresses = mutableListOf<Progress>()
                    for(doc in result.documents){
                        val progress = doc.toObject<Progress>()
                        if(progress != null){
                            progresses.add(progress)
                        }
                    }
                    response.onSuccess(progresses)
                }
            }
            .addOnFailureListener{
                run{
                    response.onError(it)
                }
            }
    }

}

interface IResult<T>{
    fun onSuccess(items: T?)
    fun onError(exception: Exception)
}
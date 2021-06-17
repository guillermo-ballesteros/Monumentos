package com.example.monuments.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import com.example.monuments.database.*
import com.example.monuments.domain.*
import com.example.monuments.network.*
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import com.example.monuments.utils.Constants

@Singleton
class MainRepository @Inject constructor(
    private val api: InterfaceAPI,
    private val database: MonumentsDao,
    val auth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    private val mStorage: StorageReference
) {

    private val monumentsLiveData = MutableLiveData<List<MonumentBO>>()

    val monuments: LiveData<List<MonumentBO>>
        get() = monumentsLiveData

    private var firstTime = true

    private val maxTime = TimeUnit.MINUTES.toMillis(5)

    private var time = System.currentTimeMillis()

    suspend fun refreshComments() {
        val comments: CommentApiDTO = api.listComments()
        database.insertComments(commentsDTDToDao(comments.comments))
    }

    suspend fun requestData() {
        val currentTime = System.currentTimeMillis() - time
        if (currentTime > maxTime || firstTime) {
            if (firstTime) {
                firstTime = false
            }
            time = System.currentTimeMillis()
            deleteData()
            monumentsLiveData.postValue(getDataAPI())
        } else {
            monumentsLiveData.postValue(getMonumentsRoom())
        }
    }

    private suspend fun getDataAPI(): List<MonumentBO> {
        val list: MonumentApiDTO = api.listMonuments()
        val listSorted = sortMonuments(list.monuments)
        val favorites: FavoriteApiDTO = api.listFavorites()
        refreshComments()

        database.insertAllMonuments(toDao(listSorted, favorites.favorites, auth))
        database.insertAllImages(imagesDTOToDao(list.monuments))

        return toBO(listSorted, favorites.favorites, auth)
    }

    private fun deleteData() {
        database.deleteAllMonuments()
        database.deleteAllImages()
        database.deleteAllComments()
    }

    private fun getMonumentsRoom(): List<MonumentBO> {
        val list: List<MonumentWithImagesDao> = database.getMonumentsWithImages()
        return list.toBO()
    }

    suspend fun login(email: String, password: String): Int {
        var result = 0
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signOut()
            try {
                auth.signInWithEmailAndPassword(email, password).await()
            } catch (e: FirebaseAuthInvalidUserException) {
                result = Constants.INVALID_USER_CODE
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                result = Constants.INVALID_CREDENTIALS_CODE
            } catch (e: FirebaseTooManyRequestsException) {
                result = Constants.TOO_MANY_REQUEST_CODE
            }
            val user: FirebaseUser? = auth.currentUser
            if (user != null) {
                result = if (user.isEmailVerified) {
                    Constants.SUCCESS_CODE

                } else {
                    Constants.EMAIL_NOT_VERIFIED_CODE
                }
            }

        } else {
            result = Constants.NULL_DATA_CODE
        }
        return result
    }

    fun getMyMonuments(): LiveData<List<MonumentBO>>? {
        return auth.currentUser?.email?.let {
            database.getMyMonuments(it).asLiveData().map { monuments ->
                monuments.toBO()
            }
        }
    }

    fun getMonument(id: String): MonumentBO {
        return database.getMonument(id).toBO()
    }

    fun getFavoritesMonuments(): LiveData<List<MonumentBO>> {
        return database.getFavorites().asLiveData().map {
            it.toBO()
        }
    }

    suspend fun createNewAccount(
        name: String,
        lastName: String,
        email: String,
        password1: String,
        password2: String
    ): Int {
        var result = Constants.SUCCESS_CODE
        if (name.isNotEmpty() && lastName.isNotEmpty()
            && email.isNotEmpty()
            && password1.isNotEmpty()
            && password2.isNotEmpty()
        ) {
            if (password1 == password2) {
                try {
                    auth.createUserWithEmailAndPassword(email, password1).await()
                    val user: FirebaseUser? = auth.currentUser
                    verifyEmail(user)
                    firebaseFirestore.collection(Constants.USER_DATABASE).document().set(
                        hashMapOf(
                            Constants.DATABASE_NAME to name,
                            Constants.USER_DATABASE_LASTNAME to lastName
                        )
                    ).await()

                } catch (e: FirebaseAuthUserCollisionException) {
                    result = Constants.USER_COLLISION_CODE
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    result = Constants.INVALID_CREDENTIALS_CODE
                }

            } else {
                result = Constants.PASSWORD_NOT_MATCH_CODE
            }

        } else {
            result = Constants.NULL_DATA_CODE
        }
        return result
    }

    private suspend fun verifyEmail(user: FirebaseUser?) {
        user?.sendEmailVerification()?.await()
    }

    suspend fun restorePassword(email: String): Int {
        return if (email.isNotEmpty()) {
            try {
                auth.sendPasswordResetEmail(email).await()
                Constants.SUCCESS_CODE
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                Constants.INVALID_CREDENTIALS_CODE
            }

        } else {
            Constants.NULL_DATA_CODE
        }
    }

    suspend fun addData(
        name: String,
        city: String,
        urlExtra: String?,
        description: String,
        location: LocationBO,
        images: List<ImageBO>
    ) {
        if (urlExtra != "") {
            firebaseFirestore.collection(Constants.MONUMENTS_DATABASE).document().set(
                hashMapOf(
                    Constants.DATABASE_NAME to name,
                    Constants.MONUMENT_DATABASE_CITY to city,
                    Constants.MONUMENT_DATABASE_DESCRIPTION to description,
                    Constants.MONUMENT_DATABASE_LOCATION to hashMapOf(
                        Constants.MONUMENTS_DATABASE_LOCATION_LATITUDE to location.latitude,
                        Constants.MONUMENTS_DATABASE_LOCATION_LONGITUDE to location.longitude
                    ),
                    Constants.MONUMENTS_DATABASE_IMAGES to images,
                    Constants.DATABASE_USER to auth.currentUser?.email,
                    Constants.MONUMENT_DATABASE_URL_EXTRA to urlExtra
                ),
            ).await()

        } else {
            firebaseFirestore.collection(Constants.MONUMENTS_DATABASE).document().set(
                hashMapOf(
                    Constants.DATABASE_NAME to name,
                    Constants.MONUMENT_DATABASE_CITY to city,
                    Constants.MONUMENT_DATABASE_DESCRIPTION to description,
                    Constants.MONUMENT_DATABASE_LOCATION to hashMapOf(
                        Constants.MONUMENTS_DATABASE_LOCATION_LATITUDE to location.latitude,
                        Constants.MONUMENTS_DATABASE_LOCATION_LONGITUDE to location.longitude
                    ),
                    Constants.MONUMENTS_DATABASE_IMAGES to images,
                    Constants.DATABASE_USER to auth.currentUser?.email,
                )
            ).await()
        }
        monumentsLiveData.postValue(getDataAPI())
    }

    suspend fun addImages(images: List<Uri?>): List<ImageBO> {
        val finalListImages = mutableListOf<ImageBO>()
        for (image in images) {
            val filePath: StorageReference =
                mStorage.child(Constants.MONUMENTS_DATABASE_IMAGES).child(Date().toString())

            if (image != null) {
                filePath.putFile(image).await()
            }

            val id = filePath.name
            val url = filePath.downloadUrl.await()
            finalListImages.add(ImageBO(id, url.toString()))

        }
        return finalListImages
    }

    suspend fun changeFavorite(monument: MonumentBO) {
        if (monument.favorite.id == Constants.UNKNOWN_VALUE) {
            firebaseFirestore.collection(Constants.FAVORITES_DATABASE).document().set(
                hashMapOf(
                    Constants.DATABASE_MONUMENT_ID to monument.id,
                    Constants.DATABASE_USER to auth.currentUser?.email
                )
            ).await()

        } else {
            firebaseFirestore.collection(Constants.FAVORITES_DATABASE).document(monument.favorite.id)
                .delete().await()

        }
        monumentsLiveData.postValue(getDataAPI())
    }

    fun getComments(id: String): LiveData<List<CommentBO>> {
        return database.getComments(id).asLiveData().map {
            it.commentsToBO()
        }
    }

    private fun sortMonuments(monuments: List<MonumentDTO>?): List<MonumentDTO> {
        if (monuments != null) {
            return monuments.sortedByDescending {
                it.createTime
            }
        }
        return emptyList()
    }

    suspend fun addComment(rate: Int, comment: String, monumentId: String) {
        firebaseFirestore.collection(Constants.COMMENTS_DATABASE).document().set(
            hashMapOf(
                Constants.DATABASE_MONUMENT_ID to monumentId,
                Constants.COMMENTS_DATABASE_COMMENT to comment,
                Constants.COMMENTS_DATABASE_RATE to rate,
                Constants.DATABASE_USER to auth.currentUser?.email
            )
        ).await()
        getDataAPI()
    }

    suspend fun removeMonument(position: Int): Int {
        val monument = monuments.value?.get(position)
        return if (monument?.user == auth.currentUser?.email) {
            monument?.id?.let { id ->
                firebaseFirestore.collection(Constants.MONUMENTS_DATABASE).document(id).delete().await()
                database.deleteMonument(id)
            }

            monumentsLiveData.postValue(getMonumentsRoom())
            Constants.SUCCESS_CODE

        } else {
            Constants.NOT_SAME_USER_CODE
        }
    }

    fun logOut() {
        firstTime = true
        auth.signOut()
    }

}
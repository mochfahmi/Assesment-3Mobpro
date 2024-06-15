package org.d3if3134.mobpro2.ui.screen

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.d3if3134.asessment3.model.banMobil
import org.d3if3134.asessment3.network.BanMobilApi
import java.io.ByteArrayOutputStream

class MainViewModel : ViewModel() {
    var data = mutableStateOf(emptyList<banMobil>())
        private set

    var status = MutableStateFlow(BanMobilApi.ApiStatus.LOADING)
        private  set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    fun retrieveData(userId: String) {
        viewModelScope.launch (Dispatchers.IO){
            status.value = BanMobilApi.ApiStatus.LOADING
            try {
                data.value = BanMobilApi.service.getBanMobil(userId)
                status.value = BanMobilApi.ApiStatus.SUCCESS
            }catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                status.value = BanMobilApi.ApiStatus.FAILED
            }
        }
    }
    fun saveData(userId: String, merkBan: String, jenisBan: String, ukuranBan : String, bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = BanMobilApi.service.postBanMobil(
                    userId,
                    merkBan.toRequestBody("text/plain".toMediaTypeOrNull()),
                    jenisBan.toRequestBody("text/plain".toMediaTypeOrNull()),
                    ukuranBan.toRequestBody("text/plain".toMediaTypeOrNull()),
                    bitmap.toMultipartBody()
                )
                if (result.status == "success")
                    retrieveData(userId)
                else
                    throw Exception(result.message)
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun deleteData(userId: String, id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = BanMobilApi.service.deleteBanMobil(userId, id)
                if (result.status == "success")
                    retrieveData(userId)
                else
                    throw Exception(result.message)
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }
private fun Bitmap.toMultipartBody(): MultipartBody.Part {
    val stream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, 80, stream)
    val byteArray = stream.toByteArray()
    val requestBody = byteArray.toRequestBody(
        "image/jpg".toMediaTypeOrNull(), 0, byteArray.size)
    return MultipartBody.Part.createFormData(
        "image", "image.jpg", requestBody)
}
fun clearMessage() { errorMessage.value = null }
}


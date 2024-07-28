package com.rleonb.reported.ui.screens.home

import android.graphics.Bitmap
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rleonb.reported.api.ApiClient
import com.rleonb.reported.domain.models.CreateNewsResponse
import com.rleonb.reported.domain.models.CreatingNews
import com.rleonb.reported.domain.models.News
import com.rleonb.reported.domain.models.NewsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
//    private val context: Context,
//    private val fusedLocationClient: FusedLocationProviderClient
) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState
    private var allNews: List<News> = emptyList()

    init {
        fetchNews()
    }

    fun searchNews(query: String) {
        val filteredNews = allNews.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.address.contains(query, ignoreCase = true)
        }
        _uiState.value = HomeUiState.Success(filteredNews)
    }

//    private fun getLastKnownLocation(callback: (String) -> Unit) {
//        if (ActivityCompat.checkSelfPermission(
//                context,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                context,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return
//        }
//        fusedLocationClient.lastLocation
//            .addOnSuccessListener { location ->
//                if (location != null) {
//                    val address = "Lat: ${location.latitude}, Lon: ${location.longitude}"
//                    callback(address)
//                } else {
//                    callback("Unknown Location")
//                }
//            }
//    }

    fun createNews(bitmap: Bitmap) {
//        getLastKnownLocation { address ->
        val encodedImage = encodeBitmap(bitmap)
        val news = CreatingNews(
            title = "Title",
            description = "Description",
            address = "Address",
            image = encodedImage
        )
        postNews(news)
//        }
    }

    private fun fetchNews() {
        viewModelScope.launch {
            try {
                val call = ApiClient.apiService.getNews()
                call.enqueue(object : Callback<NewsResponse> {
                    override fun onResponse(
                        call: Call<NewsResponse>,
                        response: Response<NewsResponse>
                    ) {
                        if (response.isSuccessful) {
                            val newsItems = response.body()!!.data!!.news
                            allNews = newsItems!!.map { it!!.toOverview() }
                            _uiState.value = HomeUiState.Success(allNews)
                        } else {
                            _uiState.value = HomeUiState.Error(Exception(response.message()))
                        }
                    }

                    override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                        _uiState.value = HomeUiState.Error(t)
                    }
                })
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e)
            }
        }
    }

    private fun postNews(news: CreatingNews) {
        viewModelScope.launch {
            try {
                val call = ApiClient.apiService.postNews(news)
                call.enqueue(object : Callback<CreateNewsResponse> {
                    override fun onResponse(
                        call: Call<CreateNewsResponse>,
                        response: Response<CreateNewsResponse>
                    ) {
                        if (response.isSuccessful) {
                            fetchNews()
                        } else {
                            _uiState.value = HomeUiState.Error(Exception(response.message()))
                        }
                    }

                    override fun onFailure(call: Call<CreateNewsResponse>, t: Throwable) {
                        _uiState.value = HomeUiState.Error(t)
                    }
                })
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e)
            }
        }
    }

    private fun encodeBitmap(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}


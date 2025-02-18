package com.example.drawrun.ui.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.drawrun.R
import com.example.drawrun.data.api.ImageUploadApi
import com.example.drawrun.data.dto.request.course.AiCourseRequest
import com.example.drawrun.data.model.ParcelablePoint
import com.example.drawrun.utils.RetrofitInstance
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class AiMapActivity : AppCompatActivity() {

    private lateinit var canvasView: CanvasView
    private lateinit var btnCreateCourse: Button

    private lateinit var tvStartLocation: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_map)

        canvasView = findViewById(R.id.canvasView)
        btnCreateCourse = findViewById(R.id.btnCreateCourse)
        tvStartLocation = findViewById(R.id.tvStartLocation)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocation() // ✅ GPS 현재 위치 가져오기 실행

//        // "만들기" 버튼 클릭 시 이미지 업로드 실행
//        btnCreateCourse.setOnClickListener {
//            val bitmap = canvasView.getBitmap()
//
//            CoroutineScope(Dispatchers.Main).launch {
//                val imageUrl = uploadImage(bitmap)
//                if (imageUrl != null) {
//                    Log.d("AiMapActivity", "✅ 이미지 업로드 성공: $imageUrl")
//                    // 이후 경로 생성 로직 추가 (예: AI 추천 경로 받기)
//                    if (currentLatitude != null && currentLongitude != null) {
//                        requestAiRecommendedCourse(imageUrl, currentLatitude!!, currentLongitude!!)
//                    } else {
//                        Log.e("AiMapActivity", "❌ 현재 위치 정보가 없음")
//                    }
//                } else {
//                    Log.e("AiMapActivity", "❌ 이미지 업로드 실패")
//                }
//            }
//        }
//
        btnCreateCourse.setOnClickListener {
            mockAiPathAndNavigate() // ✅ 버튼 클릭 시 더미 데이터로 이동
        }
    }

    private suspend fun uploadImage(bitmap: Bitmap): String? {
        return withContext(Dispatchers.IO) {
            val file = File(cacheDir, "temp_image.jpg")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            try {
                val imageUploadApi = RetrofitInstance.ImageUploadApi(this@AiMapActivity)
                val response = imageUploadApi.uploadImage(body)
                if (response.isSuccess) {
                    response.data?.url
                } else {
                    null
                }
            } catch (e: Exception) {
                Log.e("AiMapActivity", "❌ 이미지 업로드 중 오류 발생", e)
                null
            } finally {
                file.delete()
            }
        }
    }

    // ✅ 현재 GPS 좌표 가져오기
    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                currentLatitude = location.latitude
                currentLongitude = location.longitude
//                tvStartLocation.text = "위도: $currentLatitude\n경도: $currentLongitude"
                Log.d("AiMapActivity", "현재 위치: 위도 $currentLatitude, 경도 $currentLongitude")
            } else {
                tvStartLocation.text = "위치를 가져올 수 없습니다"
                Log.e("AiMapActivity", "현재 위치를 가져오지 못함")
            }
        }
    }


    // ✅ AI 추천 경로 요청
    private fun requestAiRecommendedCourse(imageUrl: String, latitude: Double, longitude: Double) {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                try {
                    val aiCourseApi = RetrofitInstance.AiCourseApi(this@AiMapActivity)
                    val requestBody = AiCourseRequest(lat = latitude, lon = longitude, paintUrl = imageUrl)

                    val response = aiCourseApi.requestAiCourse(requestBody)
                    if (response.isSuccessful && response.body()?.isSuccess == true) {
                        val aiPath = response.body()?.data?.path
                        Log.d("AiMapActivity", "✅ AI 추천 경로 받음: $aiPath")

                        if (!aiPath.isNullOrEmpty()){
                            Log.d("AiMapactivity", "✅ AI 추천 경로 받음: $aiPath")
                            // ✅ AI 추천 경로를 `ParcelablePoint` 리스트로 변환
                            val parcelablePoints = aiPath.map { ParcelablePoint(Point.fromLngLat(it.longitude, it.latitude)) }

                            // ✅ `MapActivity`로 이동하면서 `aiPath` 데이터 전달
                            val intent = Intent(this@AiMapActivity, MapActivity::class.java).apply {
                                putParcelableArrayListExtra("aiPath", ArrayList(parcelablePoints)) // 리스트 전달
                            }
                            startActivity(intent)
                        }
                    } else {
                        Log.e("AiMapActivity", "❌ AI 추천 경로 실패: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    Log.e("AiMapActivity", "❌ AI 추천 경로 요청 오류 발생", e)
                }
            }
        }
    }

    private fun mockAiPathAndNavigate() {
        // ✅ AI 경로 더미 데이터 (API 없이 테스트용)
        val mockAiPath = listOf(
            ParcelablePoint(Point.fromLngLat(128.854045, 35.09338)),
            ParcelablePoint(Point.fromLngLat(128.89472, 35.1220298)),
            ParcelablePoint(Point.fromLngLat(128.89472, 35.1220298)),
            ParcelablePoint(Point.fromLngLat(128.89472, 35.1220298)),
            ParcelablePoint(Point.fromLngLat(128.89472, 35.1220298)),
            ParcelablePoint(Point.fromLngLat(128.89472, 35.1220298))
        )


        // ✅ `MapActivity`로 이동하면서 `mockAiPath`를 전달
        val intent = Intent(this, MapActivity::class.java).apply {
            putParcelableArrayListExtra("aiPath", ArrayList(mockAiPath)) // 리스트 전달
        }
        startActivity(intent) // `MapActivity` 실행
    }

}

package com.example.drawrun

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.LinearGradient
import android.graphics.Shader
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.drawrun.ui.auth.LoginActivity
import com.example.drawrun.utils.SecureStorage
import org.json.JSONObject
import android.util.Base64
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import com.example.drawrun.ui.common.BaseActivity
import com.example.drawrun.ui.map.MapActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale
import android.Manifest
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowInsetsController
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.drawrun.data.dto.response.search.CourseData
import com.example.drawrun.data.repository.SearchRepository
import com.example.drawrun.dto.course.PathPoint
import com.example.drawrun.ui.main.fragment.CoursePagerAdapter
import com.example.drawrun.ui.map.AiMapActivity
import com.example.drawrun.ui.navi.NaviActivity
import com.example.drawrun.utils.RetrofitInstance
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {

    private lateinit var btnLoginLogout: Button
    private lateinit var btnAICourse: ImageView
    private lateinit var btnCustomCourse: ImageView
    private lateinit var tvLocation: TextView
    private lateinit var tvRunNear: TextView
    override fun getLayoutId(): Int = R.layout.activity_main  // ✅ 레이아웃 리소스 지정

    // 위치 ..
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var searchRepository: SearchRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        LaunchAppMessageReceiver(this)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            window.statusBarColor = Color.TRANSPARENT // 상태바 투명하게 만들기
            insets
        }
        // 상태바 배경을 투명하게 설정
        window.statusBarColor = Color.TRANSPARENT
        // 상태바 아이콘을 흰색으로 변경 (Android 11 이상)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                0, // 흰색 글씨 유지
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        }

        val tvWelcomeMessage = findViewById<TextView>(R.id.tvWelcomeMessage)
        val customFont = ResourcesCompat.getFont(this, R.font.praise_regular)
        tvWelcomeMessage.typeface = customFont

        tvRunNear = findViewById(R.id.tvRunNear)
        applyGradientToText(tvRunNear)

        tvLocation = findViewById(R.id.tvLocation)

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        btnLoginLogout = findViewById(R.id.btnLogin)

        // ✅ 초기 로그인 상태 확인
        updateLoginState()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkAndRequestLocationPermission()

        // ✅ UI 요소 초기화
        tvLocation = findViewById(R.id.tvLocation)
        btnLoginLogout = findViewById(R.id.btnLogin)
        btnAICourse = findViewById(R.id.btnAICourse) // 🔹 ImageView로 수정
        btnCustomCourse = findViewById(R.id.btnCustomCourse) // 🔹 ImageView로 수정

        // ✅ `RetrofitInstance`를 통해 `SearchApi` 인스턴스 생성 (context 사용)
        val searchApi = RetrofitInstance.SearchApi(this)

        // ✅ `SearchRepository` 인스턴스 생성
        searchRepository = SearchRepository(searchApi)


        // ✅ 로그인/로그아웃 버튼 클릭 이벤트 처리
        btnLoginLogout.setOnClickListener {
            if (SecureStorage.getAccessToken(this) != null) {
                // 로그아웃 처리
                SecureStorage.clearAccessToken(this)
                Log.d("MainActivity", "로그아웃 완료")
            } else {
                // 로그인 화면으로 이동
                try {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error starting LoginActivity", e)
                }
            }
            // ✅ 상태 업데이트
            updateLoginState()
        }


        // ✅ AI 코스 버튼 클릭 이벤트
        btnAICourse.setOnClickListener {
            val accessToken = SecureStorage.getAccessToken(this)
            if (accessToken != null) {
                val intent = Intent(this, AiMapActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        // ✅ 나만의 코스 버튼 클릭 이벤트
        btnCustomCourse.setOnClickListener {
            val accessToken = SecureStorage.getAccessToken(this)
            if (accessToken != null) {
                val intent = Intent(this, MapActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }



        // 인텐트에 포함된 메시지 경로 확인 (추가 검증)
        intent?.extras?.let {
            Log.d("DrawRun", "MainActivity 인텐트 데이터: ${it.toString()}")
        }

    }

    override fun onResume() {
        super.onResume()
        updateLoginState()  // ✅ 액티비티가 다시 보일 때 로그인 상태 업데이트
        getCurrentLocation()
    }

    // ✅ 로그인 상태 업데이트 함수
    private fun updateLoginState() {
        val accessToken = SecureStorage.getAccessToken(this)
        val isLoggedIn = accessToken != null

        if (isLoggedIn) {
            btnLoginLogout.text = "로그아웃"
            val username = parseUsernameFromToken(accessToken)
        } else {
            btnLoginLogout.text = "로그인"
        }
    }

    // ✅ JWT 토큰에서 username 추출
    private fun parseUsernameFromToken(token: String?): String {
        return try {
            val parts = token?.split(".")
            if (parts != null && parts.size >= 2) {
                val payload = String(Base64.decode(parts[1], Base64.URL_SAFE)) // ✅ URL_SAFE로 수정
                val jsonObject = JSONObject(payload)
                jsonObject.optString("username", "사용자") // ✅ username 파싱
            } else {
                "사용자"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "사용자"
        }
    }

    private fun applyGradientToText(textView: TextView) {
        val paint = textView.paint
        val width = paint.measureText(textView.text.toString())

        val shader = LinearGradient(
            0f, 0f, width, textView.textSize,
            intArrayOf(0xFF66FF99.toInt(), 0xFF228B22.toInt()), // 연두색 → 녹색
            null,
            Shader.TileMode.CLAMP
        )
        textView.paint.shader = shader
    }

    private fun checkAndRequestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation()
        }
    }

    // ✅ API 호출 (Repository 통해 요청)
    private fun loadCoursesByLocation(area: String) {
        Log.d("MainActivity", "🚀 API 요청할 지역: $area")

        lifecycleScope.launch {
            val result = searchRepository.searchByLocation(area)
            result.onSuccess { searchResponse ->
                val courseList = searchResponse.take(5)
                Log.d("MainActivity", "🎯 받아온 코스 데이터: $courseList")

                // ✅ 받아온 데이터를 `setupViewPager`에 넘겨줌
                setupViewPager(courseList)

            }.onFailure { error ->
                Log.e("MainActivity", "❌ API 요청 실패: ${error.message}")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val geocoder = Geocoder(this, Locale.KOREAN)
                val address = geocoder.getFromLocation(it.latitude, it.longitude, 1)?.firstOrNull()
                val areaName = address?.subLocality ?: "강서구"
                tvLocation.text = areaName

                // ✅ 위치 기반 API 요청 실행
                loadCoursesByLocation(areaName)
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    private fun setupViewPager(courses: List<CourseData>) {
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)

        // ✅ `FragmentActivity`(`this`)와 `courses`만 넘겨주면 됨
        val adapter = CoursePagerAdapter(this, courses.take(5))
        viewPager.adapter = adapter

        // ✅ 이미지 클릭 시 상세 데이터 요청
        adapter.setOnItemClickListener { course ->
            fetchCourseDetails(course.courseId)
        }
    }

    private fun fetchCourseDetails(courseId: Int) {
        Log.d("MainActivity", "Fetching details for courseId: $courseId")

        // ✅ Retrofit 인스턴스 생성 (API 호출)
        val courseApi = RetrofitInstance.CourseApi(this)

        lifecycleScope.launch {
            try {
                val response = courseApi.getCourseDetails(courseId) // ✅ API 호출
                if (response.isSuccessful) {
                    val details = response.body()
                    if (details != null) {
                        Log.d("MainActivity", "✅ Loaded Course Details: $details")

                        val pathPoints = details.path.map { PathPoint(it.latitude, it.longitude) }

                        // ✅ `NaviActivity`로 이동 (데이터 전달)
                        val intent = Intent(this@MainActivity, NaviActivity::class.java).apply {
                            putParcelableArrayListExtra("path", ArrayList(pathPoints))
                            putExtra("startLocation", details.location)
                            putExtra("distance", details.distance)
                        }
                        startActivity(intent)
                        overridePendingTransition(0, 0) // ✅ 애니메이션 제거
                    } else {
                        Log.e("MainActivity", "❌ Course details are null")
                    }
                } else {
                    Log.e("MainActivity", "❌ API 요청 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "❌ Error loading course details", e)
            }
        }
    }



}



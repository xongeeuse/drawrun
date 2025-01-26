package com.example.drawrun.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


// API 통신용 데이터 클래스
data class SignUpRequest(
    val userId: String,
    val email: String,
    val password: String,
    val userName: String,
    val nickname: String,
)

// 로컬 저장용 Entity 클래스
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val email: String,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "login_status")
    val isLoggedIn: Boolean = false
)


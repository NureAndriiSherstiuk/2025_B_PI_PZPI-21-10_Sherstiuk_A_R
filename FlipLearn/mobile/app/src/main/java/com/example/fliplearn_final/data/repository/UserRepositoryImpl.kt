package com.example.fliplearn_final.data.repository

import com.example.fliplearn_final.data.local.dao.UserDao
import com.example.fliplearn_final.data.local.entity.UserEntity
import com.example.fliplearn_final.domain.model.User
import com.example.fliplearn_final.domain.repository.user.UserRepository

class UserRepositoryImpl(
private val userDao: UserDao
) : UserRepository {

    override suspend fun signUpUser(user: User) {
        val userEntity = UserEntity(
            user_id = user.id,
            email = user.email,
            username = user.username,
            password = user.password,
            createdAt = user.createdAt
        )
        userDao.insertUser(userEntity)
    }

    override suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)?.let { entity ->
            User(
                id = entity.user_id,
                email = entity.email,
                username = entity.username,
                password = entity.password,
                createdAt = entity.createdAt
            )
        }
    }

    override suspend fun updateUserProfile(userId: Int, email: String, username: String) {
        userDao.updateUserProfile(userId, email, username)
    }


}
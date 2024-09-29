package com.example.assignment2.Data

class UserRepository(private val db: UserDatabase) {


    suspend fun insertUser(user: User){
        db.dao.insertUser(user)
    }


    suspend fun updateUser(user: User){
        db.dao.updateUser(user)
    }


    suspend fun deleteUser(user: User){
        db.dao.deleteUser(user)
    }


    fun getUserById(userId: String) = db.dao.getUserById(userId)


    fun getAllUsers() = db.dao.getAllUsers()
}
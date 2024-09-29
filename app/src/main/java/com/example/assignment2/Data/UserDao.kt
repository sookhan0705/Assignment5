package com.example.assignment2.Data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    // Fetch a user by their userId
    @Query("SELECT * FROM userTable WHERE userId = :userId")
    fun getUserById(userId: String): Flow<User>

    // Get all users (optional, for testing purposes)
    @Query("SELECT * FROM userTable")
    fun getAllUsers(): Flow<List<User>>
}


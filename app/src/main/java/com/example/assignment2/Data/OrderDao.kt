package com.example.assignment2.Data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrders(orders: Order)


    @Query("SELECT * FROM orders WHERE CustomerIdFk = :userId")
    fun getOrdersByUserId(userId: String): Flow<List<Order>>


    @Query("SELECT * FROM orders")
    suspend fun getAllOrders(): List<Order>


    @Query("DELETE FROM orders")
    suspend fun clearOrders()
}

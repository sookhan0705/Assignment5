package com.example.assignment2.Data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(vararg orders: Order)

    // Or to insert a list of orders:
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<Order>)


    @Query("SELECT * FROM orders WHERE CustomerIdFk = :userId")
    fun getOrdersByUserId(userId: String): Flow<List<Order>>


    @Query("SELECT * FROM orders")
    fun getAllOrders(): Flow<List<Order>>


    @Query("DELETE FROM orders")
    suspend fun clearOrders()
}

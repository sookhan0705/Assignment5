package com.example.assignment2.Data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [CreditCard::class], version = 1)
abstract class CreditCardDatabase :RoomDatabase(){
    abstract val creditCardDao: CreditCardDao


    companion object {
        @Volatile
        private var INSTANCE: CreditCardDatabase? = null


        fun getDatabase(context: Context): CreditCardDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CreditCardDatabase::class.java,
                    "CC.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}
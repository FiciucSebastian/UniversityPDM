package com.example.ficiapp.components.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ficiapp.components.data.Component

@Database(entities = [Component::class], version = 13)
abstract class ComponentDatabase : RoomDatabase() {

    abstract fun componentDao(): ComponentDao

    companion object {
        @Volatile
        private var INSTANCE: ComponentDatabase? = null

        fun getDatabase(context: Context): ComponentDatabase {
            val inst = INSTANCE
            if (inst != null) {
                return inst
            }
            val instance =
                Room.databaseBuilder(
                    context.applicationContext,
                    ComponentDatabase::class.java,
                    "components_db"
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            INSTANCE = instance
            return instance
        }
    }

}
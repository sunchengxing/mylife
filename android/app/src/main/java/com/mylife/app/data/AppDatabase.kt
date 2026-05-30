package com.mylife.app.data

import androidx.room.*
import com.mylife.app.data.converter.DateConverter
import com.mylife.app.data.recipe.CachedRecipe
import com.mylife.app.data.recipe.CachedIngredient
import com.mylife.app.data.recipe.CachedStep
import com.mylife.app.data.recipe.CachedRecipeDao
import com.mylife.app.data.recipe.CachedIngredientDao
import com.mylife.app.data.recipe.CachedStepDao

@Database(
    entities = [Record::class, CachedRecipe::class, CachedIngredient::class, CachedStep::class],
    version = 2,
    exportSchema = false,
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
    abstract fun cachedRecipeDao(): CachedRecipeDao
    abstract fun cachedIngredientDao(): CachedIngredientDao
    abstract fun cachedStepDao(): CachedStepDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mylife",
                ).fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
        }
    }
}

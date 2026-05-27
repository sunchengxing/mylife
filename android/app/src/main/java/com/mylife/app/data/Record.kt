package com.mylife.app.data

import androidx.room.*
import java.util.Date

@Entity(tableName = "records")
data class Record(
    @PrimaryKey val id: String,
    val store: String,       // clothing, food, housing, transport
    val name: String,
    val category: String = "",
    val season: String = "",     // clothing only
    val calories: String = "",  // food only
    val cost: String = "",
    val note: String = "",
    val photo: String = "",     // base64 or file path
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
)

@Dao
interface RecordDao {
    @Query("SELECT * FROM records WHERE store = :store ORDER BY createdAt DESC")
    suspend fun getByStore(store: String): List<Record>

    @Query("SELECT * FROM records WHERE id = :id")
    suspend fun getById(id: String): Record?

    @Query("SELECT COUNT(*) FROM records WHERE store = :store")
    suspend fun countByStore(store: String): Int

    @Query("SELECT * FROM records WHERE updatedAt > :since ORDER BY updatedAt ASC")
    suspend fun getUpdatedSince(since: Date): List<Record>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(record: Record)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(records: List<Record>)

    @Delete
    suspend fun delete(record: Record)

    @Query("DELETE FROM records WHERE id = :id AND store = :store")
    suspend fun deleteById(id: String, store: String)
}

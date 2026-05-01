package com.juanesarangoi.consumoapirest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.juanesarangoi.consumoapirest.data.local.entity.PostRemoteKeys

@Dao
interface PostRemoteKeysDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<PostRemoteKeys>)
    
    @Query("SELECT * FROM post_remote_keys WHERE id = :id")
    suspend fun getRemoteKeysById(id: Int): PostRemoteKeys?
    
    @Query("DELETE FROM post_remote_keys")
    suspend fun clearRemoteKeys()
}

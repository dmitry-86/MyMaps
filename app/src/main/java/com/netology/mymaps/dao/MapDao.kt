package com.netology.mymaps.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.netology.mymaps.entity.MapEntity

@Dao
interface MapDao {
    @Query("SELECT * FROM MapEntity ORDER BY id DESC")
    fun getAll(): LiveData<List<MapEntity>>

    @Insert
    fun insert(location: MapEntity)

    @Query("UPDATE MapEntity SET title = :title WHERE id =:id")
    fun updateContentById(id: Long, title: String)

    fun save(location: MapEntity) =
        if(location.id == 0L) insert(location) else updateContentById(location.id, location.title)

    @Query("DELETE FROM MapEntity WHERE id = :id")
    fun removeById(id: Long)

}
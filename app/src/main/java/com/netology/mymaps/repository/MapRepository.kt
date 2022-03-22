package com.netology.mymapapp.repository

import androidx.lifecycle.LiveData
import com.netology.mymapapp.dto.UserMap

interface MapRepository {
    fun getAll(): LiveData<List<UserMap>>
    fun save(location: UserMap)
    fun saveMarker(location: UserMap)
    fun removeById(id: Long)
}
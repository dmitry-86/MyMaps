package com.netology.mymapapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.Marker
import com.netology.mymapapp.dto.UserMap
import com.netology.mymapapp.repository.MapRepository
import com.netology.mymapapp.repository.MapRepositoryImpl
import com.netology.mymaps.db.AppDb

private val empty = UserMap(
    id = 0,
    title = "",
    latitude = 0.0,
    longitude = 0.0
)

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MapRepository = MapRepositoryImpl(
        AppDb.getInstance(context = application).mapDao()
    )
    val data = repository.getAll()

    val edited = MutableLiveData(empty)

    fun saveMarker(marker: Marker) {
        edited.value?.copy(
            title = marker.title!!,
            latitude = marker.position.latitude,
            longitude = marker.position.longitude
        ).let {
            repository.saveMarker(it!!)
        }
    }

    fun save() {
        edited.value?.let {
            repository.save(it)
        }
        edited.value = empty
    }

    fun edit(location: UserMap) {
        edited.value = location
    }

    fun changeContent(title: String) {
        val text = title.trim()
        if (edited.value?.title == text) {
            return
        }
        edited.value = edited.value?.copy(title = text)
    }

    fun removeById(id: Long) = repository.removeById(id)

}
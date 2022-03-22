package com.netology.mymapapp.repository

import androidx.lifecycle.Transformations
import com.netology.mymapapp.dto.UserMap
import com.netology.mymaps.dao.MapDao
import com.netology.mymaps.entity.MapEntity

class MapRepositoryImpl(
    private val dao: MapDao,
) : MapRepository {

//    private var locations = listOf(
//        UserMap(
//            id = 1,
//            title = "Berlin",
//            latitude = 55.751999,
//            longitude = 37.617734
//        ),
//        UserMap(
//            id = 2,
//            title = "Baikal",
//            latitude = 52.2855,
//            longitude = 104.2890
//        ),
//        UserMap(
//            id = 3,
//            title = "Vladivostok",
//            latitude = 43.1332,
//            longitude = 131.9113
//        ),
//    )


//    private val data = MutableLiveData(locations)

//    override fun getAll(): LiveData<List<UserMap>> = data

    override fun getAll() = Transformations.map(dao.getAll()) { list ->
        list.map {
            it.toDto()
        }
    }

    override fun save(location: UserMap) {
        dao.save(MapEntity.fromDto(location))
    }

    override fun saveMarker(location: UserMap) {
        dao.save(MapEntity.fromDto(location))
    }

    override fun removeById(id: Long) {
        dao.removeById(id)
    }

}




package com.netology.mymaps.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.netology.mymapapp.dto.UserMap

@Entity
data class MapEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val title: String,
    val latitude: Double,
    val longitude: Double
) {
    fun toDto() = UserMap(
        id, title, latitude, longitude
    )

    companion object {
        fun fromDto(dto: UserMap) =
            MapEntity(
                dto.id,
                dto.title,
                dto.latitude,
                dto.longitude,
            )
    }

}
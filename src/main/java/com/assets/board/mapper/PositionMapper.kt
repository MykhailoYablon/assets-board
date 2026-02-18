package com.assets.board.mapper

import com.assets.board.entity.Position
import com.assets.board.model.ib.IBPosition
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface PositionMapper {
    // DTO to Entity
    fun toEntity(dto: IBPosition?): Position?

    // Entity to DTO
    fun toDto(entity: Position?): IBPosition?

    // List of DTOs to List of Entities
    fun toEntityList(dtoList: MutableList<IBPosition?>?): MutableList<Position?>?

    // List of Entities to List of DTOs
    fun toDtoList(entityList: MutableList<Position?>?): MutableList<IBPosition?>?
}

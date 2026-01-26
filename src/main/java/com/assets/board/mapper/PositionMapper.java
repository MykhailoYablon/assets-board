package com.assets.board.mapper;

import com.assets.board.entity.Position;
import com.assets.board.model.ib.IBPosition;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PositionMapper {

    // DTO to Entity
    Position toEntity(IBPosition dto);

    // Entity to DTO
    IBPosition toDto(Position entity);

    // List of DTOs to List of Entities
    List<Position> toEntityList(List<IBPosition> dtoList);

    // List of Entities to List of DTOs
    List<IBPosition> toDtoList(List<Position> entityList);
}

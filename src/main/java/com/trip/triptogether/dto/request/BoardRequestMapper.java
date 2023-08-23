package com.trip.triptogether.dto.request;
import com.trip.triptogether.constant.GenericMapper;
import com.trip.triptogether.domain.Board;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
//Mapper 추후 사용해보기
public interface BoardRequestMapper extends GenericMapper<BoardRequest,Board> {
    BoardRequestMapper INSTANCE = Mappers.getMapper(BoardRequestMapper.class);
}

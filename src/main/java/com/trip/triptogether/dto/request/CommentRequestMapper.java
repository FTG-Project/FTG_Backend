package com.trip.triptogether.dto.request;

import com.trip.triptogether.constant.GenericMapper;
import com.trip.triptogether.domain.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CommentRequestMapper extends GenericMapper<CommentReqeust, Comment> {
    CommentRequestMapper INSTANCE= Mappers.getMapper(CommentRequestMapper.class);

}

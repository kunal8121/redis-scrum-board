package com.redisdemoproject.mapper;

import com.redisdemoproject.model.Comment;
import com.redisdemoproject.model.response.RestComment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "jsr330")
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    RestComment toRest(Comment comment);
}

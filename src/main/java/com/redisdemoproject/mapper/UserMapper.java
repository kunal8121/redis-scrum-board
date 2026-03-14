package com.redisdemoproject.mapper;

import com.redisdemoproject.model.User;
import com.redisdemoproject.model.response.RestUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "email", source = "emailId")
    RestUser toRestUser(User userDTO);
}

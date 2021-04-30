package net.shyshkin.study.webflux.userservice.mapper;

import net.shyshkin.study.webflux.userservice.dto.UserDto;
import net.shyshkin.study.webflux.userservice.entity.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {

    User toEntity(UserDto dto);

    UserDto toDto(User user);

}

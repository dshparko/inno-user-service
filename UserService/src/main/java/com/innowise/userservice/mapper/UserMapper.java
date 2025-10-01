package com.innowise.userservice.mapper;

import com.innowise.userservice.database.entity.User;
import com.innowise.userservice.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @ClassName UserMapper
 * @Description MapStruct-based mapper for converting between {@link User} entities and DTOs.
 * Provides bidirectional mapping methods for request and response models.
 * Used to decouple persistence layer from API contracts and reduce boilerplate conversion logic.
 * @Author dshparko
 * @Date 14.09.2025 19:09
 * @Version 1.0
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Maps a {@link UserDto} DTO to a {@link User} entity.
     * Used during user creation.
     *
     * @param request the DTO containing user creation data
     * @return the mapped user entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userCards", ignore = true)
    User mapToEntity(UserDto request);

    /**
     * Maps a list of {@link User} entities to a list of {@link UserDto} DTOs.
     *
     * @param entities the list of user entities to convert
     * @return the list of mapped response DTOs
     */
    List<UserDto> mapToDtoList(List<User> entities);

    /**
     * Maps a {@link User} entity to a {@link UserDto} DTO.
     * <p>
     * Transforms the {@code userCards} field from the entity into the {@code cards} field in the response,
     * enabling structured transfer of user data along with associated card information.
     *
     * @param entity the {@link User} entity to map
     * @return a {@link UserDto} containing user details and mapped card list
     */
    @Mapping(source = "userCards", target = "cards")
    UserDto mapToDto(User entity);
}

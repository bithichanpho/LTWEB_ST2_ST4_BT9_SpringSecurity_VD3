package web.programming.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import web.programming.dto.UserDTO;
import web.programming.entity.User;

/**
 * Mapper DTO <-> Entity bang MapStruct.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

	// Entity -> DTO
	@Mapping(target = "roleName", source = "role.name")
	UserDTO toDTO(User entity);

	// DTO -> Entity
	@Mapping(target = "role", ignore = true)
	@Mapping(target = "products", ignore = true)
	@Mapping(target = "password", ignore = true)
	User toEntity(UserDTO dto);
}

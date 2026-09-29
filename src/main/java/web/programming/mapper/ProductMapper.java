package web.programming.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import web.programming.dto.ProductDTO;
import web.programming.entity.Product;

/**
 * Mapper DTO <-> Entity bang MapStruct.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

	// Entity -> DTO
	@Mapping(target = "id", source = "productId")
	@Mapping(target = "name", source = "productName")
	@Mapping(target = "imageUrl", source = "images")
	@Mapping(target = "userId", source = "user.id")
	@Mapping(target = "username", source = "user.username")
	@Mapping(target = "image", ignore = true)
	ProductDTO toDTO(Product entity);

	// DTO -> Entity
	@Mapping(target = "productId", source = "id")
	@Mapping(target = "productName", source = "name")
	@Mapping(target = "images", source = "imageUrl")
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	Product toEntity(ProductDTO dto);
}

package com.zim4ik.spacecatmarket.product.mapper;

import com.zim4ik.spacecatmarket.category.model.Category;
import com.zim4ik.spacecatmarket.product.dto.ProductDTO;
import com.zim4ik.spacecatmarket.product.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ProductMapper {

    @Mapping(target = "categoryId", source = "category", qualifiedByName = "categoryToCategoryId")
    ProductDTO productToProductDto(Product product);

    @Named("categoryToCategoryId")
    default Long categoryToCategoryId(Category category) {
        return category != null ? category.getId() : null;
    }

}

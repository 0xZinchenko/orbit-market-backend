package com.zim4ik.spacecatmarket.category.mapper;

import com.zim4ik.spacecatmarket.category.dto.CategoryDTO;
import com.zim4ik.spacecatmarket.category.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CategoryMapper {

    CategoryDTO categoryToCategoryDto(Category category);

}

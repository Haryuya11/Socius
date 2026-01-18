package com.uit.sociusmvcapp.iam.internal.converter;

import com.uit.sociusmvcapp.iam.internal.domain.ApiPermission;
import com.uit.sociusmvcapp.iam.internal.dto.ApiPermissionDto;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/** MapStruct converter for ApiPermission entity and DTO. */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ApiPermissionConverter {

  /**
   * Convert ApiPermission entity to DTO.
   *
   * @param entity the ApiPermission entity
   * @return the ApiPermissionDto
   */
  ApiPermissionDto entityToDto(ApiPermission entity);

  /**
   * Convert list of ApiPermission entities to list of DTOs.
   *
   * @param entities list of ApiPermission entities
   * @return list of ApiPermissionDto
   */
  List<ApiPermissionDto> entitiesToDtos(List<ApiPermission> entities);
}

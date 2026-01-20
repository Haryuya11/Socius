package com.uit.sociusmvcapp.iam.internal.converter;

import com.uit.sociusmvcapp.iam.internal.domain.ApiPermission;
import com.uit.sociusmvcapp.iam.internal.dto.ApiPermissionDto;
import com.uit.sociusmvcapp.shared.converter.BaseConverter;
import org.mapstruct.Mapper;

/** MapStruct converter for ApiPermission entity and DTO. */
@Mapper(componentModel = "spring")
public interface ApiPermissionConverter extends BaseConverter<ApiPermission, ApiPermissionDto> {}

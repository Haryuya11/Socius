package com.uit.sociusmvcapp.iam.internal.converter;

import com.uit.sociusmvcapp.iam.internal.domain.RolePermission;
import com.uit.sociusmvcapp.iam.internal.dto.RolePermissionDto;
import com.uit.sociusmvcapp.shared.converter.BaseConverter;
import org.mapstruct.Mapper;

/** Converter interface for RolePermission entity and RolePermissionDto. */
@Mapper(componentModel = "spring")
public interface RolePermissionConverter extends BaseConverter<RolePermission, RolePermissionDto> {}

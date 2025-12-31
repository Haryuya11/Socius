package com.uit.sociusmvcapp.iam.internal.converter;

import com.uit.sociusmvcapp.iam.dto.PermissionDto;
import com.uit.sociusmvcapp.iam.internal.domain.Permission;
import com.uit.sociusmvcapp.shared.converter.BaseConverter;
import org.mapstruct.Mapper;

/** Converter interface for Permission entity to PermissionDto. */
@Mapper(componentModel = "spring")
public interface PermissionConverter extends BaseConverter<Permission, PermissionDto> {}

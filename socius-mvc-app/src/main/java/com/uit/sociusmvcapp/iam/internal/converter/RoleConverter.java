package com.uit.sociusmvcapp.iam.internal.converter;

import com.uit.sociusmvcapp.iam.dto.RoleDto;
import com.uit.sociusmvcapp.iam.internal.domain.Role;
import com.uit.sociusmvcapp.shared.converter.BaseConverter;
import org.mapstruct.Mapper;

/** Converter interface for Role and Permission entities to their respective DTOs. */
@Mapper(
    componentModel = "spring",
    uses = {PermissionConverter.class})
public interface RoleConverter extends BaseConverter<Role, RoleDto> {}

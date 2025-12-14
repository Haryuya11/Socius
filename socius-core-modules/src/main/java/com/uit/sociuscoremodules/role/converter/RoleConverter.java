package com.uit.sociuscoremodules.role.converter;

import com.uit.sociuscoremodules.role.domain.Permission;
import com.uit.sociuscoremodules.role.domain.Role;
import com.uit.sociuscoremodules.role.dto.PermissionDto;
import com.uit.sociuscoremodules.role.dto.RoleDto;
import com.uit.sociuscoremodules.role.dto.RoleWithPermissionsDto;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/** Converter interface for Role and Permission entities to their respective DTOs. */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleConverter {

  /** Converts a Role entity to a RoleDto. */
  RoleDto entityToDto(Role role);

  /** Converts a list of Role entities to a list of RoleDtos. */
  List<RoleDto> entitiesToDtos(List<Role> roles);

  /** Converts a Role entity to a RoleWithPermissionsDto, including its permissions. */
  RoleWithPermissionsDto toWithPermissionsDto(Role role);

  /** Converts a Permission entity to a PermissionDto. */
  PermissionDto permissionToDto(Permission permission);

  /** Converts a list of Permission entities to a list of PermissionDtos. */
  List<PermissionDto> permissionsToDtoList(List<Permission> permissions);
}

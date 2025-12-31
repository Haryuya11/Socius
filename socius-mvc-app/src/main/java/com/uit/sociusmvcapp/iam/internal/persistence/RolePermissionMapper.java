package com.uit.sociusmvcapp.iam.internal.persistence;

import com.uit.sociusmvcapp.iam.internal.domain.RolePermission;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** Mapper interface for RolePermission entity. */
@Mapper
public interface RolePermissionMapper {

  /**
   * Find permissions associated with given role codes.
   *
   * @param roleCodes list of role codes
   * @return list of RolePermissionMapping containing permissions for each role code
   */
  List<RolePermission> findPermissionsByRoleCodes(@Param("roleCodes") List<String> roleCodes);
}

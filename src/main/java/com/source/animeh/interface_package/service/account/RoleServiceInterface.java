package com.source.animeh.interface_package.service.account;

import com.source.animeh.dto.request.account.RoleRequest;
import com.source.animeh.dto.response.account.RoleResponse;
import java.util.List;

public interface RoleServiceInterface {

  // Lấy hết toàn bộ Role
  List<RoleResponse> getAllRoles();

  // Tạo Role
  RoleResponse createRole(RoleRequest roleRequest);

  // Chỉnh sửa Role
  RoleResponse updateRole(
      String id,
      RoleRequest updatedRoleRequest);

  // Xoá Role
  void deleteRole(String id);

}

package com.source.animeh.controller.account;

import com.source.animeh.dto.api.ApiResponse;
import com.source.animeh.dto.request.account.RoleRequest;
import com.source.animeh.dto.response.account.RoleResponse;
import com.source.animeh.service.account.RoleService;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller quản lý các thao tác liên quan đến Role.
 * <p>
 * Các endpoint và thông báo được định nghĩa dưới dạng hằng số để dễ dàng quản lý và thay đổi.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
@RequestMapping(RoleController.ENDPOINT_BASE)
public class RoleController {

  // Base endpoint
  public static final String ENDPOINT_BASE = "/admin";
  // Các endpoint con
  public static final String ENDPOINT_GET_ALL_ROLES = "/roles";
  public static final String ENDPOINT_CREATE_ROLE = "/role";
  public static final String ENDPOINT_UPDATE_ROLE = "/role/{id}";
  public static final String ENDPOINT_DELETE_ROLE = "/role/{id}";
  // Các thông báo phản hồi
  public static final String MSG_GET_ALL_ROLES_SUCCESS = "Get All Roles Successfully!";
  public static final String MSG_CREATE_ROLE_SUCCESS = "Create Role Successfully!";
  public static final String MSG_UPDATE_ROLE_SUCCESS = "Update Role Successfully!";
  public static final String MSG_DELETE_ROLE_SUCCESS = "Delete Role Successfully!";
  RoleService roleService;

  /**
   * Lấy danh sách tất cả các Role.
   *
   * @return ApiResponse chứa danh sách các Role
   */
  @GetMapping(ENDPOINT_GET_ALL_ROLES)
  @PreAuthorize("hasRole('ADMIN')")
  ApiResponse<List<RoleResponse>> getAllRoles() {
    List<RoleResponse> roles = roleService.getAllRoles();
    return ApiResponse.<List<RoleResponse>>builder()
        .message(MSG_GET_ALL_ROLES_SUCCESS)
        .result(roles)
        .build();
  }

  /**
   * Tạo một Role mới.
   *
   * @param roleRequest thông tin yêu cầu tạo Role
   * @return ApiResponse chứa thông tin Role đã được tạo
   */
  @PostMapping(ENDPOINT_CREATE_ROLE)
  @PreAuthorize("hasRole('ADMIN')")
  ApiResponse<RoleResponse> createRole(@RequestBody RoleRequest roleRequest) {
    RoleResponse role = roleService.createRole(roleRequest);
    return ApiResponse.<RoleResponse>builder()
        .message(MSG_CREATE_ROLE_SUCCESS)
        .result(role)
        .build();
  }

  /**
   * Cập nhật thông tin một Role.
   *
   * @param id          mã định danh của Role cần cập nhật
   * @param roleRequest thông tin cập nhật Role
   * @return ApiResponse chứa thông tin Role đã được cập nhật
   */
  @PutMapping(ENDPOINT_UPDATE_ROLE)
  @PreAuthorize("hasRole('ADMIN')")
  ApiResponse<RoleResponse> updateRole(
      @PathVariable("id") String id,
      @RequestBody RoleRequest roleRequest) {
    RoleResponse role = roleService.updateRole(id, roleRequest);
    return ApiResponse.<RoleResponse>builder()
        .message(MSG_UPDATE_ROLE_SUCCESS)
        .result(role)
        .build();
  }

  /**
   * Xoá một Role.
   *
   * @param id mã định danh của Role cần xoá
   * @return ApiResponse thông báo quá trình xoá Role thành công
   */
  @DeleteMapping(ENDPOINT_DELETE_ROLE)
  @PreAuthorize("hasRole('ADMIN')")
  ApiResponse<Void> deleteRole(@PathVariable("id") String id) {
    roleService.deleteRole(id);
    return ApiResponse.<Void>builder()
        .message(MSG_DELETE_ROLE_SUCCESS)
        .build();
  }
}

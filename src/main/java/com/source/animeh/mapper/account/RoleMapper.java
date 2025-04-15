package com.source.animeh.mapper.account;

import com.source.animeh.dto.request.account.RoleRequest;
import com.source.animeh.dto.response.account.RoleResponse;
import com.source.animeh.entity.account.Role;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * Mapper chuyển đổi giữa đối tượng {@link RoleRequest}, {@link Role} và {@link RoleResponse}.
 * <p>
 * Mapper này chịu trách nhiệm chuyển đổi dữ liệu từ đối tượng yêu cầu (RoleRequest) sang thực thể
 * {@link Role} và ngược lại, chuyển đổi thực thể {@link Role} sang {@link RoleResponse} để trả về
 * cho client.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface RoleMapper {

  /**
   * Chuyển đổi đối tượng {@link RoleRequest} sang thực thể {@link Role}.
   *
   * @param roleRequest đối tượng {@code RoleRequest} chứa thông tin vai trò cần tạo
   * @return đối tượng {@code Role} được tạo từ {@code RoleRequest}
   */
  Role toRole(RoleRequest roleRequest);

  /**
   * Cập nhật thông tin của đối tượng {@link Role} dựa trên dữ liệu từ {@link RoleRequest}.
   *
   * @param roleRequest đối tượng {@code RoleRequest} chứa thông tin cập nhật
   * @param role        đối tượng {@code Role} cần được cập nhật (MappingTarget)
   */
  void updateRole(RoleRequest roleRequest, @MappingTarget Role role);

  /**
   * Chuyển đổi đối tượng {@link Role} sang {@link RoleResponse} để trả về cho client.
   *
   * @param role đối tượng {@code Role} cần chuyển đổi
   * @return đối tượng {@code RoleResponse} chứa thông tin của {@code Role}
   */
  RoleResponse toRoleResponse(Role role);
}

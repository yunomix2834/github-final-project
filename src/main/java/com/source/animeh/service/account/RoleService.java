package com.source.animeh.service.account;

import com.source.animeh.dto.request.account.RoleRequest;
import com.source.animeh.dto.response.account.RoleResponse;
import com.source.animeh.entity.account.Role;
import com.source.animeh.exception.AppException;
import com.source.animeh.exception.ErrorCode;
import com.source.animeh.interface_package.service.account.RoleServiceInterface;
import com.source.animeh.mapper.account.RoleMapper;
import com.source.animeh.repository.account.RoleRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService implements RoleServiceInterface {

  RoleRepository roleRepository;
  RoleMapper roleMapper;

  // Tìm role thông qua id
  Role findRoleById(String id) {
    return roleRepository
        .findById(id)
        .orElseThrow(
            () -> new AppException(ErrorCode.ROLE_NOT_FOUND)
        );
  }

  // Tìm role thông qua name
  public Role findRoleByName(String roleName) {
    return roleRepository
        .findByName(roleName)
        .orElseThrow(
            () -> new AppException(ErrorCode.ROLE_NOT_FOUND)
        );
  }

  public Role findRoleByNameThrowNull(String roleName) {
    return roleRepository
        .findByName(roleName)
        .orElse(null);
  }

  // Lấy hết toàn bộ Role
  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public List<RoleResponse> getAllRoles() {
    return roleRepository
        .findAll()
        .stream()
        .map(roleMapper::toRoleResponse)
        .toList();
  }

  // TODO Đang lỗi sửa lại sau
  // Tạo Role
  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public RoleResponse createRole(RoleRequest roleRequest) {

    Role role = new Role();
    role.setId(UUID.randomUUID().toString());
    roleMapper.toRole(roleRequest);
    roleRepository.save(role);

    return roleMapper.toRoleResponse(role);
  }

  // Chỉnh sửa Role
  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public RoleResponse updateRole(
      String id,
      RoleRequest updatedRoleRequest) {

    Role existingRole = findRoleById(id);

    roleMapper.updateRole(updatedRoleRequest, existingRole);
    existingRole.setCreatedAt(LocalDateTime.now());

    return roleMapper.toRoleResponse(
        roleRepository.save(existingRole));
  }

  // Xoá Role
  @PreAuthorize("hasRole('ADMIN')")
  @Override
  public void deleteRole(String id) {
    roleRepository.deleteById(id);
  }
}

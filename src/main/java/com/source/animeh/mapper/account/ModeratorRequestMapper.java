package com.source.animeh.mapper.account;

import com.source.animeh.dto.response.account.ModeratorRequest_Response;
import com.source.animeh.entity.account.ModeratorRequest;
import com.source.animeh.interface_package.mapper.BaseRoleFilterMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper chuyển đổi giữa thực thể {@link ModeratorRequest} và DTO
 * {@link ModeratorRequest_Response}.
 * <p>
 * Mapper này chịu trách nhiệm chuyển đổi dữ liệu từ {@code ModeratorRequest} sang
 * {@code ModeratorRequest_Response} và áp dụng các bộ lọc theo vai trò người dùng.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface ModeratorRequestMapper extends
    BaseRoleFilterMapper<ModeratorRequest, ModeratorRequest_Response> {

  // Các hằng số cho giá trị mapping (source fields)
  String USER_USERNAME_SOURCE = "user.username";
  String ADMIN_USERNAME_SOURCE = "admin.username";
  String USER_ID_SOURCE = "user.id";
  String ADMIN_ID_SOURCE = "admin.id";

  /**
   * Chuyển đổi đối tượng {@link ModeratorRequest} sang {@link ModeratorRequest_Response}.
   *
   * @param moderatorRequest đối tượng {@code ModeratorRequest} cần chuyển đổi
   * @return đối tượng {@code ModeratorRequest_Response} sau khi chuyển đổi
   */
  @Mapping(target = "userUsername", source = USER_USERNAME_SOURCE)
  @Mapping(target = "adminUsername", source = ADMIN_USERNAME_SOURCE)
  @Mapping(target = "userId", source = USER_ID_SOURCE)
  @Mapping(target = "adminId", source = ADMIN_ID_SOURCE)
  ModeratorRequest_Response toModeratorRequest_Response(ModeratorRequest moderatorRequest);

  /**
   * Lọc thông tin của {@link ModeratorRequest_Response} theo vai trò người dùng.
   * <p>
   * Phương thức này thực hiện việc loại bỏ các thông tin nhạy cảm như thời gian xử lý, tạo và cập
   * nhật.
   * </p>
   *
   * @param dto đối tượng {@code ModeratorRequest_Response} cần được lọc thông tin
   */
  @Override
  default void filterForUser(ModeratorRequest_Response dto) {
    BaseRoleFilterMapper.super.filterForUser(dto);
    dto.setProcessedAt(null);
    dto.setCreatedAt(null);
    dto.setUpdatedAt(null);
  }
}

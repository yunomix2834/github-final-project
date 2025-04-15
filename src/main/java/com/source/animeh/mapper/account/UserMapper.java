package com.source.animeh.mapper.account;

import com.source.animeh.dto.request.account.UserUpdateRequest;
import com.source.animeh.dto.response.account.ModeratorProfileResponse;
import com.source.animeh.dto.response.account.ProfileResponse;
import com.source.animeh.dto.response.account.UserResponse;
import com.source.animeh.entity.account.User;
import com.source.animeh.interface_package.mapper.BaseRoleFilterMapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper chuyển đổi giữa các đối tượng liên quan đến người dùng.
 * <p>
 * Mapper này chuyển đổi từ {@link UserUpdateRequest} sang {@link User} để cập nhật thông tin
 * (partial update), cũng như chuyển đổi {@link User} sang {@link UserResponse},
 * {@link ProfileResponse} hoặc {@link ModeratorProfileResponse} để trả về thông tin cho client.
 * Ngoài ra, các phương thức lọc thông tin theo vai trò người dùng cũng được định nghĩa trong mapper
 * này.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface UserMapper extends BaseRoleFilterMapper<User, UserResponse> {

  // Hằng số cho mapping các field
  String ROLE_FIELD = "role";
  String ROLE_NAME_FIELD = "role.name";

  /**
   * Cập nhật thông tin người dùng (User) dựa trên dữ liệu từ {@link UserUpdateRequest}.
   * <p>
   * Phương thức này sử dụng chiến lược partial update: chỉ cập nhật các trường có giá trị khác
   * null.
   * </p>
   *
   * @param userUpdateRequest đối tượng chứa thông tin cập nhật từ client
   * @param user              đối tượng {@link User} cần được cập nhật
   */
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateUser(UserUpdateRequest userUpdateRequest, @MappingTarget User user);

  /**
   * Chuyển đổi đối tượng {@link User} sang {@link UserResponse}.
   *
   * @param user đối tượng {@link User} cần chuyển đổi
   * @return đối tượng {@link UserResponse} chứa thông tin của người dùng
   */
  @Mapping(target = ROLE_FIELD, source = ROLE_NAME_FIELD)
  UserResponse toUserResponse(User user);

  /**
   * Chuyển đổi đối tượng {@link User} sang {@link ProfileResponse}.
   *
   * @param user đối tượng {@link User} cần chuyển đổi
   * @return đối tượng {@link ProfileResponse} chứa thông tin hồ sơ của người dùng
   */
  @Mapping(target = ROLE_FIELD, source = ROLE_NAME_FIELD)
  ProfileResponse toProfileResponse(User user);

  /**
   * Chuyển đổi đối tượng {@link User} sang {@link ModeratorProfileResponse}.
   *
   * @param user đối tượng {@link User} cần chuyển đổi
   * @return đối tượng {@link ModeratorProfileResponse} chứa thông tin hồ sơ dành cho moderator
   */
  ModeratorProfileResponse toModeratorProfileResponse(User user);

  /**
   * Lọc thông tin của {@link UserResponse} theo vai trò người dùng.
   *
   * @param dto đối tượng {@link UserResponse} cần lọc thông tin
   */
  @Override
  default void filterForUser(UserResponse dto) {
    BaseRoleFilterMapper.super.filterForUser(dto);
  }

  /**
   * Lọc thông tin của {@link UserResponse} theo vai trò moderator.
   *
   * @param dto đối tượng {@link UserResponse} cần lọc thông tin
   */
  @Override
  default void filterForModerator(UserResponse dto) {
    BaseRoleFilterMapper.super.filterForModerator(dto);
  }
}

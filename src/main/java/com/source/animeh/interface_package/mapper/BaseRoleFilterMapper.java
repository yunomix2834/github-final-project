package com.source.animeh.interface_package.mapper;

import static com.source.animeh.constant.account.PredefinedRole.ADMIN;
import static com.source.animeh.constant.account.PredefinedRole.MOD;

import com.source.animeh.entity.account.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.MappingTarget;

public interface BaseRoleFilterMapper<E, D> {

  @AfterMapping
  default void afterMapping(
      E entity,
      @MappingTarget D dto,
      @Context User currentUser
  ) {
    filterFieldsByRole(entity, dto, currentUser);
  }

  default void filterFieldsByRole(
      E ignoredEntity,
      D dto,
      User currentUser) {
    if (currentUser == null || currentUser.getRole() == null) {
      // Không có user/role => coi như user thường
      filterForUser(dto);
      return;
    }

    String roleName = currentUser.getRole().getName();

    switch (roleName) {
      case ADMIN:
        break;
      case MOD:
        filterForModerator(dto);
        break;
      default:
        filterForUser(dto);
        break;

    }
  }

  default void filterForUser(D dto) {
  }

  default void filterForModerator(D dto) {
  }
}

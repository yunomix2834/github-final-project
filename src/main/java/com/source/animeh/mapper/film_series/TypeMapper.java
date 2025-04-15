package com.source.animeh.mapper.film_series;

import com.source.animeh.dto.request.film_series.type.TypeUpdateRequest;
import com.source.animeh.dto.response.film_series.type.TypeResponse;
import com.source.animeh.entity.film_series.Type;
import com.source.animeh.interface_package.mapper.BaseRoleFilterMapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper chuyển đổi giữa đối tượng {@link Type} và {@link TypeResponse}.
 * <p>
 * Mapper này chịu trách nhiệm chuyển đổi dữ liệu từ entity {@code Type} sang DTO
 * {@code TypeResponse} và cập nhật thông tin của {@code Type} từ {@code TypeUpdateRequest} theo
 * chiến lược partial update.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface TypeMapper extends BaseRoleFilterMapper<Type, TypeResponse> {

  // ===================== Các hằng số mapping =====================
  String FIELD_ID = "id";
  String FIELD_CREATED_AT = "createdAt";
  String FIELD_UPDATED_AT = "updatedAt";

  /**
   * Chuyển đổi đối tượng {@link Type} sang {@link TypeResponse}.
   *
   * @param type đối tượng {@code Type} cần chuyển đổi
   * @return đối tượng {@code TypeResponse} chứa thông tin của type
   */
  TypeResponse toTypeResponse(Type type);

  /**
   * Cập nhật thông tin của đối tượng {@link Type} từ {@link TypeUpdateRequest}.
   * <p>
   * Áp dụng chiến lược partial update: chỉ cập nhật các trường có giá trị khác null. Các trường sau
   * sẽ bị bỏ qua: {@code id}, {@code createdAt}, {@code updatedAt}.
   * </p>
   *
   * @param type              đối tượng {@code Type} cần được cập nhật
   * @param typeUpdateRequest đối tượng chứa thông tin cập nhật
   */
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = FIELD_ID, ignore = true)
  @Mapping(target = FIELD_CREATED_AT, ignore = true)
  @Mapping(target = FIELD_UPDATED_AT, ignore = true)
  void updateType(@MappingTarget Type type, TypeUpdateRequest typeUpdateRequest);

  /**
   * Lọc thông tin của {@link TypeResponse} khi hiển thị cho người dùng.
   * <p>
   * Các trường nhạy cảm như {@code createdAt} và {@code updatedAt} sẽ được ẩn đi.
   * </p>
   *
   * @param dto đối tượng {@code TypeResponse} cần lọc thông tin
   */
  @Override
  default void filterForUser(TypeResponse dto) {
    BaseRoleFilterMapper.super.filterForUser(dto);
    dto.setCreatedAt(null);
    dto.setUpdatedAt(null);
  }
}

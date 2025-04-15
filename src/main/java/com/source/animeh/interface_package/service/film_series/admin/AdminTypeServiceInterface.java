package com.source.animeh.interface_package.service.film_series.admin;

import com.source.animeh.dto.request.film_series.type.AddTypeToAnimeRequest;
import com.source.animeh.dto.request.film_series.type.TypeCreateRequest;
import com.source.animeh.dto.request.film_series.type.TypeUpdateRequest;
import com.source.animeh.dto.response.film_series.type.TypeResponse;
import java.util.List;

public interface AdminTypeServiceInterface {

  /**
   * Lấy 1 Type theo id
   */
  TypeResponse getTypeById(String id);

  /**
   * Lấy các Type theo type
   */
  List<TypeResponse> getTypesByType(
      String type);

  /**
   * Lấy toàn bộ Type
   */
  List<TypeResponse> getAllTypes();

  /**
   * Tạo mới 1 Type
   */
  // TODO Type của type cố định
  TypeResponse createType(TypeCreateRequest typeCreateRequest);

  /**
   * Cập nhật Type
   */
  TypeResponse updateType(
      String typeId,
      TypeUpdateRequest typeUpdateRequest);

  /**
   * Xoá Type
   */
  void deleteType(String typeId);

  /**
   * Thêm Type cho Anime theo typeName
   */
  void addTypeToAnime(
      AddTypeToAnimeRequest addTypeToAnimeRequest);
}

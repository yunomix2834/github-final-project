package com.source.animeh.controller.film_series.admin;

import com.source.animeh.dto.api.ApiResponse;
import com.source.animeh.dto.request.film_series.type.AddTypeToAnimeRequest;
import com.source.animeh.dto.request.film_series.type.TypeCreateRequest;
import com.source.animeh.dto.request.film_series.type.TypeUpdateRequest;
import com.source.animeh.dto.response.film_series.type.TypeResponse;
import com.source.animeh.service.film_series.admin.AdminTypeService;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller quản lý các thao tác liên quan đến Type của phim/series.
 * <p>
 * Các endpoint và thông báo được định nghĩa dưới dạng hằng số để dễ quản lý và thay đổi.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
public class AdminTypeController {

  // Endpoint cho các thao tác đọc
  public static final String ENDPOINT_GET_ALL_TYPES = "/animes-query/types";
  public static final String ENDPOINT_GET_TYPE_BY_ID = "/animes-query/type/{typeId}";
  public static final String ENDPOINT_GET_TYPES_BY_TYPE = "/animes-query/types-byType";
  // Endpoint cho các thao tác tạo, cập nhật, xoá
  public static final String ENDPOINT_CREATE_TYPE = "/admin/type";
  public static final String ENDPOINT_UPDATE_TYPE = "/admin/type/{typeId}";
  public static final String ENDPOINT_DELETE_TYPE = "/admin/type/{typeId}";
  public static final String ENDPOINT_ADD_TYPE_TO_ANIME = "/admin/type/add-to-anime";
  // Các thông báo phản hồi
  public static final String MSG_GET_ALL_TYPES_SUCCESS = "Get all types successfully!";
  public static final String MSG_GET_TYPE_SUCCESS = "Get type successfully!";
  public static final String MSG_GET_TYPES_SUCCESS = "Get types successfully!";
  public static final String MSG_CREATE_TYPE_SUCCESS = "Create type successfully!";
  public static final String MSG_UPDATE_TYPE_SUCCESS = "Update type successfully!";
  public static final String MSG_DELETE_TYPE_SUCCESS = "Delete type successfully!";
  public static final String MSG_ADD_TYPE_SUCCESS = "Add type successfully!";
  AdminTypeService adminTypeService;

  /**
   * [GET] Lấy danh sách tất cả các Type.
   *
   * @return ApiResponse chứa danh sách các Type
   */
  @GetMapping(ENDPOINT_GET_ALL_TYPES)
  ApiResponse<List<TypeResponse>> getAllTypes() {
    List<TypeResponse> types = adminTypeService.getAllTypes();
    return ApiResponse.<List<TypeResponse>>builder()
        .message(MSG_GET_ALL_TYPES_SUCCESS)
        .result(types)
        .build();
  }

  /**
   * [POST] Tạo mới một Type.
   *
   * @param req yêu cầu tạo mới Type
   * @return ApiResponse chứa thông tin Type được tạo
   */
  @PostMapping(ENDPOINT_CREATE_TYPE)
  @PreAuthorize("hasRole('ADMIN')")
  ApiResponse<TypeResponse> createType(@RequestBody TypeCreateRequest req) {
    TypeResponse type = adminTypeService.createType(req);
    return ApiResponse.<TypeResponse>builder()
        .message(MSG_CREATE_TYPE_SUCCESS)
        .result(type)
        .build();
  }

  /**
   * [GET] Lấy thông tin của một Type theo ID.
   *
   * @param typeId mã định danh của Type
   * @return ApiResponse chứa thông tin của Type
   */
  @GetMapping(ENDPOINT_GET_TYPE_BY_ID)
  ApiResponse<TypeResponse> getTypeById(@PathVariable("typeId") String typeId) {
    TypeResponse type = adminTypeService.getTypeById(typeId);
    return ApiResponse.<TypeResponse>builder()
        .message(MSG_GET_TYPE_SUCCESS)
        .result(type)
        .build();
  }

  /**
   * [GET] Lấy danh sách các Type theo tên Type.
   *
   * @param type tên của Type cần tìm
   * @return ApiResponse chứa danh sách các Type phù hợp
   */
  @GetMapping(ENDPOINT_GET_TYPES_BY_TYPE)
  ApiResponse<List<TypeResponse>> getTypesByType(@RequestParam String type) {
    List<TypeResponse> types = adminTypeService.getTypesByType(type);
    return ApiResponse.<List<TypeResponse>>builder()
        .message(MSG_GET_TYPES_SUCCESS)
        .result(types)
        .build();
  }

  /**
   * [PATCH] Cập nhật thông tin của một Type.
   *
   * @param typeId mã định danh của Type cần cập nhật
   * @param req    yêu cầu cập nhật thông tin Type
   * @return ApiResponse chứa thông tin của Type sau khi cập nhật
   */
  @PatchMapping(ENDPOINT_UPDATE_TYPE)
  @PreAuthorize("hasRole('ADMIN')")
  ApiResponse<TypeResponse> updateType(@PathVariable("typeId") String typeId,
      @RequestBody TypeUpdateRequest req) {
    TypeResponse updatedType = adminTypeService.updateType(typeId, req);
    return ApiResponse.<TypeResponse>builder()
        .message(MSG_UPDATE_TYPE_SUCCESS)
        .result(updatedType)
        .build();
  }

  /**
   * [DELETE] Xoá một Type.
   *
   * @param typeId mã định danh của Type cần xoá
   * @return ApiResponse thông báo quá trình xoá thành công
   */
  @DeleteMapping(ENDPOINT_DELETE_TYPE)
  @PreAuthorize("hasRole('ADMIN')")
  ApiResponse<Void> deleteType(@PathVariable String typeId) {
    adminTypeService.deleteType(typeId);
    return ApiResponse.<Void>builder()
        .message(MSG_DELETE_TYPE_SUCCESS)
        .build();
  }

  /**
   * [POST] Gán một Type cho Anime theo tên Type.
   *
   * @param req yêu cầu gán Type cho Anime
   * @return ApiResponse thông báo quá trình gán Type thành công
   */
  @PostMapping(ENDPOINT_ADD_TYPE_TO_ANIME)
  @PreAuthorize("hasRole('ADMIN')")
  ApiResponse<Void> addTypeToAnime(@RequestBody AddTypeToAnimeRequest req) {
    adminTypeService.addTypeToAnime(req);
    return ApiResponse.<Void>builder()
        .message(MSG_ADD_TYPE_SUCCESS)
        .build();
  }
}

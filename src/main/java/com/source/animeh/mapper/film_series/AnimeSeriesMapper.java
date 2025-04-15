package com.source.animeh.mapper.film_series;

import com.source.animeh.dto.request.film_series.anime_series.AnimeSeriesCreateRequest;
import com.source.animeh.dto.request.film_series.anime_series.AnimeSeriesUpdateRequest;
import com.source.animeh.dto.response.film_series.anime.AnimeInSeriesResponse;
import com.source.animeh.dto.response.film_series.anime_series.AnimeSeriesResponse;
import com.source.animeh.entity.account.User;
import com.source.animeh.entity.film_series.Anime;
import com.source.animeh.entity.film_series.AnimeSeries;
import com.source.animeh.interface_package.mapper.BaseRoleFilterMapper;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper chuyển đổi giữa các đối tượng liên quan đến AnimeSeries và các DTO tương ứng.
 * <p>
 * Bao gồm các chuyển đổi:
 * <ul>
 *   <li>Chuyển từ {@link AnimeSeriesCreateRequest} sang {@link AnimeSeries} (tạo mới)</li>
 *   <li>Cập nhật thông tin {@link AnimeSeries} từ {@link AnimeSeriesUpdateRequest} (partial update)</li>
 *   <li>Chuyển từ {@link AnimeSeries} sang {@link AnimeSeriesResponse}</li>
 *   <li>Hỗ trợ chuyển đổi danh sách {@link Anime} sang danh sách {@link AnimeInSeriesResponse}</li>
 *   <li>Áp dụng các filter theo vai trò người dùng.</li>
 * </ul>
 * </p>
 */
@Mapper(componentModel = "spring", uses = {AnimeMapper.class})
public interface AnimeSeriesMapper extends BaseRoleFilterMapper<AnimeSeries, AnimeSeriesResponse> {

  // =================== Các hằng số cho mapping ===================
  String FIELD_ID = "id";
  String FIELD_POSTER_URL = "posterUrl";
  String FIELD_BANNER_URL = "bannerUrl";
  String FIELD_CREATED_AT = "createdAt";
  String FIELD_UPDATED_AT = "updatedAt";
  String FIELD_ANIMES = "animes";

  // =================== Các phương thức mapping ===================

  /**
   * Chuyển đổi đối tượng {@link AnimeSeriesCreateRequest} sang đối tượng {@link AnimeSeries}.
   * <p>
   * Các trường sau sẽ bị ignore: id, posterUrl, bannerUrl, createdAt, updatedAt, animes.
   * </p>
   *
   * @param request đối tượng {@code AnimeSeriesCreateRequest} chứa thông tin tạo series
   * @return đối tượng {@code AnimeSeries} mới được tạo từ {@code request}
   */
  @Mapping(target = FIELD_ID, ignore = true)
  @Mapping(target = FIELD_POSTER_URL, ignore = true)
  @Mapping(target = FIELD_BANNER_URL, ignore = true)
  @Mapping(target = FIELD_CREATED_AT, ignore = true)
  @Mapping(target = FIELD_UPDATED_AT, ignore = true)
  @Mapping(target = FIELD_ANIMES, ignore = true)
  AnimeSeries toAnimeSeries(AnimeSeriesCreateRequest request);

  /**
   * Cập nhật thông tin của đối tượng {@link AnimeSeries} từ {@link AnimeSeriesUpdateRequest}.
   * <p>
   * Áp dụng chiến lược partial update: chỉ cập nhật các trường có giá trị khác null. Các trường sau
   * bị ignore: id, posterUrl, bannerUrl, createdAt, animes.
   * </p>
   *
   * @param animeSeries đối tượng {@code AnimeSeries} cần được cập nhật
   * @param request     đối tượng {@code AnimeSeriesUpdateRequest} chứa thông tin cập nhật
   */
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = FIELD_ID, ignore = true)
  @Mapping(target = FIELD_POSTER_URL, ignore = true)
  @Mapping(target = FIELD_BANNER_URL, ignore = true)
  @Mapping(target = FIELD_CREATED_AT, ignore = true)
  @Mapping(target = FIELD_ANIMES, ignore = true)
  void updateAnimeSeries(@MappingTarget AnimeSeries animeSeries, AnimeSeriesUpdateRequest request);

  /**
   * Chuyển đổi đối tượng {@link AnimeSeries} sang {@link AnimeSeriesResponse} với thông tin của
   * người dùng hiện tại.
   *
   * @param animeSeries đối tượng {@code AnimeSeries} cần chuyển đổi
   * @param currentUser đối tượng {@code User} hiện tại (để áp dụng các filter nếu cần)
   * @return đối tượng {@code AnimeSeriesResponse} chứa thông tin của series
   */
  @Mapping(target = FIELD_ANIMES, ignore = true)
  AnimeSeriesResponse toAnimeSeriesResponse(AnimeSeries animeSeries, @Context User currentUser);

  /**
   * Chuyển đổi danh sách đối tượng {@link Anime} sang danh sách {@link AnimeInSeriesResponse}.
   *
   * @param animes danh sách {@code Anime} cần chuyển đổi
   * @return danh sách {@code AnimeInSeriesResponse} tương ứng; nếu danh sách đầu vào null thì trả
   * về danh sách rỗng
   */
  default List<AnimeInSeriesResponse> mapSubAnimes(List<Anime> animes) {
    if (animes == null) {
      return new ArrayList<>();
    }
    return animes.stream()
        .map(a -> {
          AnimeInSeriesResponse dto = new AnimeInSeriesResponse();
          dto.setId(a.getId());
          dto.setTitle(a.getTitle());
          dto.setSeriesOrder(a.getSeriesOrder());
          dto.setPosterUrl(a.getPosterUrl());
          dto.setBannerUrl(a.getBannerUrl());
          dto.setAverageRating(a.getAverageRating());
          dto.setViewCount(a.getViewCount());
          return dto;
        })
        .toList();
  }

  // =================== Các phương thức filter thông tin theo vai trò ===================

  /**
   * Lọc thông tin của {@link AnimeSeriesResponse} khi hiển thị cho người dùng.
   * <p>
   * Các trường nhạy cảm như createdAt và updatedAt sẽ bị ẩn đi.
   * </p>
   *
   * @param dto đối tượng {@code AnimeSeriesResponse} cần lọc thông tin
   */
  @Override
  default void filterForUser(AnimeSeriesResponse dto) {
    BaseRoleFilterMapper.super.filterForUser(dto);
    dto.setCreatedAt(null);
    dto.setUpdatedAt(null);
  }

  /**
   * Lọc thông tin của {@link AnimeSeriesResponse} khi hiển thị cho moderator.
   *
   * @param dto đối tượng {@code AnimeSeriesResponse} cần lọc thông tin
   */
  @Override
  default void filterForModerator(AnimeSeriesResponse dto) {
    BaseRoleFilterMapper.super.filterForModerator(dto);
  }
}

package com.source.animeh.service.film_series.admin;

import com.source.animeh.dto.request.film_series.type.AddTypeToAnimeRequest;
import com.source.animeh.dto.request.film_series.type.TypeCreateRequest;
import com.source.animeh.dto.request.film_series.type.TypeUpdateRequest;
import com.source.animeh.dto.response.film_series.type.TypeResponse;
import com.source.animeh.entity.film_series.Anime;
import com.source.animeh.entity.film_series.AnimeType;
import com.source.animeh.entity.film_series.Type;
import com.source.animeh.exception.AppException;
import com.source.animeh.exception.ErrorCode;
import com.source.animeh.interface_package.service.film_series.admin.AdminTypeServiceInterface;
import com.source.animeh.mapper.film_series.TypeMapper;
import com.source.animeh.repository.film_series.AnimeRepository;
import com.source.animeh.repository.film_series.AnimeTypeRepository;
import com.source.animeh.repository.film_series.TypeRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminTypeService implements AdminTypeServiceInterface {

  TypeRepository typeRepository;
  AnimeRepository animeRepository;
  AnimeTypeRepository animeTypeRepository;
  TypeMapper typeMapper;

  /**
   * Lấy 1 Type theo id
   */
  @Override
  public TypeResponse getTypeById(String id) {
    Type type = typeRepository
        .findById(id)
        .orElseThrow(
            () -> new AppException(ErrorCode.TYPE_NOT_FOUND)
        );
    return typeMapper.toTypeResponse(type);
  }

  /**
   * Lấy các Type theo type
   */
  @Override
  public List<TypeResponse> getTypesByType(
      String type) {
    return typeRepository
        .findByType(type)
        .stream()
        .map(typeMapper::toTypeResponse)
        .toList();
  }

  /**
   * Lấy toàn bộ Type
   */
  @Override
  public List<TypeResponse> getAllTypes() {
    return typeRepository
        .findAll()
        .stream()
        .map(typeMapper::toTypeResponse)
        .toList();
  }

  /**
   * Tạo mới 1 Type
   */
  // TODO Type của type cố định
  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public TypeResponse createType(TypeCreateRequest typeCreateRequest) {
    // Kiểm tra đã tồn tại type (name+type) chưa
    if (typeRepository.existsByNameAndType(typeCreateRequest.getName(),
        typeCreateRequest.getType())) {
      throw new AppException(ErrorCode.TYPE_ALREADY_EXISTS);
    }

    Type newType = new Type();
    newType.setId(UUID.randomUUID().toString());
    newType.setName(typeCreateRequest.getName());
    newType.setType(typeCreateRequest.getType());
    newType.setDescription(typeCreateRequest.getDescription());
    newType.setCreatedAt(LocalDateTime.now());

    try {
      return typeMapper.toTypeResponse(
          typeRepository.save(newType)
      );
    } catch (DataIntegrityViolationException ex) {
      throw new AppException(ErrorCode.TYPE_ALREADY_EXISTS);
    }
  }

  /**
   * Cập nhật Type
   */
  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public TypeResponse updateType(
      String typeId,
      TypeUpdateRequest typeUpdateRequest) {

    Type type = typeRepository
        .findById(typeId)
        .orElseThrow(() -> new AppException(ErrorCode.TYPE_NOT_FOUND));

    typeMapper.updateType(type, typeUpdateRequest);
    type.setUpdatedAt(LocalDateTime.now());

    try {
      return typeMapper.toTypeResponse(type);
    } catch (DataIntegrityViolationException ex) {
      throw new AppException(ErrorCode.TYPE_ALREADY_EXISTS);
    }
  }

  /**
   * Xoá Type
   */
  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public void deleteType(String typeId) {
    typeRepository
        .findById(typeId)
        .orElseThrow(
            () -> new AppException(ErrorCode.TYPE_NOT_FOUND)
        );

    animeTypeRepository.deleteByTypeId(typeId);
  }

  /**
   * Thêm Type cho Anime theo typeName
   */
  @Override
  @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
  public void addTypeToAnime(
      AddTypeToAnimeRequest addTypeToAnimeRequest) {
    // Tìm anime
    Anime anime = animeRepository
        .findById(addTypeToAnimeRequest.getAnimeId())
        .orElseThrow(
            () -> new AppException(ErrorCode.ANIME_NOT_FOUND)
        );

    // Tìm type theo name => (có thể) theo name + type
    Type type = typeRepository
        .findByName(addTypeToAnimeRequest.getTypeName())
        .orElseThrow(() -> new AppException(ErrorCode.TYPE_NOT_FOUND));

    // Kểm tra xem đã có trong AnimeType hay chưa
    boolean alreadyExist = animeTypeRepository
        .existsByAnimeIdAndTypeId(
            anime.getId(),
            type.getId()
        );
    if (alreadyExist) {
      throw new AppException(ErrorCode.ANIME_TYPE_ALREADY_EXISTS);
    }

    AnimeType animeType = new AnimeType();
    animeType.setAnime(anime);
    animeType.setType(type);
    animeTypeRepository.save(animeType);
  }
}

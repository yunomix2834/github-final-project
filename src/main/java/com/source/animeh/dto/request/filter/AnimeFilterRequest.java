package com.source.animeh.dto.request.filter;

import java.math.BigDecimal;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnimeFilterRequest {

  // Tìm kiếm tên
  String title; // tên tuyệt đối hay 1 phần
  Boolean partialTitle; // true => LIKE, false => EQUAL

  // Lọc theo typeName của type="Loại phim" //
  List<String> movieTypes;

  // Lọc theo typeName của type="Tình trạng" //
  List<String> statusNames;

  // Lọc theo typeName của type="Năm phát hành" //
  List<String> releaseYearNames;

  // Lọc theo typeName của type="Studio" //
  List<String> studioNames;

  // Lọc theo typeName của type="Lịch chiếu phim" //
  List<String> scheduleNames;

  // Tương tự, type="Thể loại" => list //
  List<String> genreNames;

  // Quốc gia
  List<String> countryNames;

  // Lọc theo Rating
  BigDecimal minRating;

  // Lọc phim đề cử => rating >= 8.0?
  Boolean nominated;

  // Lọc mới cập nhật => sắp xếp updatedAt desc
  Boolean newlyUpdated;

  // Lọc theo trạng thái chung (APPROVED, PENDING, REJECTED)
  List<String> statusFilters;

  String submittedByUsername;   // Lọc theo người submit
  String reviewedByUsername;    // Lọc theo người review
}

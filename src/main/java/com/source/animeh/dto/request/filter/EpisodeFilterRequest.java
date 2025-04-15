package com.source.animeh.dto.request.filter;

import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EpisodeFilterRequest {

  // Filter theo animeId
  String animeId;
  // Tìm kiếm theo title
  String title;
  // Lọc partial hay exact
  Boolean partialTitle;
  // Lọc theo danh sách trạng thái
  List<String> statusFilters;

  String submittedByUsername;   // Lọc theo người submit
  String reviewedByUsername;    // Lọc theo người review
}

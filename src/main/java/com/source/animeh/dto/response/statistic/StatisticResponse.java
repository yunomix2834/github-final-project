package com.source.animeh.dto.response.statistic;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatisticResponse {

  long totalAnime;
  long tvSeries;
  long movie;
  long finishedMovie;
  long unfinishedMovie;
  long unapprovedMovie;
}

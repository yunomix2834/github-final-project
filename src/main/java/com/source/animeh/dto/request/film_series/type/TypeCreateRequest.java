package com.source.animeh.dto.request.film_series.type;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TypeCreateRequest {

  String name;
  String type;
  String description;
}

package com.source.animeh.dto.response.film_series.type;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class TypeResponse {

  String id;
  String name;
  String description;
  String type;
  LocalDateTime createdAt;
  LocalDateTime updatedAt;
}

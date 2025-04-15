package com.source.animeh.dto.request.user_interaction;

import com.source.animeh.data.LocalHistoryData;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocalHistoryRequest {

  List<LocalHistoryData> localData;
}

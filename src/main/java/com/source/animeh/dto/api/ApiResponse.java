package com.source.animeh.dto.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Lớp {@code ApiResponse} là đối tượng bọc chung cho phản hồi API.
 * <p>
 * Lớp này chứa thông tin về mã lỗi, thông điệp, trạng thái và kết quả trả về từ API. Các trường có
 * giá trị null sẽ không được đưa vào JSON do cấu hình {@code @JsonInclude}.
 * </p>
 *
 * @param <T> kiểu dữ liệu của kết quả trả về
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

  /**
   * Mã phản hồi mặc định (20000) cho các phản hồi thành công.
   */
  public static final int DEFAULT_CODE = 20000;

  /**
   * Trạng thái mặc định ("Ok") cho các phản hồi thành công.
   */
  public static final String DEFAULT_STATUS = "Ok";

  /**
   * Mã phản hồi. Mặc định là {@link #DEFAULT_CODE}.
   */
  @Builder.Default
  private int code = DEFAULT_CODE;

  /**
   * Thông điệp phản hồi.
   */
  private String message;

  /**
   * Trạng thái phản hồi. Mặc định là {@link #DEFAULT_STATUS}.
   */
  @Builder.Default
  private String status = DEFAULT_STATUS;

  /**
   * Kết quả trả về của API.
   */
  private T result;
}

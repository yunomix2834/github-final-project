package com.source.animeh.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * Lớp {@code AppException} mở rộng từ {@link RuntimeException}.
 * <p>
 * Lớp này được sử dụng để báo lỗi ứng dụng với thông tin chi tiết qua {@link ErrorCode}. Khi ngoại
 * lệ được ném ra, thông điệp của {@code ErrorCode} sẽ được sử dụng làm thông điệp lỗi.
 * </p>
 */
@Getter
@Setter
public class AppException extends RuntimeException {

  /**
   * Mã lỗi đi kèm với ngoại lệ.
   */
  private ErrorCode errorCode;

  /**
   * Khởi tạo một ngoại lệ ứng dụng với mã lỗi cụ thể.
   *
   * @param errorCode đối tượng {@link ErrorCode} chứa thông tin lỗi và thông điệp liên quan
   */
  public AppException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}

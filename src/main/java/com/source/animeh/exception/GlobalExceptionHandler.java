package com.source.animeh.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.source.animeh.constant.PredefinedValue;
import com.source.animeh.dto.api.ApiResponse;
import jakarta.validation.ConstraintViolation;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * GlobalExceptionHandler xử lý các ngoại lệ xảy ra trong ứng dụng và chuyển chúng thành các phản
 * hồi API có cấu trúc thông qua {@link ApiResponse}.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  // Hằng số log message
  private static final String LOG_EXCEPTION_MESSAGE = "Exception: ";

  // Hằng số placeholder để thay thế trong thông điệp lỗi
  private static final String PLACEHOLDER_MIN = "{min}";
  private static final String PLACEHOLDER_MAX = "{max}";

  /**
   * Xử lý các ngoại lệ RuntimeException không được bắt riêng.
   *
   * @param exception ngoại lệ RuntimeException được ném ra
   * @return ResponseEntity chứa ApiResponse với mã lỗi, thông điệp và trạng thái lỗi tương ứng
   */
  @ExceptionHandler(value = Exception.class)
  ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception) {
    log.error(LOG_EXCEPTION_MESSAGE, exception);
    ApiResponse apiResponse = new ApiResponse();
    apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
    apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
    apiResponse.setStatus(ErrorCode.UNCATEGORIZED_EXCEPTION.getStatus());
    return ResponseEntity.badRequest().body(apiResponse);
  }

  /**
   * Xử lý ngoại lệ ứng dụng (AppException) và chuyển thành ApiResponse.
   *
   * @param exception ngoại lệ AppException được ném ra
   * @return ResponseEntity chứa ApiResponse với thông tin lỗi tương ứng
   */
  @ExceptionHandler(value = AppException.class)
  ResponseEntity<ApiResponse> handlingAppException(AppException exception) {
    ErrorCode errorCode = exception.getErrorCode();
    ApiResponse apiResponse = new ApiResponse();
    apiResponse.setCode(errorCode.getCode());
    apiResponse.setMessage(errorCode.getMessage());
    apiResponse.setStatus(errorCode.getStatus());
    return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
  }

  /**
   * Xử lý ngoại lệ AccessDeniedException khi người dùng không có quyền truy cập.
   *
   * @param exception ngoại lệ AccessDeniedException
   * @return ResponseEntity chứa ApiResponse với thông tin lỗi truy cập
   */
  @ExceptionHandler(value = AccessDeniedException.class)
  ResponseEntity<ApiResponse> handlingAccessDeniedException(AccessDeniedException exception) {
    ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
    ApiResponse apiResponse = ApiResponse.builder()
        .code(errorCode.getCode())
        .message(errorCode.getMessage())
        .status(errorCode.getStatus())
        .build();
    return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
  }

  /**
   * Xử lý ngoại lệ MethodArgumentNotValidException khi tham số không hợp lệ.
   * <p>
   * Phương thức này lấy thông điệp lỗi từ field error, cố gắng ánh xạ sang {@link ErrorCode} và
   * thay thế các placeholder trong thông điệp nếu cần.
   * </p>
   *
   * @param exception ngoại lệ MethodArgumentNotValidException
   * @return ResponseEntity chứa ApiResponse với thông tin lỗi hợp lệ
   */
  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  ResponseEntity<ApiResponse> handlingValidation(MethodArgumentNotValidException exception) {
    String enumKey = exception.getFieldError().getDefaultMessage();
    ErrorCode errorCode = ErrorCode.INVALID_KEY;
    Map<String, Object> attributes = null;
    try {
      errorCode = ErrorCode.valueOf(enumKey);
      ConstraintViolation<?> constraintViolation = exception
          .getBindingResult()
          .getAllErrors()
          .get(0)
          .unwrap(ConstraintViolation.class);
      attributes = constraintViolation.getConstraintDescriptor().getAttributes();
      log.info(attributes.toString());
    } catch (IllegalArgumentException e) {
      // Nếu không tìm thấy ErrorCode phù hợp, giữ nguyên errorCode mặc định
    }
    ApiResponse apiResponse = new ApiResponse();
    apiResponse.setCode(errorCode.getCode());
    apiResponse.setMessage(
        Objects.nonNull(attributes)
            ? mapAttribute(errorCode.getMessage(), attributes)
            : errorCode.getMessage());
    return ResponseEntity.status(BAD_REQUEST).body(apiResponse);
  }

  /**
   * Thay thế các placeholder {min} và {max} trong thông điệp lỗi bằng giá trị tương ứng từ
   * attributes.
   *
   * @param message    thông điệp lỗi có chứa placeholder
   * @param attributes bản đồ chứa các thuộc tính của ràng buộc (constraint)
   * @return thông điệp lỗi sau khi đã thay thế placeholder
   */
  private String mapAttribute(String message, Map<String, Object> attributes) {
    String minValue = String.valueOf(attributes.get(PredefinedValue.MIN_ATTRIBUTE));
    String maxValue = String.valueOf(attributes.get(PredefinedValue.MAX_ATTRIBUTE));
    return message.replace(PLACEHOLDER_MIN, minValue)
        .replace(PLACEHOLDER_MAX, maxValue);
  }
}

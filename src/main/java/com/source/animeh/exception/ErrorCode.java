package com.source.animeh.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

/**
 * Enum {@code ErrorCode} định nghĩa các mã lỗi và thông điệp tương ứng cho ứng dụng.
 * <p>
 * Mỗi hằng số của enum bao gồm:
 * <ul>
 *   <li>Mã lỗi (code)</li>
 *   <li>Trạng thái HTTP dưới dạng chuỗi (status)</li>
 *   <li>Thông điệp lỗi (message) có thể chứa các placeholder như {min}, {max}, ...</li>
 *   <li>Mã trạng thái HTTP (statusCode)</li>
 * </ul>
 * </p>
 */
@Getter
public enum ErrorCode {

  // Các lỗi không phân loại và lỗi chung
  UNCATEGORIZED_EXCEPTION(99999, StatusConstants.INTERNAL_SERVER_STATUS, "Uncategorized error",
      INTERNAL_SERVER_ERROR),
  INVALID_KEY(10001, StatusConstants.INTERNAL_SERVER_STATUS, "Invalid key", INTERNAL_SERVER_ERROR),

  // 400 - Bad Request
  INVALID_USERNAME(40001, StatusConstants.BAD_REQUEST_STATUS,
      "Invalid username! Username must be at least {min} characters and {max} characters",
      BAD_REQUEST),
  INVALID_PASSWORD(40002, StatusConstants.BAD_REQUEST_STATUS,
      "Invalid password! Password must be at least {min} characters and {max} characters",
      BAD_REQUEST),
  INVALID_EMAIL(40003, StatusConstants.BAD_REQUEST_STATUS,
      "Invalid email! Email must be at least {min} characters and {max} characters", BAD_REQUEST),
  REQUIRED_USERNAME(40004, StatusConstants.BAD_REQUEST_STATUS, "Required username!", BAD_REQUEST),
  REQUIRED_PASSWORD(40005, StatusConstants.BAD_REQUEST_STATUS, "Required password!", BAD_REQUEST),
  REQUIRED_EMAIL(40006, StatusConstants.BAD_REQUEST_STATUS, "Required email!", BAD_REQUEST),
  INVALID_USERNAME_PASSWORD(40007, StatusConstants.BAD_REQUEST_STATUS,
      "Username, Password do not match!", BAD_REQUEST),
  INVALID_REQUEST_STATUS(40008, StatusConstants.BAD_REQUEST_STATUS, "Invalid request status!",
      BAD_REQUEST),
  INVALID_CREDENTIALS(40009, StatusConstants.BAD_REQUEST_STATUS, "Invalid credentials!",
      BAD_REQUEST),
  REQUIRED_TOKEN(40010, StatusConstants.BAD_REQUEST_STATUS, "Required token!", BAD_REQUEST),
  INVALID_TOKEN(40011, StatusConstants.BAD_REQUEST_STATUS, "Invalid token!", BAD_REQUEST),
  TOKEN_EXPIRED(40012, StatusConstants.BAD_REQUEST_STATUS, "Token expired!", BAD_REQUEST),
  TOKEN_ALREADY_USED(40013, StatusConstants.BAD_REQUEST_STATUS, "Token already used!", BAD_REQUEST),
  INVALID_STATUS_PENDING(40014, StatusConstants.BAD_REQUEST_STATUS, "Invalid status pending!",
      BAD_REQUEST),
  INVALID_STATUS_APPROVED(40015, StatusConstants.BAD_REQUEST_STATUS, "Invalid status approved!",
      BAD_REQUEST),
  INVALID_STATUS_REJECTED(40016, StatusConstants.BAD_REQUEST_STATUS, "Invalid status rejected!",
      BAD_REQUEST),
  INVALID_STATUS(40017, StatusConstants.BAD_REQUEST_STATUS, "Invalid status!", BAD_REQUEST),
  CANNOT_DELETE_FILES_IN_SERIES(40018, StatusConstants.BAD_REQUEST_STATUS,
      "Can't delete files inside!", BAD_REQUEST),
  INVALID_TOKEN_SIGNATURE(40019, StatusConstants.BAD_REQUEST_STATUS, "Invalid token signature!",
      BAD_REQUEST),
  EPISODE_NUMBER_INVALID(40020, StatusConstants.BAD_REQUEST_STATUS, "Episode number invalid!",
      BAD_REQUEST),
  INVALID_STATUS_UPDATE(40021, StatusConstants.BAD_REQUEST_STATUS, "Invalid status update!",
      BAD_REQUEST),
  INVALID_VALUE_RATING(40022, StatusConstants.BAD_REQUEST_STATUS, "Invalid value rating!",
      BAD_REQUEST),
  INVALID_DATE_UPDATE(40023, StatusConstants.BAD_REQUEST_STATUS, "Invalid date update!",
      BAD_REQUEST),
  INVALID_IMAGE_FORMAT(40024, StatusConstants.BAD_REQUEST_STATUS, "Invalid image format!",
      BAD_REQUEST),
  INVALID_FILE_NAME(40025, StatusConstants.BAD_REQUEST_STATUS, "Invalid file name!",
      BAD_REQUEST),

  // 401 - Unauthorized
  UNAUTHENTICATED(40101, StatusConstants.UNAUTHORIZED_STATUS, "Unauthenticated!",
      org.springframework.http.HttpStatus.UNAUTHORIZED),

  // 403 - Forbidden
  UNAUTHORIZED(40301, StatusConstants.FORBIDDEN_STATUS, "You do not have permission!", FORBIDDEN),

  // 404 - Not Found
  USER_NOT_FOUND(40401, StatusConstants.NOT_FOUND_STATUS, "User not found!", NOT_FOUND),
  ROLE_NOT_FOUND(40402, StatusConstants.NOT_FOUND_STATUS, "Role not found!", NOT_FOUND),
  REQUEST_NOT_FOUND(40403, StatusConstants.NOT_FOUND_STATUS, "Request not found!", NOT_FOUND),
  FILE_NOT_FOUND(40404, StatusConstants.NOT_FOUND_STATUS, "File not found!", NOT_FOUND),
  ANIME_NOT_FOUND(40405, StatusConstants.NOT_FOUND_STATUS, "Anime not found!", NOT_FOUND),
  EPISODE_NOT_FOUND(40406, StatusConstants.NOT_FOUND_STATUS, "Episode not found!", NOT_FOUND),
  ANIME_SERIES_NOT_FOUND(40407, StatusConstants.NOT_FOUND_STATUS, "Anime Series not found!",
      NOT_FOUND),
  TYPE_NOT_FOUND(40408, StatusConstants.NOT_FOUND_STATUS, "Type not found!", NOT_FOUND),
  COMMENT_NOT_FOUND(40409, StatusConstants.NOT_FOUND_STATUS, "Comment not found!", NOT_FOUND),
  HISTORY_NOT_FOUND(40410, StatusConstants.NOT_FOUND_STATUS, "History not found!", NOT_FOUND),
  SCHEDULE_NOT_FOUND(40411, StatusConstants.NOT_FOUND_STATUS, "Schedule not found!", NOT_FOUND),

  // 409 - Conflict
  USERNAME_ALREADY_EXISTS(40901, StatusConstants.CONFLICT_STATUS, "User already exists!", CONFLICT),
  EMAIL_ALREADY_EXISTS(40902, StatusConstants.CONFLICT_STATUS, "Email already exists!", CONFLICT),
  TYPE_ALREADY_EXISTS(40903, StatusConstants.CONFLICT_STATUS, "Type already exists!", CONFLICT),
  ANIME_TYPE_ALREADY_EXISTS(40904, StatusConstants.CONFLICT_STATUS, "Anime + Type already exists!",
      CONFLICT),
  EPISODE_NUMBER_ALREADY_APPROVED(40905, StatusConstants.CONFLICT_STATUS,
      "Episode number already approved!", CONFLICT),

  // 500 - Internal Server Error
  FAILED_SEND_MAIL(50000, StatusConstants.INTERNAL_SERVER_STATUS, "Failed to send mail!",
      INTERNAL_SERVER_ERROR),
  FAILED_READ_FOLDER(50001, StatusConstants.INTERNAL_SERVER_STATUS, "Failed to read folder!",
      INTERNAL_SERVER_ERROR),
  FAILED_DELETE_FILE(50002, StatusConstants.INTERNAL_SERVER_STATUS, "Failed to delete file!",
      INTERNAL_SERVER_ERROR),
  FAILED_FFMPEG_TRANSCODE(50003, StatusConstants.INTERNAL_SERVER_STATUS, "Failed to ffmpeg!",
      INTERNAL_SERVER_ERROR),
  FAILED_VALIDATE_TOKEN(50004, StatusConstants.INTERNAL_SERVER_STATUS, "Token validation error!",
      INTERNAL_SERVER_ERROR),
  FAILED_GENERATE_TOKEN(50005, StatusConstants.INTERNAL_SERVER_STATUS,
      "Error generating JWT token!", INTERNAL_SERVER_ERROR);

  private final int code;
  private final String status;
  private final String message;
  private final HttpStatusCode statusCode;

  ErrorCode(int code, String status, String message, HttpStatusCode statusCode) {
    this.code = code;
    this.status = status;
    this.message = message;
    this.statusCode = statusCode;
  }

  /**
   * Lớp tĩnh nội bộ chứa các hằng số trạng thái HTTP dưới dạng chuỗi.
   */
  private static class StatusConstants {

    private static final String INTERNAL_SERVER_STATUS = INTERNAL_SERVER_ERROR.toString();
    private static final String BAD_REQUEST_STATUS = BAD_REQUEST.toString();
    private static final String UNAUTHORIZED_STATUS = org.springframework.http.HttpStatus.UNAUTHORIZED.toString();
    private static final String FORBIDDEN_STATUS = FORBIDDEN.toString();
    private static final String NOT_FOUND_STATUS = NOT_FOUND.toString();
    private static final String CONFLICT_STATUS = CONFLICT.toString();
  }
}

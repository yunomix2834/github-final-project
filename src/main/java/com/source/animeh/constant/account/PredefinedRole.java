package com.source.animeh.constant.account;

public class PredefinedRole {

  public static final String USER = "ROLE_USER";
  public static final String ADMIN = "ROLE_ADMIN";
  public static final String MOD = "ROLE_MODERATOR";

  /**
   * Quy tắc phân cấp vai trò: ROLE_ADMIN > ROLE_MODERATOR > ROLE_USER
   */
  public static final String ROLE_HIERARCHY_RULES = "ROLE_ADMIN > ROLE_MODERATOR \nROLE_MODERATOR > ROLE_USER";

  public static final String ADMIN_DESCRIPTION = "ROLE_ADMIN: Quyền quản trị hệ thống, cho phép truy cập và điều chỉnh tất cả các tính năng, quản lý người dùng và hệ thống một cách toàn diện.";
  public static final String MODERATOR_DESCRIPTION = "ROLE_MODERATOR: Quyền điều hành, có khả năng quản lý nội dung và kiểm duyệt hoạt động trên hệ thống để duy trì môi trường an toàn và chất lượng.";
  public static final String USER_DESCRIPTION = "ROLE_USER: Quyền người dùng cơ bản, cho phép truy cập các tính năng tiêu chuẩn và sử dụng dịch vụ theo mức độ hạn chế.";

  private PredefinedRole() {
  }
}

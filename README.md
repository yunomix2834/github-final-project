# AnimeH - Backend

<!-- PROJECT SHIELDS  -->
<!-- 
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]
-->

## 1. Giới thiệu

AnimeH là dự án website xem phim anime (quản lý, đăng tải, đánh giá, bình luận, …) được xây dựng trên nền tảng Spring Boot. Đây là **backend** cung cấp các API, gồm:

- Chức năng **đăng ký/đăng nhập** (JWT)
- Quản lý **User** (thông tin cá nhân, cập nhật mật khẩu, upload avatar/background)
- Quản lý **Role** (phân quyền người dùng)
- Đăng ký **Moderator** (User yêu cầu, Admin duyệt)
- Chức năng **Anime** (lọc anime, top view, …)
- Quản lý file upload (avatar, background)
- Và nhiều thành phần khác...

Phiên bản hiện tại đang được phát triển và đã hoàn thiện cơ bản các chức năng chính nêu trên.

---

## 2. Công nghệ sử dụng

- **Java 21**
- **Spring Boot 3.4.2**
- **Spring Data JPA**
- **Spring Security** (JWT)
- **MapStruct** (chuyển đổi Entity <-> DTO)
- **Maven** (quản lý gói)
- **H2 Database** (cho test) & **MS SQL Server** (chính)
- **Lombok** (giảm code lặp)
- **Thumbnailator** (xử lý ảnh)
- **JUnit** & **Mockito** (kiểm thử)
- Và các thư viện liên quan khác...

---

## 3. Yêu cầu hệ thống (Requirements)

1. **Java 21** trở lên.
2. **Maven 3.8+** (hoặc dùng wrapper `mvnw`).
3. **SQL Server** (để chạy môi trường thực tế).
4. **Mail SMTP** (để gửi email quên mật khẩu, …). Mặc định cấu hình Gmail trong `application.properties`.

---

## 4. Cấu trúc thư mục (Directory Structure)

```
├── src
│   └── main
│       ├── java
│       │   └── com/source/animeh
│       │       ├── AnimehApplication.java        # File main khởi chạy Spring Boot
│       │       ├── configuration/               # Cấu hình Spring Security, Filter, ...
│       │       │   ├── config/...
│       │       │   └── filter/...
│       │       ├── constant/                    # Các hằng số toàn dự án
│       │       ├── controller/                  # Lớp controller - chứa các API endpoint
│       │       │   ├── account/...
│       │       │   ├── account/auth/...
│       │       │   └── film_series/...
│       │       ├── dto/                         # Các Data Transfer Object
│       │       │   ├── request/...
│       │       │   └── response/...
│       │       ├── entity/                      # Các Entity ánh xạ database
│       │       │   ├── account/...
│       │       │   ├── account/auth/...
│       │       │   ├── film_series/...
│       │       │   ├── episode/...
│       │       │   ├── store_transaction/...
│       │       │   └── user_interaction/...
│       │       ├── exception/                   # Xử lý ngoại lệ (GlobalExceptionHandler, AppException, …)
│       │       ├── interface_package/           # Các interface chung (ServiceInterface, MapperInterface, …)
│       │       ├── mapper/                      # MapStruct mapper
│       │       │   ├── account/...
│       │       │   └── film_series/...
│       │       ├── repository/                  # Giao tiếp database (JpaRepository, …)
│       │       │   ├── account/...
│       │       │   ├── account/auth/...
│       │       │   └── film_series/...
│       │       ├── service/                     # Business logic (Service)
│       │       │   ├── account/...
│       │       │   ├── account/auth/...
│       │       │   ├── film_series/...
│       │       │   └── file/...
│       │       └── utils/                       # Lớp tiện ích (JwtUtils, SecurityUtils, …)
│       └── resources
│           ├── application.properties           # Cấu hình chính
│           ├── application-test.properties      # Cấu hình cho test
│           └── static/ (nếu có)
└── pom.xml
```

---

## 5. Chức năng đã hoàn thiện

### 5.1. Quản lý Tài khoản - Xác thực (Authentication, Authorization)

**Tệp chính liên quan**:
- **Service**:
    - [`com.source.animeh.service.account.auth.AuthService`](./src/main/java/com/source/animeh/service/account/auth/AuthService.java)
    - [`com.source.animeh.service.account.UserService`](./src/main/java/com/source/animeh/service/account/UserService.java)
- **Controller**:
    - [`com.source.animeh.controller.account.auth.AuthController`](./src/main/java/com/source/animeh/controller/account/auth/AuthController.java)
    - [`com.source.animeh.controller.account.UserController`](./src/main/java/com/source/animeh/controller/account/UserController.java)
- **JwtAuthenticationFilter**:
    - [`com.source.animeh.configuration.filter.JwtAuthenticationFilter`](./src/main/java/com/source/animeh/configuration/filter/JwtAuthenticationFilter.java)

**Tính năng**:
1. **Đăng ký**:
    - `POST /auth/register`: Tạo tài khoản, lưu DB, tạo JWT + RefreshToken.
2. **Đăng nhập**:
    - `POST /auth/login-username` hoặc `POST /auth/login-email`: Xác thực bằng username/email và password. Phát hành JWT.
3. **Refresh Token**:
    - `POST /refresh-token`: Cấp lại AccessToken nếu refresh token còn giá trị.
4. **Đăng xuất**:
    - `POST /user-logout`: Hủy refresh token hiện tại.
5. **Quên mật khẩu - Reset mật khẩu**:
    - `POST /auth/forgot-password`: Gửi mail reset password (có token).
    - `POST /auth/reset-password`: Đổi mật khẩu mới khi có token reset.
6. **Kiểm tra token**:
    - `GET /check-token`: Kiểm tra AccessToken còn hiệu lực hay không.

---

### 5.2. Quản lý User

**Tệp chính liên quan**:
- **Service**:
    - [`com.source.animeh.service.account.UserService`](./src/main/java/com/source/animeh/service/account/UserService.java)
- **Controller**:
    - [`com.source.animeh.controller.account.UserController`](./src/main/java/com/source/animeh/controller/account/UserController.java)

**Tính năng**:
- **Xem danh sách User** (Admin): `GET /admin/users`
- **Xem chi tiết User** (Admin): `GET /admin/user/{id}`
- **Khóa tài khoản** (Admin): `PATCH /admin/user/{id}`
- **Xem thông tin cá nhân**: `GET /user/profile`
- **Cập nhật thông tin cá nhân** (User): `PUT /user`
- **Cập nhật mật khẩu** (User): `PUT /user/password`
- **Lấy thông tin đăng ký Moderator**: `GET /moderator-register/profile`

---

### 5.3. Quản lý Role

**Tệp chính liên quan**:
- **Service**:
    - [`com.source.animeh.service.account.RoleService`](./src/main/java/com/source/animeh/service/account/RoleService.java)
- **Controller**:
    - [`com.source.animeh.controller.account.RoleController`](./src/main/java/com/source/animeh/controller/account/RoleController.java)

**Tính năng**:
- **Lấy danh sách role**: `GET /admin/roles`
- **Tạo role**: `POST /admin/role`
- **Chỉnh sửa role**: `PUT /admin/role/{id}`
- **Xóa role**: `DELETE /admin/role/{id}`

---

### 5.4. Đăng ký Moderator

**Tệp chính liên quan**:
- **Service**:
    - [`com.source.animeh.service.account.ModeratorRequestService`](./src/main/java/com/source/animeh/service/account/ModeratorRequestService.java)
- **Controller**:
    - [`com.source.animeh.controller.account.ModeratorRequestController`](./src/main/java/com/source/animeh/controller/account/ModeratorRequestController.java)

**Tính năng**:
- **User hoặc Guest request làm Moderator**:
    - `POST /moderator-register/guest` (cho khách chưa có tài khoản)
    - `POST /moderator-register/user` (cho user đã có tài khoản)
- **Admin duyệt (approve/reject)**:
    - `POST /moderator-register/admin/{adminId}/approve`
    - `POST /moderator-register/admin/{adminId}/rejected`

---

### 5.5. Quản lý Anime (Phim)

**Tệp chính liên quan**:
- **Service**:
    - [`com.source.animeh.service.film_series.AnimeService`](./src/main/java/com/source/animeh/service/film_series/AnimeService.java)
- **Controller**:
    - [`com.source.animeh.controller.film_series.AnimeController`](./src/main/java/com/source/animeh/controller/film_series/AnimeController.java)
- **Specification** (lọc nâng cao):
    - [`com.source.animeh.specification.film_series.AnimeSpecification`](./src/main/java/com/source/animeh/specification/film_series/AnimeSpecification.java)

**Tính năng**:
- **Lấy top 4** phim có lượt xem cao: `GET /animes/top4`
- **Lọc anime cơ bản**: `GET /animes/filter-basic`  
  Tham số: `tagIds`, `releaseYear`, `minRating`, `nominated`.
- **Lọc anime nâng cao**: `POST /animes/filter-advanced`  
  Body: `AnimeFilterRequest` (`tagIds`, `releaseYear`, `minRating`, `nominated`).

---

### 5.6. Upload File (Avatar / Background)

**Tệp chính liên quan**:
- **Service**:
    - [`com.source.animeh.service.file.ImageStorageService`](./src/main/java/com/source/animeh/service/file/FileStorageService.java)
    - [`com.source.animeh.service.file.UserFileService`](./src/main/java/com/source/animeh/service/file/UserFileService.java)
- **Controller**:
    - [`com.source.animeh.controller.file.UserFileController`](./src/main/java/com/source/animeh/controller/file/UserFileController.java)

**Tính năng**:
- **Upload avatar** (User): `POST /user/upload/avatar`
- **Upload background** (User): `POST /user/upload/background`
- **Xem avatar** (User): `GET /user/avatar?size=...` (tiny, small, original)
- **Xem background** (User): `GET /user/background?size=...` (tiny, small, original)

Thư viện **Thumbnailator** được dùng để resize, tạo 3 phiên bản (tiny, small, original).

---

## 6. Cấu hình - Config

- **Database**:
    - Tùy theo môi trường, cấu hình trong `application.properties` hoặc `application-test.properties`.
    - Mặc định `application.properties` đang trỏ đến SQL Server.
- **Port**: Mặc định `server.port=8081`, context `server.servlet.context-path=/animeh/api`.
- **Mail**: Sử dụng SMTP Gmail. Điền `spring.mail.username` và `spring.mail.password`.
- **JWT**: Có **secret key** cứng trong [`com.source.animeh.utils.JwtUtils`](./src/main/java/com/source/animeh/utils/JwtUtils.java). (Nên chuyển vào cấu hình .properties thực tế).
- **Upload**: Tạm thời đường dẫn gốc là `D:/fileCode/animeh/image_test` (trong [`FileStorageService`](./src/main/java/com/source/animeh/service/file/FileStorageService.java)).

---

## 7. Cách chạy dự án

1. **Clone** dự án:
   ```bash
   git clone <https://gitlab.com/yunomix2834/animeh_be.git>
   cd animeh
   ```
2. **Cấu hình** trong `application.properties`:
    - Cập nhật đường dẫn SQL Server, user/password DB, mail, …
3. **Chạy bằng Maven**:
   ```bash
   mvn spring-boot:run
   ```
   Hoặc dùng IDE (IntelliJ, Eclipse, VSCode).
4. **Truy cập**:
    - Mặc định: [http://localhost:8081/animeh/api](http://localhost:8081/animeh/api)

---

## 8. Kiểm thử

- **Spring Boot Test** và **H2 Database**:
    - Các file test nằm chung với service (`AuthServiceTest`, …).
    - Cấu hình test: `application-test.properties`.
    - Chạy:
      ```bash
      mvn test
      ```
- **Postman / cURL**:
    - Có thể kiểm thử các endpoint như `/auth/register`, `/auth/login-username`, …

---

## 9. Đóng góp - Phát triển


---

## 10. Thông tin liên hệ

- **Tác giả**:
    - Nguyễn Đình An - Yunomi Xavia
- **Email**:
    - yunomix2834@gmail.com
- **Website**:
    - https://github.com/YunomiXavia

---

> **Ghi chú**: Đây là tài liệu mô tả trạng thái hiện tại của **Backend AnimeH**. Một số tính năng trong tương lai (comment, rating, streaming, …) có thể được bổ sung sau. Vui lòng xem chi tiết code để hiểu rõ hơn.
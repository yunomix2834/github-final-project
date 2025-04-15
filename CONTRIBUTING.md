# Hướng dẫn đóng góp cho AnimeH

Xin chào, cảm ơn bạn đã quan tâm tới dự án web xem phim AnimeH!  
Chúng mình luôn chào đón mọi đóng góp – từ sửa lỗi chính tả đến tính năng “xé gió”.

## 1. Tôi bắt đầu từ đâu?

1. **Fork & Clone**  
   ```bash
   git clone https://github.com/<your‑username>/animeh.git
   cd animeh
   git remote add upstream https://github.com/animeh-dev/animeh.git
   ```

2. **Tạo nhánh mới** (tên gợi ý: `feature/<mô-tả-ngắn>` hoặc `fix/<issue#>`):  
   ```bash
   git checkout -b feature/video-player-skip-op
   ```

3. **Cài đặt môi trường**  
   - Java 17+, Maven 3.9+  
   - Node 18+ (cho front‑end), pnpm/yarn tùy sở thích  
   - FFmpeg 6.x (nếu bạn định vọc transcoding)  
   - Tệp `.env.local` (xem `env.example`)

4. **Chạy dự án**  
   ```bash
   mvn spring-boot:run
   # hoặc
   ./mvnw spring-boot:run
   ```

## 2. Quy tắc commit

- Sử dụng [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/).  
  Ví dụ:  
  ```
  feat(player): add 10‑second rewind button  
  fix(api): handle NPE when ratingCount is null
  docs(readme): update project logo
  ```
- Một commit nên làm **một việc**; tránh “tất cả trong một”.

## 3. Pull Request (PR)

1. **Rebase** nhánh của bạn với `upstream/main` trước khi mở PR.  
2. PR phải đính kèm mô tả chi tiết, ảnh/GIF demo (nếu là UI).  
3. Bảo đảm `mvn test` và `npm test` (nếu có) **xanh**.  
4. Tự check‑list:
   - [ ] Code đã qua `mvn spotless:apply` hoặc `./gradlew ktlintFormat`?  
   - [ ] Không chứa thông tin nhạy cảm (.env, token)?  
   - [ ] Đã cập nhật tài liệu / migration script?  

> _Tip:_ Nhỏ nhưng chất lượng > To nhưng lỗi tè le.

## 4. Style Guide

| Layer | Tech | Quy ước ngắn gọn |
|-------|------|------------------|
| Back‑end | Spring Boot 3 | Package theo “feature‑slice”; tránh “god service”. |
| Front‑end | React + Vite | Sử dụng hooks, Tailwind, tránh className spaghetti. |
| DB | PostgreSQL | Snake_case; primary key UUID v4; soft delete dùng `is_deleted`. |

## 5. Vấn đề bảo mật

Phát hiện lỗ hổng? Gửi email **security@animeh.dev** – đừng tạo issue public.  
Bạn sẽ được ghi nhận (và có thể nhận GP ảo để mua “huy hiệu siêu cute” 🎁).

## 6. Liên hệ

- Discord: **https://discord.gg/animeh**  
- Email chung: **hello@animeh.dev**

Happy coding & _arigatou_! 

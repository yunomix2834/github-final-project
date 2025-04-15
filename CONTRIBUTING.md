# Hướng dẫn đóng góp cho AnimeH

> TL;DR: Fork → tạo branch → code + test → `./gradlew check` → Pull Request.

---

## 1. Yêu cầu hệ thống

| Thành phần | Phiên bản khuyến nghị |
|------------|----------------------|
| **JDK**    | 21 (Temurin/OpenJDK) |
| **Gradle** | Wrapper đi kèm repo  |
| **Node**   | ≥ 18 LTS (nếu sửa front‑end) |
| **Docker** | 24+ (chạy Postgres & MinIO) |

## 2. Luồng công việc Git

1. **Fork** repo (hoặc tạo branch nếu bạn đã có quyền push).  
2. `git switch -c feat/my-awesome-feature`  
3. Commit nhỏ, rõ ràng:  

   ```bash
   git commit -m "film: add filter by release year (#123)"
   ```

4. Rebase từ `main` trước khi mở PR:  

   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

5. Mở **Pull Request** và mô tả:
   * _What_: Thêm/tối ưu gì?  
   * _Why_: Vấn đề nào? Link issue.  
   * _How_: Ảnh GIF, log test, benchmark…

> **Tip:** PR < 400 LOC thường review nhanh hơn 🍵.

---

## 3. Quy ước mã nguồn

* **Java**: Google Java Style + Lombok.  
  * Không để `@Slf4j` trống – luôn log lỗi chi tiết.  
* **SQL**: JPA Criteria trước, native query chỉ khi cần.  
* **Shell/JS**: `#!/usr/bin/env` shebang, `set -euo pipefail`.  
* Tên hàm tiếng Anh, comment tiếng Việt/Anh tùy ngữ cảnh.

---

## 4. Kiểm thử & CI

```bash
./gradlew test              # JUnit + Testcontainers
./gradlew jacocoTestReport  # ≥ 80 % coverage
pnpm test --filter web      # Vitest front‑end
```

PR sẽ chạy GitHub Actions:

* **build** → **lint** → **unit‑test** → **docker‑build** (snapshot).  
* Không pass ✅ 100 % → không merge.

---

## 5. Issue tracker

* **Bug**: label `bug` + steps to reproduce + log/stacktrace.  
* **Feature**: label `enhancement`, mô tả rõ use‑case.  
* **Question**: label `question` hoặc hỏi trên Discord `#dev-help`.

---

## 6. Tài liệu bổ sung

* Kiến trúc tổng quan: `docs/architecture.md`  
* Swagger: `http://localhost:8080/swagger-ui.html`  
* Cơ chế transcoding FFmpeg: `docs/video-pipeline.md`

---

## 7. Bản quyền

Mã nguồn AnimeH phát hành dưới giấy phép **MIT**. Khi đóng góp, bạn đồng ý cấp phép thay đổi của mình theo MIT.

Happy hacking & have fun binge‑watching! 🚀
```

---

## simple-interest.sh
```bash
#!/usr/bin/env bash
# simple-interest.sh — Tính lãi đơn (Simple Interest)
# Công thức: SI = P * R * T / 100

set -euo pipefail

usage() {
  echo "Usage: $0 <principal> <annual_rate_%> <time_years>"
  echo "Example: $0 1000000 7.5 3"
  exit 1
}

[[ $# -ne 3 ]] && usage

P=$1   # Số tiền gốc
R=$2   # Lãi suất (%/năm)
T=$3   # Thời gian (năm)

# bc hỗ trợ số thực
SI=$(bc -l <<< "$P * $R * $T / 100")

printf "Số tiền gốc (P): %'d\n" "$P"
printf "Lãi suất (R):    %.2f%%/năm\n" "$R"
printf "Thời gian (T):   %.2f năm\n" "$T"
printf "=> Lãi đơn (SI): %'.2f\n" "$SI"
```

> **Cách dùng nhanh**  
> ```bash
> chmod +x simple-interest.sh
> ./simple-interest.sh 5000000 8 2.5
> ```

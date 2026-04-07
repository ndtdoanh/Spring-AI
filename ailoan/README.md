# AI Loan Admin (Spring Boot demo)

Demo **AI Admin Assistant** cho quản lý scheme/khoản vay: admin nhập lệnh tiếng Việt → Spring AI + Ollama gọi tool (truy vấn/cập nhật PostgreSQL).

## Công nghệ

- Java 21, Spring Boot 3.5, Spring Data JPA, Spring Security (mở toàn bộ cho demo), Lombok
- Spring AI 1.x, Ollama (model mặc định `qwen2.5:3b`, có thể đổi qua biến môi trường)

## Chạy nhanh với Docker Compose

1. **Khởi động Postgres + Ollama**

   ```bash
   docker compose up -d
   ```

2. **Tải model LLM vào Ollama** (một lần; có thể đổi model trong `application.yml` / env)

   ```bash
   docker exec -it $(docker ps -qf "ancestor=ollama/ollama") ollama pull qwen2.5:3b
   ```

   Model nhỏ ~4B thay thế cho mô tả “qwen 3.8b”; nếu máy bạn có `qwen2.5` hoặc `qwen3` phiên bản khác, chỉnh `OLLAMA_MODEL`.

3. **Chạy ứng dụng**

   ```bash
   ./mvnw spring-boot:run
   ```

   Hoặc cài Maven và chạy `mvn spring-boot:run` trong thư mục dự án.

   Ứng dụng: `http://localhost:8080`

### Biến môi trường (tùy chọn)

| Biến            | Mặc định                 | Mô tả                          |
|----------------|--------------------------|--------------------------------|
| `DB_HOST`      | `localhost`              | Host Postgres                  |
| `DB_USER`      | `postgres`               | User DB                        |
| `DB_PASSWORD`  | `demo123`                | Mật khẩu DB                    |
| `OLLAMA_BASE_URL` | `http://localhost:11434` | URL Ollama API              |
| `OLLAMA_MODEL` | `qwen2.5:3b`             | Tên model chat                 |

Khi chạy app trong Docker (không có trong file compose mẫu), đặt `DB_HOST=postgres`, `OLLAMA_BASE_URL=http://ollama:11434`.

## Dữ liệu mẫu

- 3 scheme **A, B, C** với config `maxAmount`, `interestRate`, `tenorMonths`, `serviceFee`.
- **20** khoản vay: 7 thuộc A, 7 thuộc B, 6 thuộc C.

## API

| Method | Đường dẫn | Mô tả |
|--------|------------|--------|
| POST | `/api/admin/ai/command` | Body JSON `{"command":"..."}`. Header: `X-Session-Id` (tuỳ chọn), `X-Admin-User` (tuỳ chọn). Response: `message`, `toolCalled`, `affectedCount` |
| GET | `/api/schemes` | Liệt kê scheme + config |
| GET | `/api/loans?scheme=A` | Khoản vay; bỏ query để lấy tất cả |
| GET | `/api/audit-log` | Lịch sử tool thực thi (audit) |
| GET | `/api/chat-history?sessionId=...` hoặc header `X-Session-Id` | Hội thoại theo session |

## Câu lệnh demo (Postman / curl)

```bash
curl -s -X POST http://localhost:8080/api/admin/ai/command \
  -H "Content-Type: application/json" \
  -H "X-Session-Id: demo-session-1" \
  -H "X-Admin-User: admin1" \
  -d '{"command":"Tìm tất cả khoản vay scheme A"}'
```

Các ý khác có thể thử:

- `Cập nhật interestRate = 8.5 cho scheme B` (AI nên gọi `listAllSchemes` rồi `updateSchemeConfig`).
- `Cập nhật số tiền = 100000000 cho scheme A` (AI sẽ cập nhật `amount` cho tất cả loan thuộc scheme A).
- `Liệt kê tất cả scheme đang có`
- `Copy config scheme A sang scheme C`
- `Scheme nào đang có interestRate là 8.5?`
- `Nhân đôi maxAmount của scheme B`

## Cấu trúc chính

- `controller/` — REST
- `ai/` — `LoanTools` (@Tool), `AiOrchestrator`, `SystemPromptConfig`, `AiConfig` (ChatClient + tools)
- `service/`, `repository/`, `entity/`

## Ghi chú

- Tool **chỉ ghi audit khi thực thi thành công** (đúng yêu cầu). Lỗi (ví dụ scheme không tồn tại) trả JSON lỗi trong tool, không tạo audit thành công.
- `affectedCount` trong response là **số liên quan đến lần gọi tool cuối cùng** (ví dụ số khoản vay khi `findLoansByScheme`, hoặc `1` khi cập nhật scheme).
- CORS bật cho React tại `http://localhost:3000`.

## Frontend React (Demo UI)

Thư mục frontend nằm ở `frontend/` (Vite + React 19) để bạn bấm lệnh và xem kết quả tool, loans, audit log, chat history.

### Chạy

```bash
cd frontend
npm install
npm run dev
```

Giao diện: `http://localhost:3000`

# CRUD Shop

## Cấu hình MySQL

Cần JDK 21, Maven, MySQL và Tomcat hỗ trợ Jakarta Servlet 6 (ví dụ Tomcat 10.1).
Tạo cơ sở dữ liệu trước khi chạy ứng dụng:

```sql
CREATE DATABASE IF NOT EXISTS ServletCRUDMVC
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Ứng dụng mặc định kết nối `jdbc:mysql://localhost:3306/ServletCRUDMVC` bằng
người dùng `root` với mật khẩu rỗng. Sao chép `.env.example` thành `.env` nếu
chưa có, rồi điền tài khoản MySQL, email SMTP và thư mục tải ảnh. Tệp `.env`
được Git bỏ qua; không đưa mật khẩu thật vào `.env.example`.

Ứng dụng đọc `.env` từ thư mục làm việc của tiến trình Tomcat, không nhất thiết
là thư mục dự án. Khi chạy Tomcat từ IDE hoặc dưới dạng dịch vụ, nên đặt biến
`APP_ENV_FILE` thành đường dẫn tuyệt đối tới `.env` trước khi khởi động Tomcat.
Ví dụ PowerShell:

```powershell
$env:APP_ENV_FILE = 'F:\duong-dan\du-an\.env'
```

Các biến môi trường thực tế như `DB_URL`, `DB_USER`, `DB_PASSWORD`,
`MAIL_USERNAME` và `MAIL_PASSWORD` có độ ưu tiên cao hơn giá trị trong `.env`.
Tệp dùng định dạng Java properties `KEY=value`, không bao giá trị trong dấu
nháy; dùng `/` trong đường dẫn Windows và `\\` để biểu diễn dấu gạch chéo ngược.
Khởi động lại Tomcat sau khi thay đổi cấu hình.

Tài khoản MySQL cần có quyền tạo và sửa bảng trên cơ sở dữ liệu. Hibernate
được cấu hình `update` để tạo/cập nhật các bảng khi ứng dụng khởi động; tùy chọn
này không tạo cơ sở dữ liệu và không chuyển dữ liệu từ SQL Server sang MySQL.
Nếu muốn giữ dữ liệu cũ, cần xuất và chuyển đổi dữ liệu riêng.

Chạy `mvn package` và triển khai tệp WAR trong `target` lên Tomcat.

## Chạy từ PowerShell

Script sau build dự án, truyền đường dẫn `.env` tuyệt đối cho Tomcat và triển khai
ứng dụng với context `/CRUD`. Cấu hình, log và bản triển khai Tomcat được tạo trong
`.runtime/tomcat`, tách khỏi thư mục build `target` để `mvn clean package` không
xóa các tệp JAR mà Tomcat đang sử dụng trên Windows.
Script cần JDK 21, Maven trong PATH và thư mục cài Tomcat 10.1:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\run.ps1 `
  -TomcatHome 'F:\Downloads\Apache Tomcat\apache-tomcat-10.1.61' `
  -JavaHome 'C:\Program Files\Eclipse Adoptium\jdk-21.0.12.101-hotspot' `
  -Port 8081
```

Thay đường dẫn bằng thư mục cài đặt trên máy. Mở `http://localhost:8081/CRUD/`.
Giữ terminal đang chạy; nhấn `Ctrl+C` để dừng. Nếu không truyền `-Port`, script
dùng cổng `8080`; cổng phải chưa được tiến trình khác sử dụng.

Để cập nhật ứng dụng đang chạy, nhấn `Ctrl+C` trong terminal chạy Tomcat, rồi chạy
lại script. Script sẽ build và sao chép WAR mới vào `.runtime/tomcat/webapps`.
Chạy riêng `mvn clean package` chỉ tạo bản build mới, chưa cập nhật bản đang chạy.

Nếu triển khai thủ công tệp `CRUD-1.1.0.war`, context mặc định là `/CRUD-1.1.0`.
Đặt `APP_ENV_FILE` trong môi trường của tiến trình **khởi động Tomcat** rồi khởi
động lại server; đặt biến ở một terminal khác không cập nhật Tomcat đang chạy.
Lỗi `Access denied ... (using password: NO)` khi `.env` đã có mật khẩu thường
cho thấy Tomcat chưa đọc đúng tệp cấu hình. Log ứng dụng sẽ ghi đường dẫn tệp
đã nạp, hoặc thông báo không tìm thấy `.env`.

## Chạy Tomcat từ IDE ở cổng 8080

IDE có thể khởi động Java trực tiếp nên không đọc `setenv.bat` của Tomcat.
Ứng dụng cũng hỗ trợ VM argument `-DAPP_ENV_FILE=F:/duong-dan/du-an/.env`
hoặc một cấu hình JNDI riêng cho ứng dụng.

Khi triển khai `CRUD-1.1.0.war`, tạo tệp
`conf/Catalina/localhost/CRUD-1.1.0.xml` trong thư mục Tomcat:

```xml
<Context>
  <Environment name="APP_ENV_FILE"
               value="F:/duong-dan/du-an/.env"
               type="java.lang.String" override="false" />
</Context>
```

Thay `value` bằng đường dẫn `.env` thực tế, rồi triển khai WAR mới và khởi động
lại ứng dụng. Truy cập `http://localhost:8080/CRUD-1.1.0/`. Cấu hình này giữ nguyên
qua các lần triển khai lại WAR và không sao chép mật khẩu vào gói ứng dụng.
Thứ tự chọn đường dẫn là biến môi trường `APP_ENV_FILE`, VM argument, JNDI,
cuối cùng là `.env` trong thư mục làm việc của Tomcat.

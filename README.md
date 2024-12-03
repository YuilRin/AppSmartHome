# AppSmartHome

Ứng dụng **AppSmartHome** là hệ thống quản lý và điều khiển các thiết bị trong nhà thông minh, cho phép điều khiển đèn và quạt trong phòng khách. Ứng dụng sử dụng **Firebase Realtime Database** để đồng bộ trạng thái thiết bị và **WorkManager** để lập lịch tự động.

---

## 📋 Tính năng

1. **Điều khiển thiết bị từ xa:**
   - Bật/tắt đèn và quạt phòng khách qua giao diện ứng dụng.
   - Đồng bộ trạng thái thời gian thực với Firebase.

2. **Lập lịch bật/tắt thiết bị:**
   - Chọn thời gian cụ thể để bật/tắt đèn hoặc quạt.
   - Sử dụng WorkManager để quản lý và thực thi các lịch trình.

3. **Điều hướng mượt mà:**
   - Điều hướng dễ dàng giữa các Fragment.
   - Hỗ trợ quay lại trang chính với nút "Back".

---

## 🛠️ Công nghệ sử dụng

### Ngôn ngữ
- **Java** (Android)

### Công cụ & Thư viện
- **Firebase Realtime Database**: Quản lý trạng thái thiết bị.
- **WorkManager**: Quản lý lịch trình bật/tắt thiết bị.
- **Navigation Component**: Điều hướng giữa các Fragment.
- **SwitchCompat**: Hiển thị và tùy chỉnh trạng thái thiết bị.

### Phần cứng
- **ESP8266**: Xử lý tín hiệu giữa ứng dụng và thiết bị IoT.

---

## 🚀 Hướng dẫn cài đặt

### Yêu cầu
- Android Studio (phiên bản 2022 hoặc mới hơn).
- ESP8266 đã được lập trình và kết nối mạng.
- Firebase Realtime Database được cấu hình.

### Cài đặt
1. **Clone repository:**
   ```bash
   git clone https://github.com/your-repo/AppSmartHome.git
2. **Mở project trong Android Studio.**
3. **Thêm tệp cấu hình Firebase (google-services.json) vào thư mục app/.**
4. **Chạy ứng dụng trên thiết bị thật hoặc máy ảo Android.**

### 💻 Cách sử dụng
**Điều khiển thiết bị**
- Bật/tắt đèn và quạt qua Switch trên giao diện ứng dụng.
- Trạng thái sẽ được đồng bộ ngay lập tức với Firebase.
**Lập lịch bật/tắt**
- Nhấn Chọn thời gian để mở hộp thoại cài đặt giờ và phút.
- Xác nhận để lưu lịch trình.
**Quay lại trang chính**
- Nhấn nút Back trên giao diện để trở về trang chính.

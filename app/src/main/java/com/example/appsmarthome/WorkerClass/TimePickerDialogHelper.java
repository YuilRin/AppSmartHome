package com.example.appsmarthome.WorkerClass;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;

public class TimePickerDialogHelper {

    public static void showTimePickerDialog(@NonNull Context context, @NonNull OnTimeSetListener listener) {
        // Khởi tạo các NumberPickers cho giờ và phút
        final NumberPicker hourPicker = new NumberPicker(context);
        final NumberPicker minutePicker = new NumberPicker(context);

        // Cấu hình NumberPicker cho giờ
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);  // Giới hạn là 24 giờ
        hourPicker.setValue(0);  // Giá trị mặc định

        // Cấu hình NumberPicker cho phút
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);  // Giới hạn là 60 phút
        minutePicker.setValue(0);  // Giá trị mặc định

        // Tạo một Layout để chứa các NumberPickers
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(hourPicker);
        layout.addView(minutePicker);

        // Tạo AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Nhập giờ và phút")
                .setView(layout)
                .setPositiveButton("OK", (dialog, which) -> {
                    int hours = hourPicker.getValue();
                    int minutes = minutePicker.getValue();
                    // Gọi listener trả về giá trị giờ và phút
                    listener.onTimeSet(hours, minutes);
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Interface để trả về kết quả khi người dùng chọn giờ và phút
    public interface OnTimeSetListener {
        void onTimeSet(int hours, int minutes);
    }
}

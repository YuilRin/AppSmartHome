package com.example.appsmarthome.WorkerClass;
import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyWorker extends Worker {

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Thực hiện cập nhật giá trị trong Firebase Realtime Database
        try {
            DatabaseReference led_LivingRoomReference = FirebaseDatabase.getInstance().getReference("ESP8266/LED/Living_Room");
            led_LivingRoomReference.setValue(true) // Đặt giá trị là true
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Firebase", "Living Room LED updated successfully");
                        } else {
                            Log.e("Firebase", "Failed to update LED state", task.getException());
                        }
                    });
            return Result.success();  // Trả về kết quả thành công
        } catch (Exception e) {
            // Nếu có lỗi, trả về kết quả thất bại
            Log.e("MyWorker", "Error updating Firebase", e);
            return Result.failure();
        }
    }
}


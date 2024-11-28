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
        // Lấy địa chỉ và giá trị boolean từ inputData
        String databasePath = getInputData().getString("database_path");
        boolean valueToUpdate = getInputData().getBoolean("value_to_update", true); // Mặc định là true nếu không có giá trị

        if (databasePath == null) {
            Log.e("MyWorker", "Database path is null");
            return Result.failure();
        }

        // Thực hiện cập nhật giá trị trong Firebase Realtime Database
        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(databasePath);
            reference.setValue(valueToUpdate) // Đặt giá trị từ inputData
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Firebase", "Value updated successfully at: " + databasePath);
                        } else {
                            Log.e("Firebase", "Failed to update value", task.getException());
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

package com.example.appsmarthome.ui.Fragment;

import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appsmarthome.R;
import com.example.appsmarthome.WorkerClass.MyWorker;
import com.example.appsmarthome.WorkerClass.TimePickerDialogHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;


public class BedRoomFragment extends Fragment {

    private DatabaseReference ledOnboardReference,motor_LivingRoomReference;
    private SwitchCompat switchLed,switchMotor_LR;
    private boolean isUpdating = false;
    private Button SetLedOn,SetLedOff,SetFanOn,SetFanOff;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bed_room, container, false);

        if (getActivity() != null) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(false); // Ẩn nút back
                actionBar.setHomeButtonEnabled(false); // Vô hiệu hóa chức năng nút back
            }
        }
        Button back=view.findViewById(R.id.buttonBack);
        back.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.nav_home);
        });


        switchLed = view.findViewById(R.id.SwitchLed2);
        switchMotor_LR= view.findViewById(R.id.SwitchFan2);
        SetLedOn = view.findViewById(R.id.btn_BedLED_On);
        SetLedOff = view.findViewById(R.id.btn_BedLED_Off);
        SetFanOn = view.findViewById(R.id.btn_BedFAN_On);
        SetFanOff = view.findViewById(R.id.btn_BedFAN_Off);

        // Tham chiếu đến LED/OnBoard trong Firebase Realtime Database
        ledOnboardReference = FirebaseDatabase.getInstance().getReference("ESP8266/LED/Bed_Room");
        motor_LivingRoomReference = FirebaseDatabase.getInstance().getReference("ESP8266/SYSTEM/Motor_Bed_Room");

        // Lắng nghe thay đổi từ Firebase để cập nhật Switch
        ledOnboardReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean switchState = snapshot.getValue(Boolean.class);
                if (switchState != null) {
                    Log.d("FirebaseData", "OnBoard value from Firebase: " + switchState);
                    isUpdating = true; // Đặt cờ để biết rằng đây là thay đổi từ Firebase
                    switchLed.setChecked(switchState); // Cập nhật trạng thái của Switch
                    isUpdating = false; // Đặt lại cờ
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗiLog.e("FirebaseData", "Failed to read value.", error.toException());
            }
        });

        // Cập nhật trạng thái Switch lên Firebase khi người dùng thay đổi
        switchLed.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isUpdating) { // Chỉ cập nhật Firebase nếu thay đổi từ người dùng
                ledOnboardReference.setValue(isChecked);
            }
        });

        //--------------------------------Motor LivingRoom--------------------------------
        // Lắng nghe thay đổi từ Firebase để cập nhật Switch
        motor_LivingRoomReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean switchState = snapshot.getValue(Boolean.class);
                if (switchState != null) {
                    Log.d("FirebaseData", "OnBoard value from Firebase: " + switchState);
                    isUpdating = true; // Đặt cờ để biết rằng đây là thay đổi từ Firebase
                    switchMotor_LR.setChecked(switchState); // Cập nhật trạng thái của Switch
                    isUpdating = false; // Đặt lại cờ
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗiLog.e("FirebaseData", "Failed to read value.", error.toException());
            }
        });

        // Cập nhật trạng thái Switch lên Firebase khi người dùng thay đổi
        switchMotor_LR.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isUpdating) { // Chỉ cập nhật Firebase nếu thay đổi từ người dùng
                motor_LivingRoomReference.setValue(isChecked);
            }
        });




        SetLedOn.setOnClickListener(v -> {
            Data inputData = new Data.Builder()
                    .putString("database_path", "ESP8266/LED/Bed_Room") // Địa chỉ Firebase
                    .build();
            // Gọi dialog từ helper class
            TimePickerDialogHelper.showTimePickerDialog(getContext(), (hours, minutes) -> {
                // Xử lý kết quả giờ và phút
                long initialDelay = calculateInitialDelay(hours, minutes);
                Log.d("Time Input", "Hours: " + hours + ", Minutes: " + minutes + ", Initial Delay: " + initialDelay);

                int totalMinutes = (hours * 60) + minutes;
                String message = "Tổng thời gian chờ: " + totalMinutes + " phút";
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                // Tiến hành enqueue work request với thời gian delay tính được
                OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                        .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                        .setInputData(inputData)
                        .build();

                WorkManager.getInstance(requireActivity()).enqueueUniqueWork(
                        "LedBedWork", // Tên công việc
                        ExistingWorkPolicy.REPLACE, // Thay thế công việc cũ nếu đã có
                        workRequest);
            });
        });

        SetFanOn.setOnClickListener(v -> {
            Data inputData = new Data.Builder()
                    .putString("database_path", "ESP8266/SYSTEM/Motor_Bed_Room") // Địa chỉ Firebase
                    .build();
            // Gọi dialog từ helper class
            TimePickerDialogHelper.showTimePickerDialog(getContext(), (hours, minutes) -> {
                // Xử lý kết quả giờ và phút
                long initialDelay = calculateInitialDelay(hours, minutes);
                Log.d("Time Input", "Hours: " + hours + ", Minutes: " + minutes + ", Initial Delay: " + initialDelay);
                int totalMinutes = (hours * 60) + minutes;
                String message = "Tổng thời gian chờ: " + totalMinutes + " phút";
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                // Tiến hành enqueue work request với thời gian delay tính được
                OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                        .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                        .setInputData(inputData)
                        .build();

                WorkManager.getInstance(requireActivity()).enqueueUniqueWork(
                        "FanBedWork", // Tên công việc
                        ExistingWorkPolicy.REPLACE, // Thay thế công việc cũ nếu đã có
                        workRequest);
            });
        });
        SetLedOff.setOnClickListener(v -> {
            Data inputData = new Data.Builder()
                    .putString("database_path", "ESP8266/LED/Bed_Room") // Địa chỉ Firebase
                    .putBoolean("value_to_update", false)
                    .build();
            // Gọi dialog từ helper class
            TimePickerDialogHelper.showTimePickerDialog(getContext(), (hours, minutes) -> {
                // Xử lý kết quả giờ và phút
                long initialDelay = calculateInitialDelay(hours, minutes);
                Log.d("Time Input", "Hours: " + hours + ", Minutes: " + minutes + ", Initial Delay: " + initialDelay);

                // Tiến hành enqueue work request với thời gian delay tính được
                OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                        .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                        .setInputData(inputData)
                        .build();

                WorkManager.getInstance(requireActivity()).enqueueUniqueWork(
                        "LedLivingWork", // Tên công việc
                        ExistingWorkPolicy.REPLACE, // Thay thế công việc cũ nếu đã có
                        workRequest);
            });
        });

        SetFanOff.setOnClickListener(v -> {
            Data inputData = new Data.Builder()
                    .putString("database_path", "ESP8266/SYSTEM/Motor_Bed_Room") // Địa chỉ Firebase
                    .putBoolean("value_to_update", false)
                    .build();
            // Gọi dialog từ helper class
            TimePickerDialogHelper.showTimePickerDialog(getContext(), (hours, minutes) -> {
                // Xử lý kết quả giờ và phút
                long initialDelay = calculateInitialDelay(hours, minutes);
                Log.d("Time Input", "Hours: " + hours + ", Minutes: " + minutes + ", Initial Delay: " + initialDelay);

                // Tiến hành enqueue work request với thời gian delay tính được
                OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                        .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                        .setInputData(inputData)
                        .build();

                WorkManager.getInstance(requireActivity()).enqueueUniqueWork(
                        "FanLivingWork", // Tên công việc
                        ExistingWorkPolicy.REPLACE, // Thay thế công việc cũ nếu đã có
                        workRequest);
            });
        });


        return view;

    }
    private long calculateInitialDelay(int hours, int minutes) {
        Calendar currentDate = Calendar.getInstance();
        Calendar targetDate = Calendar.getInstance();

        targetDate.set(Calendar.HOUR_OF_DAY, hours);
        targetDate.set(Calendar.MINUTE, minutes);
        targetDate.set(Calendar.SECOND, 0);  // Đặt giây bằng 0

        if (currentDate.after(targetDate)) {
            targetDate.add(Calendar.DATE, 1);  // Chuyển sang ngày hôm sau
        }

        return targetDate.getTimeInMillis() - currentDate.getTimeInMillis();
    }

}

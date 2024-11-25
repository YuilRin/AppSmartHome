package com.example.appsmarthome.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.appsmarthome.R;
import com.example.appsmarthome.databinding.FragmentHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    // Map lưu các Switch và DatabaseReference tương ứng
    private final Map<SwitchCompat, DatabaseReference> switchMap = new HashMap<>();
    private final Map<SwitchCompat, ValueEventListener> valueEventListenerMap = new HashMap<>();

    private boolean isUpdating = false;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Ánh xạ các Switch và View
        SwitchCompat switchLed_LR = root.findViewById(R.id.SwitchLedLivingRoom);
        SwitchCompat switchLed_BR = root.findViewById(R.id.SwitchLambBedRoom);
        SwitchCompat switchLed_Stair = root.findViewById(R.id.SwitchLedStair);
        SwitchCompat switchMotor_LR = root.findViewById(R.id.SwitchFanLivingRoom);
        SwitchCompat switchMotor_BR = root.findViewById(R.id.SwitchFanBedRoom);
        SwitchCompat switchDoor = root.findViewById(R.id.SwitchDoor);

        // Khởi tạo Firebase DatabaseReferences
        DatabaseReference led_LivingRoomReference = FirebaseDatabase.getInstance().getReference("ESP8266/LED/Living_Room");
        DatabaseReference led_BedRoomReference = FirebaseDatabase.getInstance().getReference("ESP8266/LED/Bed_Room");
        DatabaseReference led_StairReference = FirebaseDatabase.getInstance().getReference("ESP8266/LED/Stair_Light");
        DatabaseReference motor_LivingRoomReference = FirebaseDatabase.getInstance().getReference("ESP8266/SYSTEM/Motor_Living_Room");
        DatabaseReference motor_BedRoomReference = FirebaseDatabase.getInstance().getReference("ESP8266/SYSTEM/Motor_Bed_Room");
        DatabaseReference doorReference = FirebaseDatabase.getInstance().getReference("ESP32/Door");

        // Lưu vào map để xử lý chung
        switchMap.put(switchLed_LR, led_LivingRoomReference);
        switchMap.put(switchLed_BR, led_BedRoomReference);
        switchMap.put(switchLed_Stair, led_StairReference);
        switchMap.put(switchMotor_LR, motor_LivingRoomReference);
        switchMap.put(switchMotor_BR, motor_BedRoomReference);
        switchMap.put(switchDoor, doorReference);

        // Thiết lập listener cho tất cả các switch
        setupSwitchListeners();

        View LivingRoom = root.findViewById(R.id.LivingRoom);
        View BedRoom = root.findViewById(R.id.BedRoom);
        View Door = root.findViewById(R.id.Door);
        View Stair = root.findViewById(R.id.Stair);

        ///////////////////////////////////////////
        // Thiết lập onClick cho từng thẻ thiết bị để mở DeviceDetailFragment
        LivingRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = NavHostFragment.findNavController(HomeFragment.this);

                // Điều hướng đến LivingFragment và loại bỏ HomeFragment khỏi stack
                navController.navigate(R.id.livingFragment, null,
                        new NavOptions.Builder().setPopUpTo(R.id.nav_home, true).build());
            }
        });
        BedRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = NavHostFragment.findNavController(HomeFragment.this);

                // Điều hướng đến LivingFragment và loại bỏ HomeFragment khỏi stack
                navController.navigate(R.id.bedRoomFragment, null,
                        new NavOptions.Builder().setPopUpTo(R.id.nav_home, true).build());
            }
        });
        Door.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = NavHostFragment.findNavController(HomeFragment.this);

                // Điều hướng đến LivingFragment và loại bỏ HomeFragment khỏi stack
                navController.navigate(R.id.doorFragment, null,
                        new NavOptions.Builder().setPopUpTo(R.id.nav_home, true).build());
            }
        });
        Stair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = NavHostFragment.findNavController(HomeFragment.this);

                // Điều hướng đến LivingFragment và loại bỏ HomeFragment khỏi stack
                navController.navigate(R.id.stairFragment, null,
                        new NavOptions.Builder().setPopUpTo(R.id.nav_home, true).build());
            }
        });



        /*BedRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = NavHostFragment.findNavController(HomeFragment.this);
                navController.navigate(R.id.bedRoomFragment);
            }
        });*/

        /*Stair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở DeviceDetailFragment với NavController và truyền dữ liệu
                openDeviceDetailFragment("Thiết bị 1", "Mô tả chi tiết của thiết bị 1");
            }
        });  */

        return root;
    }
    private void setupSwitchListeners() {
        // Thêm listener mới
        for (Map.Entry<SwitchCompat, DatabaseReference> entry : switchMap.entrySet()) {
            SwitchCompat switchCompat = entry.getKey();
            DatabaseReference reference = entry.getValue();

            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Boolean switchState = snapshot.getValue(Boolean.class);
                    if (switchState != null) {
                        isUpdating = true; // Để tránh vòng lặp vô hạn
                        switchCompat.setChecked(switchState);
                        isUpdating = false;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseError", "Failed to read value.", error.toException());
                }
            };

            // Lưu ValueEventListener để có thể xóa sau này
            valueEventListenerMap.put(switchCompat, valueEventListener);

            // Lắng nghe dữ liệu từ Firebase
            reference.addValueEventListener(valueEventListener);

            // Lắng nghe thay đổi từ người dùng
            switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!isUpdating) {
                    reference.setValue(isChecked); // Cập nhật Firebase khi có thay đổi từ người dùng
                }
            });
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

    }

    private void openDeviceDetailFragment(String deviceName, String deviceDescription) {
        NavController navController = NavHostFragment.findNavController(this);
        Bundle bundle = new Bundle();
        bundle.putString("device_name", deviceName);
        bundle.putString("device_description", deviceDescription);

        // Điều hướng đến DeviceDetailFragment với NavController
        navController.navigate(R.id.deviceDetailFragment, bundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Lấy lại trạng thái từ Firebase khi quay lại fragment
        for (Map.Entry<SwitchCompat, DatabaseReference> entry : switchMap.entrySet()) {
            SwitchCompat switchCompat = entry.getKey();
            DatabaseReference reference = entry.getValue();

            // Kiểm tra lại giá trị từ Firebase và cập nhật lại trạng thái của switch
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Boolean switchState = snapshot.getValue(Boolean.class);

                        switchCompat.setChecked(switchState);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseError", "Failed to read value.", error.toException());
                }
            });
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        // Hủy bỏ tất cả các ValueEventListener khi fragment bị tạm dừng để tránh rò rỉ bộ nhớ
        for (Map.Entry<SwitchCompat, ValueEventListener> entry : valueEventListenerMap.entrySet()) {
            SwitchCompat switchCompat = entry.getKey();
            ValueEventListener valueEventListener = entry.getValue();
            DatabaseReference reference = switchMap.get(switchCompat);
            reference.removeEventListener(valueEventListener);
        }
        valueEventListenerMap.clear();

    }

}

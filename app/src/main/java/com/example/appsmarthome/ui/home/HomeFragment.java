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
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.appsmarthome.R;
import com.example.appsmarthome.databinding.FragmentHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private DatabaseReference led_LivingRoomReference;
    private DatabaseReference led_BedRoomReference;
    private DatabaseReference led_StairReference;
    private DatabaseReference motor_LivingRoomReference;
    private DatabaseReference motor_BedRoomReference;

    private DatabaseReference DoorReference;

    private SwitchCompat switchLed_LR,switchLed_BR,switchLed_Stair,switchMotor_LR,switchMotor_BR,switchDoor;
    private boolean isUpdating = false;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        View LivingRoom = root.findViewById(R.id.LivingRoom);
        View BedRoom = root.findViewById(R.id.BedRoom);
        View Door = root.findViewById(R.id.Door);
        View Stair = root.findViewById(R.id.Stair);

        switchLed_LR = LivingRoom.findViewById(R.id.SwitchLedLivingRoom);
        switchLed_BR = BedRoom.findViewById(R.id.SwitchLambBedRoom);
        switchLed_Stair = Stair.findViewById(R.id.SwitchLedStair);
        switchMotor_LR = LivingRoom.findViewById(R.id.SwitchFanLivingRoom);
        switchMotor_BR = BedRoom.findViewById(R.id.SwitchFanBedRoom);

        switchDoor = Door.findViewById(R.id.SwitchDoor);

        // Tham chiếu đến LED/OnBoard trong Firebase Realtime Database
        led_LivingRoomReference = FirebaseDatabase.getInstance().getReference("ESP8266/LED/Living_Room");
        led_BedRoomReference = FirebaseDatabase.getInstance().getReference("ESP8266/LED/Bed_Room");
        led_StairReference = FirebaseDatabase.getInstance().getReference("ESP8266/LED/Stair_Light");
        motor_LivingRoomReference = FirebaseDatabase.getInstance().getReference("ESP8266/SYSTEM/Motor_Living_Room");
        motor_BedRoomReference = FirebaseDatabase.getInstance().getReference("ESP8266/SYSTEM/Motor_Bed_Room");

        DoorReference = FirebaseDatabase.getInstance().getReference("ESP32/Door");
        //--------------------------------LedLivingRoom--------------------------------
        // Lắng nghe thay đổi từ Firebase để cập nhật Switch
        led_LivingRoomReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean switchState = snapshot.getValue(Boolean.class);
                if (switchState != null) {
                    Log.d("FirebaseData", "OnBoard value from Firebase: " + switchState);
                    isUpdating = true; // Đặt cờ để biết rằng đây là thay đổi từ Firebase
                    switchLed_LR.setChecked(switchState); // Cập nhật trạng thái của Switch
                    isUpdating = false; // Đặt lại cờ
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗiLog.e("FirebaseData", "Failed to read value.", error.toException());
            }
        });

        // Cập nhật trạng thái Switch lên Firebase khi người dùng thay đổi
        switchLed_LR.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isUpdating) { // Chỉ cập nhật Firebase nếu thay đổi từ người dùng
                led_LivingRoomReference.setValue(isChecked);
            }
        });


        //--------------------------------LedBedRoom--------------------------------
        // Lắng nghe thay đổi từ Firebase để cập nhật Switch
        led_BedRoomReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean switchState = snapshot.getValue(Boolean.class);
                if (switchState != null) {
                    Log.d("FirebaseData", "OnBoard value from Firebase: " + switchState);
                    isUpdating = true; // Đặt cờ để biết rằng đây là thay đổi từ Firebase
                    switchLed_BR.setChecked(switchState); // Cập nhật trạng thái của Switch
                    isUpdating = false; // Đặt lại cờ
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗiLog.e("FirebaseData", "Failed to read value.", error.toException());
            }
        });

        // Cập nhật trạng thái Switch lên Firebase khi người dùng thay đổi
        switchLed_BR.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isUpdating) { // Chỉ cập nhật Firebase nếu thay đổi từ người dùng
                led_BedRoomReference.setValue(isChecked);
            }
        });

        //--------------------------------Led Stair--------------------------------
        // Lắng nghe thay đổi từ Firebase để cập nhật Switch
        led_StairReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean switchState = snapshot.getValue(Boolean.class);
                if (switchState != null) {
                    Log.d("FirebaseData", "OnBoard value from Firebase: " + switchState);
                    isUpdating = true; // Đặt cờ để biết rằng đây là thay đổi từ Firebase
                    switchLed_Stair.setChecked(switchState); // Cập nhật trạng thái của Switch
                    isUpdating = false; // Đặt lại cờ
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗiLog.e("FirebaseData", "Failed to read value.", error.toException());
            }
        });

        // Cập nhật trạng thái Switch lên Firebase khi người dùng thay đổi
        switchLed_Stair.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isUpdating) { // Chỉ cập nhật Firebase nếu thay đổi từ người dùng
                led_StairReference.setValue(isChecked);
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

        //--------------------------------Motor BedRoom--------------------------------
        // Lắng nghe thay đổi từ Firebase để cập nhật Switch
        motor_BedRoomReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean switchState = snapshot.getValue(Boolean.class);
                if (switchState != null) {
                    Log.d("FirebaseData", "OnBoard value from Firebase: " + switchState);
                    isUpdating = true; // Đặt cờ để biết rằng đây là thay đổi từ Firebase
                    switchMotor_BR.setChecked(switchState); // Cập nhật trạng thái của Switch
                    isUpdating = false; // Đặt lại cờ
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗiLog.e("FirebaseData", "Failed to read value.", error.toException());
            }
        });

        // Cập nhật trạng thái Switch lên Firebase khi người dùng thay đổi
        switchMotor_BR.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isUpdating) { // Chỉ cập nhật Firebase nếu thay đổi từ người dùng
                motor_BedRoomReference.setValue(isChecked);
            }
        });
        //--------------------------------Door--------------------------------
        // Lắng nghe thay đổi từ Firebase để cập nhật Switch
        DoorReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean switchState = snapshot.getValue(Boolean.class);
                if (switchState != null) {
                    Log.d("FirebaseData", "OnBoard value from Firebase: " + switchState);
                    isUpdating = true; // Đặt cờ để biết rằng đây là thay đổi từ Firebase
                    switchDoor.setChecked(switchState); // Cập nhật trạng thái của Switch
                    isUpdating = false; // Đặt lại cờ
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗiLog.e("FirebaseData", "Failed to read value.", error.toException());
            }
        });

        // Cập nhật trạng thái Switch lên Firebase khi người dùng thay đổi
        switchDoor.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isUpdating) { // Chỉ cập nhật Firebase nếu thay đổi từ người dùng
                DoorReference.setValue(isChecked);
            }
        });






        ////////////////////////////////////////////////////////////////////////////////////

        // Thiết lập onClick cho từng thẻ thiết bị để mở DeviceDetailFragment
        LivingRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = NavHostFragment.findNavController(HomeFragment.this);

                navController.navigate(R.id.livingFragment);
            }
        });


        BedRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeviceDetailFragment("LED", "Thiết bị LED với chức năng OnBoard.");
            }
        });

        Stair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở DeviceDetailFragment với NavController và truyền dữ liệu
                openDeviceDetailFragment("Thiết bị 1", "Mô tả chi tiết của thiết bị 1");
            }
        });


        return root;
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

package com.example.appsmarthome.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
    private DatabaseReference ledOnboardReference;
    private Switch switchLed;
    private boolean isUpdating = false;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        View deviceCard1 = root.findViewById(R.id.deviceCard1);
        View deviceCard2 = root.findViewById(R.id.deviceCard2);
        View deviceCard3 = root.findViewById(R.id.deviceCard3);
        View deviceCard4 = root.findViewById(R.id.deviceCard4);
        TextView tvName= deviceCard1.findViewById(R.id.NameTB);
        tvName.setText("Led");
        switchLed = deviceCard1.findViewById(R.id.SwitchLed);

        // Tham chiếu đến LED/OnBoard trong Firebase Realtime Database
        ledOnboardReference = FirebaseDatabase.getInstance().getReference("LED/OnBoard");

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

        // Cập nhật trạng thái Switch lên Firebase khi người dùng thay đổi
        switchLed.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ledOnboardReference.setValue(isChecked); // Cập nhật trạng thái lên Firebase
        });

        // Thiết lập onClick cho từng thẻ thiết bị để mở DeviceDetailFragment
        deviceCard1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeviceDetailFragment("LED", "Thiết bị LED với chức năng OnBoard.");
            }
        });

        deviceCard1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở DeviceDetailFragment với NavController và truyền dữ liệu
                openDeviceDetailFragment("Thiết bị 1", "Mô tả chi tiết của thiết bị 1");
            }
        });
        deviceCard2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở DeviceDetailFragment với NavController và truyền dữ liệu
                openDeviceDetailFragment("Thiết bị 1", "Mô tả chi tiết của thiết bị 1");
            }
        });
        deviceCard3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở DeviceDetailFragment với NavController và truyền dữ liệu
                openDeviceDetailFragment("Thiết bị 1", "Mô tả chi tiết của thiết bị 1");
            }
        });
        deviceCard4.setOnClickListener(new View.OnClickListener() {
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

package com.example.appsmarthome.ui.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.appsmarthome.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class DoorFragment extends Fragment {

    private DatabaseReference Reference;
    private SwitchCompat switchDoor,switchMotor_LR;
    private boolean isUpdating = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_door, container, false);

        if (getActivity() != null) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(false); // Ẩn nút back
                actionBar.setHomeButtonEnabled(false); // Vô hiệu hóa chức năng nút back
            }
        }
        switchDoor = view.findViewById(R.id.Switch);
        // Tham chiếu đến LED/OnBoard trong Firebase Realtime Database
        Reference = FirebaseDatabase.getInstance().getReference("ESP32/Door");
        // Lắng nghe thay đổi từ Firebase để cập nhật Switch
        Reference.addValueEventListener(new ValueEventListener() {
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
                Reference.setValue(isChecked);
            }
        });


        return view;

    }

}

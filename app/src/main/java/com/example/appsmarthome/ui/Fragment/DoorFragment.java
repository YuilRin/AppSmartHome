package com.example.appsmarthome.ui.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appsmarthome.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class DoorFragment extends Fragment {

    private DatabaseReference Reference,passwordReference;
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

        Reference = FirebaseDatabase.getInstance().getReference("ESP32/Door");
        passwordReference = FirebaseDatabase.getInstance().getReference("ESP32/Password/Key");

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

        Button back=view.findViewById(R.id.buttonBack);
        back.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.nav_home);
        });


        Button btnChangePassword = view.findViewById(R.id.button_change_password);
        btnChangePassword.setOnClickListener(v -> showPasswordDialog());
        return view;

    }private void showPasswordDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_change_password, null);
        EditText etCurrentPassword = dialogView.findViewById(R.id.et_current_password);
        EditText etNewPassword = dialogView.findViewById(R.id.et_new_password);
        EditText etConfirmPassword = dialogView.findViewById(R.id.et_confirm_password);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Đổi mật khẩu");
        builder.setView(dialogView);

        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            String currentPassword = etCurrentPassword.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(requireContext(), "Mật khẩu mới không khớp!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra mật khẩu hiện tại trên Firebase
            passwordReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String storedPassword = snapshot.getValue(String.class);
                    if (storedPassword != null && storedPassword.equals(currentPassword)) {
                        // Cập nhật mật khẩu mới
                        passwordReference.setValue(newPassword).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(requireContext(), "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireContext(), "Đổi mật khẩu thất bại!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(requireContext(), "Mật khẩu hiện tại không đúng!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(requireContext(), "Lỗi Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }


}

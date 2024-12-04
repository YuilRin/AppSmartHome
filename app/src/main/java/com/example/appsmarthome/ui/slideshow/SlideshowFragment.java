package com.example.appsmarthome.ui.slideshow;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.appsmarthome.R;
import com.example.appsmarthome.databinding.FragmentSlideshowBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;
    private final Map<SwitchCompat, DatabaseReference> switchMap = new HashMap<>();
    private final Map<SwitchCompat, ValueEventListener> valueEventListenerMap = new HashMap<>();
    private boolean isUpdating = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SwitchCompat switchTurn = root.findViewById(R.id.SwitchTurn);
        DatabaseReference
                turnReference =
                FirebaseDatabase.getInstance()
                        .getReference("ESP32/SYSTEM/OPEN");
        switchMap.put(switchTurn, turnReference);
        setupSwitchListeners();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
            valueEventListenerMap.put(switchCompat, valueEventListener);
            reference.addValueEventListener(valueEventListener);
            // Lắng nghe thay đổi từ người dùng
            switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!isUpdating) {
                    reference.setValue(isChecked); // Cập nhật Firebase khi có thay đổi từ người dùng
                }
            });
        }
    }
}
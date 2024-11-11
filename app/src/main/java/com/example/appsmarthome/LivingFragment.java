package com.example.appsmarthome;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.appsmarthome.R;

public class LivingFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_living, container, false);

        // Nhận dữ liệu từ Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            String deviceName = bundle.getString("device_name");
            String deviceDescription = bundle.getString("device_description");

            // Hiển thị dữ liệu trong các TextView
            TextView deviceNameTextView = view.findViewById(R.id.deviceName);
            TextView deviceDescriptionTextView = view.findViewById(R.id.deviceDescription);

            deviceNameTextView.setText(deviceName);
            deviceDescriptionTextView.setText(deviceDescription);
        }

        return view;
    }
}

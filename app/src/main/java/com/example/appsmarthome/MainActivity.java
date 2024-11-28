package com.example.appsmarthome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.example.appsmarthome.WorkerClass.MyWorker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.appsmarthome.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long initialDelay = calculateInitialDelay();
        WorkManager.getInstance(this).cancelAllWork();

        // Create OneTimeWorkRequest with the calculated initial delay
        Data inputData = new Data.Builder()
                .putString("database_path", "ESP8266/LED/Living_Room") // Địa chỉ Firebase
                .build();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .build();

// Enqueue the work request
        WorkManager.getInstance(this).enqueueUniqueWork(
                "uniqueWork", // Tên công việc
                ExistingWorkPolicy.REPLACE, // Thay thế công việc cũ nếu đã có
                workRequest);// Use 'this' to refer to the context


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) { // ID của mục "Đăng xuất"
            FirebaseAuth.getInstance().signOut(); // Đăng xuất Firebase
            clearUserEmail(); // Xóa thông tin email đã lưu (tùy theo cách lưu trữ của bạn)

            // Chuyển đến LoginActivity
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("Choose", false); // Truyền flag nếu cần
            startActivity(intent);
            finish(); // Kết thúc Activity hiện tại
            return true; // Xác nhận xử lý thành công
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void clearUserEmail() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("USER_EMAIL");
        editor.apply();
    }

    private long calculateInitialDelay() {
        Calendar currentDate = Calendar.getInstance();
        Calendar targetDate = Calendar.getInstance();

        // Đặt thời gian mục tiêu là 6 PM
        targetDate.set(Calendar.HOUR_OF_DAY, 12);
        targetDate.set(Calendar.MINUTE, 0);
        targetDate.set(Calendar.SECOND, 0);

        // Nếu thời gian hiện tại đã qua 6 PM, chuyển sang 6 PM ngày hôm sau
        if (currentDate.after(targetDate)) {
            targetDate.add(Calendar.DATE, 1);
        }

        // Tính khoảng cách giữa thời gian hiện tại và thời gian mục tiêu
        return targetDate.getTimeInMillis() - currentDate.getTimeInMillis();
    }


}
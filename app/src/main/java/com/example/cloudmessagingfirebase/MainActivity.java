package com.example.cloudmessagingfirebase;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<String> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission granted
                    getDeviceToken();
                } else {
                    // Permission denied
                    Log.e("FireBaseLogs", "Notification permission denied");
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Set up insets for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Request notification permission
        requestPermission();
    }

    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                // Permission already granted
                getDeviceToken();
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // Optionally explain why you need the permission (not implemented here)
                Log.v("FireBaseLogs", "Explain why notification permission is needed");
            } else {
                // Request permission
                resultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            // Permissions not required for older versions
            getDeviceToken();
        }
    }

    public void getDeviceToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.e("FireBaseLogs", "Failed to get the device token: " + task.getException());
                    return;
                }

                // Get the device token
                String token = task.getResult();
                Log.v("FireBaseLogs", "Device Token: " + token);

                // Update the TextView with the token
                TextView tokenTextView = findViewById(R.id.device_token_text);
                tokenTextView.setText("Device Token: " + token);
            }
        });
    }
}

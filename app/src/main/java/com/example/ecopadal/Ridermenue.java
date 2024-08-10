package com.example.ecopadal;

import static android.widget.Toast.LENGTH_SHORT;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class Ridermenue extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter = null;
    private BluetoothSocket bluetoothSocket = null;
    private OutputStream outputStream = null;
    private boolean isLocked = true;
    private static final String DEVICE_ADDRESS = "00:11:22:33:AA:BB"; // Replace with your module's MAC address
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ridermenue);


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(DEVICE_ADDRESS);

        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Button lockUnlockButton = findViewById(R.id.lockUnlockButton);

        lockUnlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(isLocked){
                        outputStream.write('U'); //send u to unlock
                        lockUnlockButton.setText("Unlock");
                        Toast.makeText(getApplicationContext(), "Unlocked", Toast.LENGTH_SHORT).show();
                    }else {
                        outputStream.write('L');
                        lockUnlockButton.setText("Lock");
                        Toast.makeText(getApplicationContext(), "locked", Toast.LENGTH_SHORT).show();
                    }
                    isLocked=!isLocked;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    @Override
    protected void  onDestroy() {
        super.onDestroy();
        try {
            if (outputStream != null) outputStream.close();
            if (bluetoothSocket != null) bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
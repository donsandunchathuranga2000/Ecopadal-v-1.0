package com.example.ecopadal;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class PaymentActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Button payButton = findViewById(R.id.btnSaveDetails);

        payButton.setOnClickListener(v -> showPaymentSuccessfulDialog());
    }

    private void showPaymentSuccessfulDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Payment Successful")
                .setMessage("Your payment has been processed successfully.")
                .setPositiveButton("OK", (dialog, which) -> promptForBluetooth())
                .show();
    }

    private void promptForBluetooth() {
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            new AlertDialog.Builder(this)
                    .setTitle("Enable Bluetooth")
                    .setMessage("Please turn on Bluetooth to continue.")
                    .setPositiveButton("Turn On", (dialog, which) -> {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
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
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            connectToDevice();
        }
    }

    private void connectToDevice() {
        // Placeholder: Assuming connection is successful, move to RiderMenuActivity
        Toast.makeText(this, "Connected to Bluetooth device", Toast.LENGTH_SHORT).show();

        // Navigate to RiderMenuActivity
        Intent intent1 = new Intent(PaymentActivity.this, Ridermenue.class);
        startActivity(intent1);
        finish(); // Optionally, finish current activity
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            connectToDevice();
        }
    }
}


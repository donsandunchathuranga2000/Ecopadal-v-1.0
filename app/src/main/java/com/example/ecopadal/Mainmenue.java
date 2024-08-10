package com.example.ecopadal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Mainmenue extends AppCompatActivity {
    private TextView greetingText;
    private ImageView profileImage;
    private Button buttonOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenue);

        // Initialize views
        greetingText = findViewById(R.id.greetingText);
        profileImage = findViewById(R.id.profileImage);
        buttonOrder = findViewById(R.id.orderButton);

        // Get the currently logged-in user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String name = user.getDisplayName();
            greetingText.setText("Hi, " + name);
        } else {
            // Handle the case when the user is not logged in
            greetingText.setText("Hi, Guest");
        }

        // Set a click listener on the order button
        buttonOrder.setOnClickListener(v -> {
            Intent intent = new Intent(Mainmenue.this, PaymentActivity.class);
            startActivity(intent);
        });
    }
}

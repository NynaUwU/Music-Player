package com.example.musicplayer;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

   ImageButton sideMenuButton = findViewById(R.id.sideMenuButton);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ImageButton sideMenuButton = findViewById(R.id.sideMenuButton);

        sideMenuButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code to execute when the button is clicked
                // For example, display a Toast message:
                Toast.makeText(MainActivity.this, "Button clicked!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
package com.example.rentifyapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Profile extends AppCompatActivity {

    private Button buttonToAddCategory, buttonShowListofUsers, buttonToAddItem, buttonToCheckRequests, buttonToRentItem;
    private TextView fullName, Type;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ImageButton buttonBackToWelcomePage = findViewById(R.id.buttonBackToWelcomPage);
        buttonBackToWelcomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), WelcomePage.class));
            }
        });

        // Button declarations
        buttonToAddCategory = findViewById(R.id.buttonToAddCategory);
        buttonShowListofUsers = findViewById(R.id.buttonShowListofUsers);
        buttonToAddItem = findViewById(R.id.buttonToAddItem);
        buttonToRentItem = findViewById(R.id.buttonToRentItem);
        buttonToCheckRequests = findViewById(R.id.buttonToCheckRequests);

        // TextView declarations
        fullName = findViewById(R.id.textViewFullName);
        Type = findViewById(R.id.textViewUserType);

        // Firebase initialization
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        // Retrieve user details from Firestore
        DocumentReference documentReference = firestore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    String fullname = value.getString("firstName") + " " + value.getString("lastName");
                    fullName.setText(fullname);

                    String userType = value.getString("userType");
                    Type.setText("User Type: " + userType);

                    // Show/hide buttons based on user type
                    switch (userType) {
                        case "admin":
                            buttonShowListofUsers.setVisibility(View.VISIBLE);
                            buttonToAddCategory.setVisibility(View.VISIBLE);
                            buttonToAddItem.setVisibility(View.GONE);
                            buttonToRentItem.setVisibility(View.GONE);
                            buttonToCheckRequests.setVisibility(View.GONE);
                            break;

                        case "lessor":
                            buttonToAddItem.setVisibility(View.VISIBLE);
                            buttonShowListofUsers.setVisibility(View.GONE);
                            buttonToAddCategory.setVisibility(View.GONE);
                            buttonToRentItem.setVisibility(View.GONE);
                            buttonToCheckRequests.setVisibility(View.VISIBLE);
                            break;

                        case "renter":
                            buttonToRentItem.setVisibility(View.VISIBLE);
                            buttonShowListofUsers.setVisibility(View.GONE);
                            buttonToAddCategory.setVisibility(View.GONE);
                            buttonToAddItem.setVisibility(View.GONE);
                            buttonToCheckRequests.setVisibility(View.GONE);
                            break;

                        default:
                            buttonToAddItem.setVisibility(View.GONE);
                            buttonShowListofUsers.setVisibility(View.GONE);
                            buttonToAddCategory.setVisibility(View.GONE);
                            buttonToRentItem.setVisibility(View.GONE);
                            buttonToCheckRequests.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(Profile.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set button click listeners
        buttonToAddCategory.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, AddCategory.class);
            startActivity(intent);
        });

        buttonShowListofUsers.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, UserList.class);
            startActivity(intent);
        });

        buttonToAddItem.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, addItem.class);
            startActivity(intent);
        });

        buttonToRentItem.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, RentItem.class);
            startActivity(intent);
        });
        buttonToCheckRequests.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, RequestsRespond.class);
            startActivity(intent);
        });
    }
}

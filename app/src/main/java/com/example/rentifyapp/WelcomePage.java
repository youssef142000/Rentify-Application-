package com.example.rentifyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class WelcomePage extends AppCompatActivity {
    TextView fullName, Type;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String userID;
    Button buttonToRentItem;
    DatabaseReference usersRef;
    private Button buttonToProfilePage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.welcome_page);

        buttonToProfilePage = findViewById(R.id.buttonToProfilePage);
        buttonToProfilePage.setOnClickListener(v -> {
            // Navigate to the Profile Page
            Intent intent = new Intent(WelcomePage.this, Profile.class);
            startActivity(intent);
        });




        fullName = findViewById(R.id.fullName);
        Type = findViewById(R.id.userType);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        DocumentReference documentReference = firestore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String fullname = value.getString("firstName") +" "+ value.getString("lastName");
                fullName.setText(fullname);
                String userType = "User Category: " + value.getString("userType");
                Type.setText(userType);
            }
        });





    }
}
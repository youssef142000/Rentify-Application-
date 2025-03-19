package com.example.rentifyapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.text.TextUtils;
import android.widget.Button;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    TextInputEditText email, password;
    public Button button;
    private FirebaseAuth mAuth;
    Button loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        EdgeToEdge.enable(this);
        email = findViewById(R.id.EmailField);
        password = findViewById(R.id.PasswordField);
        loginButton = findViewById(R.id.LoginButton);

        loginButton.setOnClickListener(view -> {
            String emailValue, passwordValue;
            emailValue = String.valueOf(email.getText());
            passwordValue = String.valueOf(password.getText());

            if(TextUtils.isEmpty(emailValue)){
                Toast.makeText(MainActivity.this, "Enter your Email", Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(passwordValue)){
                Toast.makeText(MainActivity.this, "Enter your password", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(emailValue, passwordValue)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(MainActivity.this, "Logged in successfully",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), WelcomePage.class));
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });


        button= (Button) findViewById(R.id.ForwardingToSigupButton);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this,SignUp.class);
                startActivity(intent);
            }
        });

    }
}
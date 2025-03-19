package com.example.rentifyapp;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    TextInputEditText firstName, lastName, email, password;
    String UserType;
    Button signUpButton;
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    TextView directToLogin;
    String userId;

// create isAlpha method that checks if a string only letters

    public boolean isAlpha(String name) {
        // All letters -- > true
        // letters with any other entries -->False
        char[] chars = name.toCharArray();

        for (char c : chars) {
            if(!Character.isLetter(c)) {
                return false;
            }
        }

        return true;
    }
    // isAlpha ends here


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        //implement Spinner or dropdown list with two type of users (Lessor or Renter)
        Spinner spinner = findViewById(R.id.userType);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                UserType = item;
                Toast.makeText(SignUp.this, "Selected Item" + item, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ArrayList<String> listOfUsers = new ArrayList<>();
        listOfUsers.add("lessor");
        listOfUsers.add("renter");



        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listOfUsers);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinner.setAdapter(adapter);


        //Inputs Ids'
        firstName = findViewById(R.id.firstNameInput);
        lastName = findViewById(R.id.lastNameInput);
        email = findViewById(R.id.emailInput);
        password = findViewById(R.id.passwordInput);
        UserType = spinner.getSelectedItem().toString();
        signUpButton = findViewById(R.id.signUp);
        directToLogin = findViewById(R.id.loginNow);

        //direct to login page.
        directToLogin.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        signUpButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                //get the values of inputs of the from
                String firstname, lastname,userType,  Email, Password;
                firstname = String.valueOf(firstName.getText());
                lastname = String.valueOf(lastName.getText());
                userType = spinner.getSelectedItem().toString();
                Email = String.valueOf(email.getText());
                Password = String.valueOf(password.getText());

                //validate the input of first name if it is empty or not.

                if(TextUtils.isEmpty(firstname)){
                    Toast.makeText(SignUp.this, "Enter your First name", Toast.LENGTH_SHORT).show();
                    return;
                }
                // to check if the input of first name is only letters
                if(!isAlpha(firstname)){
                    Toast.makeText(SignUp.this, "Enter your First name with only Letters", Toast.LENGTH_LONG).show();
                    return;
                }
                //validate the input of Last name if it is empty or not.
                if(TextUtils.isEmpty(lastname)){
                    Toast.makeText(SignUp.this, "Enter your Last name", Toast.LENGTH_SHORT).show();
                    return;
                }
                // to check if the input of first name is only letters
                if(!isAlpha(lastname)){
                    Toast.makeText(SignUp.this, "Enter your Last name with only Letters", Toast.LENGTH_LONG).show();
                    return;
                }
                //validate the input of email if it is empty or not.
                // no need to check for the type of the input because the form already made to take email inputs only
                if(TextUtils.isEmpty(Email)){
                    Toast.makeText(SignUp.this, "Enter your Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                //validate the input of email if it is empty or not.
                if(TextUtils.isEmpty(Password)){
                    Toast.makeText(SignUp.this, "Enter your password name", Toast.LENGTH_SHORT).show();
                    return;
                }

                //create user with email and password function
                mAuth.createUserWithEmailAndPassword(Email, Password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                //this part could commented as it is not essential right now
                                //Send Verification link to the email used to register
                                //FirebaseUser user1 = mAuth.getCurrentUser();
                            //    user1.sendEmailVerification().addOnSuccessListener(aVoid -> {
                                    // this message will be sent to the user if the firebase was able to send a verification email to the email address used
                               //     Toast.makeText(SignUp.this, "Verify Email has been sent to your email address, please verify your email address", Toast.LENGTH_LONG).show();
                             //   }).addOnFailureListener(e -> {
                                    // if firebase failed in sending the verification email, this will show
                             //       Log.d(TAG,"onFailure: Please use a valid email address" + e.getMessage());
                            //    });
                                //Send Verification link to the email used to register end here


                                Toast.makeText(SignUp.this, "Account created.",
                                        Toast.LENGTH_SHORT).show();
                                //create new User by calling User class
                                User newUser = new User(firstname, lastname, Email, userType, Password);
                                //get the id for user to set its data in cloud store
                                userId = mAuth.getCurrentUser().getUid();
                                //insert user information to to database.
                                DocumentReference documentReference = fStore.collection("users").document(userId);
                                Map<String, Object> user = new HashMap<>();
                                user.put("firstName", newUser.getFirstname());
                                user.put("lastName", newUser.getLastname());
                                user.put("userType", newUser.getUserType());
                                user.put("email", newUser.getEmail());
                                user.put("password", newUser.getPassword());
                                documentReference.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(SignUp.this, "User profile is created",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            } else {
                                Toast.makeText(SignUp.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
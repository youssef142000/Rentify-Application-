package com.example.rentifyapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class UserList extends AppCompatActivity {

    ListView myUsersList;
    List<User> usersList;
    FirebaseFirestore db;
    private FirebaseAuth auth;
    CollectionReference userCollection;
    UserListAdapterCyrcular userListAdapter;
    RecyclerView recyclerViewUsers;
    FirebaseUser userVal = FirebaseAuth.getInstance().getCurrentUser();

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_layout_users_list);
        recyclerViewUsers = findViewById(R.id.recycler_view_Users);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUsers.setNestedScrollingEnabled(false);  // Disable nested scrolling
        usersList = new ArrayList<>();
        userListAdapter = new UserListAdapterCyrcular(usersList, this::showDeleteDisableEnable);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userCollection = db.collection("users");
        recyclerViewUsers.setAdapter(userListAdapter);
        ImageButton buttonBackToProfile = findViewById(R.id.buttonBackToProfile);
        buttonBackToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Profile.class));
            }
        });

    }
    protected void onStart(){
        super.onStart();
        usersList.clear(); // Clear the list to avoid duplicates
        userCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    // Retrieve each field from the document and create a User object
                    final String fName = document.getString("firstName");
                    final String lName = document.getString("lastName");
                    final String email = document.getString("email");
                    final String userType = document.getString("userType");
                    final String passWord = document.getString("password");
                    User user = new User(fName, lName, email, userType, passWord);
                    usersList.add(user); // Add the user to the list
                }
                userListAdapter.notifyDataSetChanged(); // Notify adapter to update the ListView
            }
        }).addOnFailureListener(e -> {
            // Handle the error if necessary
            e.printStackTrace();
        });
    }

    private void showDeleteDisableEnable(User user) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.user_delete_changestatus_dialog, null);
        dialogBuilder.setView(dialogView);
        final Button buttonDelete = dialogView.findViewById(R.id.delUserBtn);

        dialogBuilder.setTitle(user.getFullName());

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        String userEmail = user.getEmail();
        db = FirebaseFirestore.getInstance();
        userCollection = db.collection("users");
        String userType = user.getUserType();
        String userID = userVal.getUid();



         buttonDelete.setOnClickListener(v -> {
             // Handle the error if necessary
             if (Objects.equals(userType, "admin")){
                 return;
             }
             userCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
                 if (!queryDocumentSnapshots.isEmpty()) {
                     for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                         // Retrieve each field from the document and create a User object
                         String email = document.getString("email");
                         String userId = document.getId();

                         if (Objects.equals(email, userEmail)){
                             userCollection.document(userId).delete().
                                     addOnCompleteListener(task ->  {
                                         if (task.isSuccessful()) {

                                             Toast.makeText(UserList.this, "User has been deleted from Database.", Toast.LENGTH_SHORT).show();
                                             // this to refresh the userlist page
                                             Intent i = new Intent(UserList.this, UserList.class);
                                             startActivity(i);
                                         } else {
                                             // if the delete operation is failed
                                             // we are displaying a toast message.
                                             Toast.makeText(UserList.this, "Fail to delete the user. ", Toast.LENGTH_SHORT).show();
                                         }

                                     });
                                     }
                         }
                     }



             }).addOnFailureListener(Throwable::printStackTrace);



         });
    }





}
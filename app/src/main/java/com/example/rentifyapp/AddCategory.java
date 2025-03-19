package com.example.rentifyapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AddCategory extends AppCompatActivity {

    EditText editTextCategoryName;
    EditText editTextCategoryDescription;
    Button buttonAddCategory;
    RecyclerView recyclerViewItems;  // Changed from ListView to RecyclerView
    LinearLayout addCategoryForm;
    DatabaseReference categoryRef;
    DatabaseReference usersRef;
    List<Category> categories;
    boolean isAdmin = false;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        ImageButton buttonBackToProfile = findViewById(R.id.buttonBackToProfile);
        buttonBackToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Profile.class));
            }
        });
        recyclerViewItems = findViewById(R.id.recycler_view_category);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewItems.setNestedScrollingEnabled(false);  // Disable nested scrolling
        // Initialize item list and adapter
        categories = new ArrayList<>();

        categoryAdapter = new CategoryAdapter(categories, this::showUpdateDeleteDialog);
        recyclerViewItems.setAdapter(categoryAdapter);
        // Initialize Firebase reference and UI components
        categoryRef = FirebaseDatabase.getInstance().getReference("categories");
        editTextCategoryName = findViewById(R.id.editTextCategoryName);
        editTextCategoryDescription = findViewById(R.id.editTextCategoryDescription);

        buttonAddCategory = findViewById(R.id.buttonAddCategory);
        addCategoryForm = findViewById(R.id.addCategoryForm);
        // Fetch and handle user type
        getUserType();
        buttonAddCategory.setOnClickListener(v -> addCategory());
    }


    @Override
    protected void onStart() {
        super.onStart();
        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categories.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String id = postSnapshot.child("id").getValue(String.class);
                    String categoryName = postSnapshot.child("categoryName").getValue(String.class);
                    String categoryDescription = postSnapshot.child("categoryDescription").getValue(String.class);
                    Category category = new Category();
                    category.setId(id);
                    category.setCategoryName(categoryName);
                    category.setCategoryDescription(categoryDescription);
                    DataSnapshot itemsSnapshot = postSnapshot.child("items");
                    List<Item> items = new ArrayList<>();
                    for (DataSnapshot itemSnapshot : itemsSnapshot.getChildren()) {
                        Item item = itemSnapshot.getValue(Item.class);
                        if (item != null) {
                            items.add(item);
                        }
                    }
                    category.setItems(items);

                    if (category != null) {
                        categories.add(category);
                    }
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("addItem", "Database error: " + error.getMessage());
                Toast.makeText(AddCategory.this, "Failed to load items", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUpdateDeleteDialog(Category item) {
        if(isAdmin){

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.update_category_dialog, null);
            dialogBuilder.setView(dialogView);
            final EditText editTextCategoryName = dialogView.findViewById(R.id.editTextCategoryName);
            final EditText editTextCategoryDes = dialogView.findViewById(R.id.editTextCategoryDes);
            final Button buttonUpdate = dialogView.findViewById(R.id.buttonUpdateCategory);
            final Button buttonDelete = dialogView.findViewById(R.id.buttonDeleteCategory);
            dialogBuilder.setTitle(item.getCategoryName());
            editTextCategoryName.setText(item.getCategoryName());
            editTextCategoryDes.setText(item.getCategoryDescription());
            final AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
            buttonUpdate.setOnClickListener(v -> {
                updateCategory(item.getId(), editTextCategoryName.getText().toString(), editTextCategoryDes.getText().toString());
                alertDialog.dismiss();
            });
            buttonDelete.setOnClickListener(v -> {
                deleteCategory(item.getId());
                alertDialog.dismiss();
            });

        }else{
            Log.d("UserType", "you are not allow to edit and delete item");
        }


    }

    private void getUserType() {
        if (user != null) {
            String userId = user.getUid();
            DocumentReference userRef = db.collection("users").document(userId);
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot != null && snapshot.exists()) {
                        String userType = snapshot.getString("userType");
                        if (userType != null) {
                            switch (userType) {
                                case "admin":
                                    isAdmin = true;
                                    Log.d("UserType", "Admin user logged in");
                                    // Enable any admin-specific functionality here
                                    break;
                                case "renter":
                                    isAdmin = false;
                                    addCategoryForm.setVisibility(View.GONE); // Hide form for renter
                                    Log.d("UserType", "Renter user logged in, hiding addItemForm");
                                    break;

                                case "lessor":
                                    isAdmin = false;
                                    addCategoryForm.setEnabled(true); // Enable form for lessor
                                    Log.d("UserType", "Lessor user logged in, enabling addItemForm");
                                    break;

                                default:
                                    Log.d("UserType", "Unknown user type");
                                    break;
                            }
                        } else {
                            Log.e("UserType", "userType is null in Firestore");
                        }
                    } else {
                        Log.e("UserType", "User document not found in Firestore");
                    }
                } else {
                    Log.e("UserType", "Failed to retrieve user type", task.getException());
                    Toast.makeText(this, "Failed to retrieve user type", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e("UserType", "User not authenticated");
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }


    private void addCategory() {
        String categoryName = editTextCategoryName.getText().toString().trim();
        String categoryDes = editTextCategoryDescription.getText().toString().trim();

        if (!TextUtils.isEmpty(categoryName) && !TextUtils.isEmpty(categoryDes)) {
            String id = categoryRef.push().getKey();
            String userId = user.getUid();
            Category category = new Category(id, categoryName, categoryDes);
            categoryRef.child(id).setValue(category);
            editTextCategoryName.setText("");
            editTextCategoryDescription.setText("");
            Toast.makeText(this, "Category added", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Enter Name and the Description of the Category", Toast.LENGTH_LONG).show();
        }
    }

    private void updateCategory(String id, String newName, String newDescriptione) {
        DatabaseReference dR = categoryRef.child(id);
        String userId = user.getUid();
        Category item = new Category(id, newName, newDescriptione);
        dR.setValue(item);
        Toast.makeText(getApplicationContext(), "Category Updated", Toast.LENGTH_LONG).show();
    }

    private void deleteCategory(String id) {
        DatabaseReference dR = categoryRef.child(id);
        dR.removeValue();
        Toast.makeText(getApplicationContext(), "Category Deleted", Toast.LENGTH_LONG).show();
    }
}

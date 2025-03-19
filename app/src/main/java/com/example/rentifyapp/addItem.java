package com.example.rentifyapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

public class addItem extends AppCompatActivity {
    EditText editTextItemName;
    EditText editTextItemDescription;
    EditText editTextItemPrice;
    EditText editTextItemTimePeriod;
    Button buttonAddItem;
    Button buttonBackToWelcomePage;
    RecyclerView recyclerViewItems;  // Changed from ListView to RecyclerView
    LinearLayout addItemForm;
    DatabaseReference itemsRef;
    DatabaseReference usersRef;
    List<Item> items;
    boolean isAdmin = false;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ItemAdapter itemAdapter;

    ArrayAdapter<Category> categoryAdapter;
    List<Category> categoryList = new ArrayList<>();
    Spinner categorySpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_item);

        categorySpinner = findViewById(R.id.categoryType);
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        fetchCategories();


        recyclerViewItems = findViewById(R.id.recycler_view_items);
            recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewItems.setNestedScrollingEnabled(false);  // Disable nested scrolling
            // Initialize item list and adapter
            items = new ArrayList<>();

        itemAdapter = new ItemAdapter(items, item -> showUpdateDeleteDialog(item.getCategoryId(), item), "add");

            recyclerViewItems.setAdapter(itemAdapter);
            fetchAllItems();
            // Initialize Firebase reference and UI components
            itemsRef = FirebaseDatabase.getInstance().getReference("items");
            editTextItemName = findViewById(R.id.editTextItemName);
            editTextItemDescription = findViewById(R.id.editTextItemDescription);
            editTextItemPrice = findViewById(R.id.editTextItemPrice);
            editTextItemTimePeriod = findViewById(R.id.editTextItemTimePeriod);

            buttonAddItem = findViewById(R.id.buttonAddItem);
        ImageButton buttonBackToProfile = findViewById(R.id.buttonBackToProfile);
        buttonBackToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Profile.class));
            }
        });
            //addItemForm = findViewById(R.id.addItemForm);
            // Fetch and handle user type
            getUserType();
        buttonAddItem.setOnClickListener(v -> addItemToSelectedCategory());

        }


    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference("categories");
        String currentLessorId = user.getUid();  // Assuming `user` is the current Firebase user

        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();  // Clear current list of items

                // Loop through each category and each item within that category
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    DataSnapshot itemsSnapshot = categorySnapshot.child("items");

                    // Iterate over each item in the current category
                    for (DataSnapshot itemSnapshot : itemsSnapshot.getChildren()) {
                        Item item = itemSnapshot.getValue(Item.class);
                        if (item != null && item.getLessorId().equals(currentLessorId)) {
                            items.add(item);  // Add item to the list if it belongs to the current lessor
                        }
                    }
                }

                // Notify the adapter that data has changed
                itemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("addItem", "Database error: " + error.getMessage());
                Toast.makeText(addItem.this, "Failed to load items", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchCategories() {

        // Get a reference to the "categories" node in Firebase Realtime Database
        DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference("categories");
        // Add a listener to fetch data once
        categoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Clear the existing list of categories to avoid duplicates
                categoryList.clear();

                // Loop through each child (category) in the "categories" data snapshot
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {

                    // Retrieve the ID, name, and description of each category
                    String id = categorySnapshot.child("id").getValue(String.class);
                    String categoryName = categorySnapshot.child("categoryName").getValue(String.class);
                    String categoryDescription = categorySnapshot.child("categoryDescription").getValue(String.class);

                    // Create a new Category object and set its properties
                    Category category = new Category();
                    category.setId(id);
                    category.setCategoryName(categoryName);
                    category.setCategoryDescription(categoryDescription);

                    // If the category is valid, add it to the category list
                    if (category != null) {
                        categoryList.add(category);
                    }
                }

                // Notify the adapter that the data has changed, so it refreshes the Spinner
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                // Log an error message if fetching categories fails
                Log.e("addItem", "Failed to fetch categories: " + error.getMessage());
                // Show a toast message to notify the user that categories failed to load
                Toast.makeText(addItem.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchAllItems() {
        DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference("categories");

        categoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                items.clear();

                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    String categoryId = categorySnapshot.getKey();
                    String categoryName = categorySnapshot.child("categoryName").getValue(String.class); // Get category name
                    DataSnapshot itemsSnapshot = categorySnapshot.child("items");

                    for (DataSnapshot itemSnapshot : itemsSnapshot.getChildren()) {
                        Item item = itemSnapshot.getValue(Item.class);
                        if (item != null) {
                            item.setCategoryId(categoryId);       // Set categoryId for the item
                            item.setCategoryName(categoryName);   // Set categoryName for the item
                            items.add(item);
                        }
                    }
                }

                itemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("fetchAllItems", "Failed to fetch items", error.toException());
                Toast.makeText(addItem.this, "Failed to load items", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void addItemToSelectedCategory() {
        Category selectedCategory = (Category) categorySpinner.getSelectedItem();
        if (selectedCategory != null) {
            String categoryId = selectedCategory.getId();
            String categoryName = selectedCategory.getCategoryName(); // Assuming `Category` has a `getName()` method
            String itemName = editTextItemName.getText().toString().trim();
            String itemDescription = editTextItemDescription.getText().toString().trim();
            String itemTimePeriod = editTextItemTimePeriod.getText().toString().trim();
            double price;

            try {
                price = Double.parseDouble(editTextItemPrice.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid price", Toast.LENGTH_LONG).show();
                return;
            }

            String lessorId = user.getUid();

            if (!TextUtils.isEmpty(itemName) && !TextUtils.isEmpty(itemDescription)) {
                addItemToCategory(categoryId, itemName, itemDescription, price, lessorId, categoryName, itemTimePeriod);
                Toast.makeText(this, "Item added", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Enter Name and Description of the item", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
        }
    }




    private void showUpdateDeleteDialog(String categoryId, Item item) {
        String userID = item.getLessorId();

        // Check if the current user is the owner of the item
        if (user.getUid().equals(userID)) {

            // Build and display the dialog for updating or deleting the item
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.update_item_dialog, null);
            dialogBuilder.setView(dialogView);

            final EditText editTextItemName = dialogView.findViewById(R.id.editTextitemName);
            final EditText editTextItemDes = dialogView.findViewById(R.id.editTextitemDes);
            final EditText editTextItemPrice = dialogView.findViewById(R.id.editTextitemPrice);
            final EditText editTextItemTimePeriod = dialogView.findViewById(R.id.editTextItemTimePeriod);

            final Button buttonUpdate = dialogView.findViewById(R.id.buttonUpdateItem);
            final Button buttonDelete = dialogView.findViewById(R.id.buttonDeleteItem);

            // Set current item values in the dialog fields
            dialogBuilder.setTitle(item.getItemName());
            editTextItemName.setText(item.getItemName());
            editTextItemDes.setText(item.getItemDescription());
            editTextItemPrice.setText(String.valueOf(item.getPrice()));
            editTextItemTimePeriod.setText(String.valueOf(item.getTimePeriod()));
            final AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();

            // Update button click listener
            buttonUpdate.setOnClickListener(v -> {
                String newName = editTextItemName.getText().toString();
                String newDescription = editTextItemDes.getText().toString();
                double newPrice = Double.parseDouble(editTextItemPrice.getText().toString());
                String newItemTimePeriod = editTextItemTimePeriod.getText().toString();
                // Call updateItem with categoryId and itemId
                updateItem(categoryId, item.getId(), newName, newDescription, newPrice, item.getCategoryName(), newItemTimePeriod);
                alertDialog.dismiss();
            });

            // Delete button click listener
            buttonDelete.setOnClickListener(v -> {
                // Call deleteItem with categoryId and itemId
                deleteItem(categoryId, item.getId());
                alertDialog.dismiss();
            });

        } else {
            Log.d("UserType", "You are not allowed to edit and delete this item");
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
                                        //addItemForm.setVisibility(View.GONE); // Hide form for renter
                                        Log.d("UserType", "Renter user logged in, hiding addItemForm");
                                        break;

                                    case "lessor":
                                        isAdmin = false;
                                       // addItemForm.setEnabled(true); // Enable form for lessor
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


    private void addItemToCategory(String categoryId, String itemName, String itemDescription, double price, String lessorId, String categoryName, String itemTimePeriod) {
        DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference("categories");
        String itemId = categoriesRef.child(categoryId).child("items").push().getKey();

        if (itemId != null) {
            // Create a new Item instance with the provided details
            Item newItem = new Item(lessorId, itemId, itemName, itemDescription, price, categoryId, categoryName, itemTimePeriod, false, "available");

            categoriesRef.child(categoryId).child("items").child(itemId).setValue(newItem)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("Firebase", "Item added successfully");

                            // Clear input fields after successful addition
                            editTextItemName.setText("");
                            editTextItemDescription.setText("");
                            editTextItemPrice.setText("");
                            editTextItemTimePeriod.setText("");

                            // Optionally, show a confirmation message
                            Toast.makeText(addItem.this, "Item added successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("Firebase", "Failed to add item", task.getException());
                            Toast.makeText(addItem.this, "Failed to add item", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Log.e("Firebase", "Failed to generate itemId");
            Toast.makeText(addItem.this, "Failed to generate itemId", Toast.LENGTH_SHORT).show();
        }
    }




    private void updateItem(String categoryId, String itemId, String newName, String newDescription, Double newPrice, String categoryName, String itemTimePeriod) {
        DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference("categories")
                .child(categoryId)
                .child("items")
                .child(itemId);

        String lessorId = user.getUid();  // Use the current user's UID as the lessorId

        // Create an updated Item object with all fields, including categoryId and categoryName
        Item updatedItem = new Item(lessorId, itemId, newName, newDescription, newPrice, categoryId, categoryName, itemTimePeriod, false, "available");

        itemRef.setValue(updatedItem).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getApplicationContext(), "Item Updated", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to update item", Toast.LENGTH_LONG).show();
            }
        });
    }



    private void deleteItem(String categoryId, String itemId) {
        DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference("categories")
                .child(categoryId)
                .child("items")
                .child(itemId);

        itemRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getApplicationContext(), "Item Deleted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to delete item", Toast.LENGTH_LONG).show();
            }
        });
    }



}



package com.example.rentifyapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RentItem extends AppCompatActivity {
    // Firebase references and user
    private DatabaseReference itemsRef;
    private FirebaseUser user;
    // UI components
    private RecyclerView recyclerViewItems;
    private SearchView searchView;
    // Adapter and data list
    private List<Item> items;
    private ItemAdapter itemAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_item);

        // Initialize views
        recyclerViewItems = findViewById(R.id.recycler_view_items);
        searchView = findViewById(R.id.searchView);
        ImageButton buttonBackToProfile = findViewById(R.id.buttonBackToProfile);

        // Set up RecyclerView
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firebase references and data list
        user = FirebaseAuth.getInstance().getCurrentUser();
        itemsRef = FirebaseDatabase.getInstance().getReference("categories");
        items = new ArrayList<>();

        // Initialize adapter with "requestToRent" callback
        itemAdapter = new ItemAdapter(items, this::requestToRent, "rent");
        recyclerViewItems.setAdapter(itemAdapter);

        // Fetch items
        fetchAllItems();

        // Back button functionality
        buttonBackToProfile.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Profile.class)));

        // Search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterItems(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterItems(newText);
                return true;
            }
        });
    }

    private void fetchAllItems() {
        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear(); // Clear the current list
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    for (DataSnapshot itemSnapshot : categorySnapshot.child("items").getChildren()) {
                        Item item = itemSnapshot.getValue(Item.class);
                        if (item != null) {
                            // Fetch the rentStatus
                            String rentStatus = itemSnapshot.child("rentStatus").getValue(String.class);
                            DatabaseReference itemRef = itemsRef.child(categorySnapshot.getKey())
                                    .child("items")
                                    .child(item.getId());

                            if (rentStatus != null) {
                                if ("deny".equals(rentStatus)) {
                                    // Update the rentStatus to "available" in Firebase
                                    itemRef.child("rentStatus").setValue("available");
                                    item.setRentStatus("available");
                                } else {
                                    item.setRentStatus(rentStatus); // Set rentStatus for other cases
                                }
                            } else {
                                item.setRentStatus("available"); // Default to "available"
                            }
                            items.add(item); // Add to the list
                        }
                    }
                }
                itemAdapter.notifyDataSetChanged(); // Refresh the RecyclerView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RentItem.this, "Failed to load items.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Filter items based on search query
    private void filterItems(String query) {
        if (query == null || query.isEmpty()) {
            itemAdapter.restoreFullList();
            return;
        }

        List<Item> filteredList = new ArrayList<>();
        for (Item item : items) {
            if (item.getItemName().toLowerCase().contains(query.toLowerCase()) ||
                    item.getCategoryName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }

        itemAdapter.updateList(filteredList);
    }

    // Handle "Request to Rent" functionality
    private void requestToRent(Item item) {
        if (user == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        DatabaseReference requestsRef = FirebaseDatabase.getInstance().getReference("rentalRequests");
        DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference("categories")
                .child(item.getCategoryId())
                .child("items")
                .child(item.getId());

        RentalRequest request = new RentalRequest(userId, item.getId(), item.getLessorId());
        requestsRef.push().setValue(request)
                .addOnSuccessListener(aVoid -> {
                    // Update rentStatus to "pending" in Firebase
                    itemRef.child("rentStatus").setValue("pending");

                    // Update the item's local state
                    item.setRentStatus("pending");
                    itemAdapter.notifyDataSetChanged(); // Refresh the adapter

                    Toast.makeText(this, "Request sent for: " + item.getItemName(), Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Request failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("requestToRent", "Failed to send request: ", e);
                });
    }



    @Override
    protected void onStart() {
        super.onStart();

        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    DataSnapshot itemsSnapshot = categorySnapshot.child("items");
                    for (DataSnapshot itemSnapshot : itemsSnapshot.getChildren()) {
                        Item item = itemSnapshot.getValue(Item.class);
                        if (item != null) {
                            items.add(item);
                        }
                    }
                }

                if (!items.isEmpty()) {
                    Log.d("onStart", "Items loaded: " + items.size());
                    itemAdapter.restoreFullList();
                } else {
                    Log.e("onStart", "No items available");
                    Toast.makeText(RentItem.this, "No items available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("onStart", "Database error: " + error.getMessage());
            }
        });
    }
}

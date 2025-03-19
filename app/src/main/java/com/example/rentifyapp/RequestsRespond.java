package com.example.rentifyapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RequestsRespond extends AppCompatActivity {
    // Firebase references and user
    private DatabaseReference itemsRef;

    // UI components
    private RecyclerView recyclerViewRequests;
    // Adapter and data list
    private List<Item> itemsRequested;
    private RequestAdapter requestAdapter;
    List<RentalRequest> lessorRequests;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DatabaseReference requestsRef;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_requests_respond);

        recyclerViewRequests = findViewById(R.id.recyclerViewRequests);
        ImageButton buttonBackToProfile = findViewById(R.id.buttonBackToProfile);
        buttonBackToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Profile.class));
            }
        });
        lessorRequests = new ArrayList<>();
        DatabaseReference requestsRef = FirebaseDatabase.getInstance().getReference("rentalRequests");
        recyclerViewRequests.setLayoutManager(new LinearLayoutManager(this));
      //  fetchAllRequests();


        itemsRef = FirebaseDatabase.getInstance().getReference("categories");
        itemsRequested = new ArrayList<>();
        requestAdapter = new RequestAdapter(itemsRequested, this::showRespondDialog);
        recyclerViewRequests.setAdapter(requestAdapter);



    }
    protected void onStart(){
        super.onStart();
        fetchRequestedItems();
    }
//    private void fetchAllRequests() {
//        String currentLessorId = user.getUid();
//        requestsRef = FirebaseDatabase.getInstance().getReference("rentalRequests");
//        requestsRef.addValueEventListener(new ValueEventListener() {
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                lessorRequests.clear();
//                for (DataSnapshot requestsSnapshot : snapshot.getChildren()) {
//                    RentalRequest request = requestsSnapshot.getValue(RentalRequest.class);
//                    if (request != null){
//                        String ownerid = requestsSnapshot.child("ownerId").getValue(String.class);
//                        String renterid = requestsSnapshot.child("renterId").getValue(String.class);
//                        String itemid = requestsSnapshot.child("itemId").getValue(String.class);
//                        RentalRequest lessorRequest = new RentalRequest(renterid, ownerid, itemid);
//                        if (ownerid.equals(currentLessorId)){
//
//                            lessorRequests.add(lessorRequest);
//                        }
//                    }
//                }
//            }
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(RequestsRespond.this, "Failed to load Request.", Toast.LENGTH_SHORT).show();
//            }});
//    }

    private void fetchRequestedItems() {
        String currentLessorId = user.getUid();
        itemsRef = FirebaseDatabase.getInstance().getReference("categories");
        itemsRef.addValueEventListener(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemsRequested.clear();
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    DataSnapshot itemsSnapshot = categorySnapshot.child("items");
                    // Iterate over each item in the current category
                    for (DataSnapshot itemSnapshot : itemsSnapshot.getChildren()) {
                        Item item = itemSnapshot.getValue(Item.class);
                        if(item != null && currentLessorId.equals(item.getLessorId())){
                                    if (item.getRentStatus().equals("pending")){
                                        itemsRequested.add(item);
                                    }
                        }
                    }
                }
                requestAdapter.notifyDataSetChanged();
            }
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RequestsRespond.this, "Failed to load Request.", Toast.LENGTH_SHORT).show();
            }});
    }

    private void showRespondDialog(Item item) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.request_action_dialog, null);
        dialogBuilder.setView(dialogView);
        final Button buttonAccept = dialogView.findViewById(R.id.acceptRequest);
        final Button buttonReject = dialogView.findViewById(R.id.rejectRequest);
        dialogBuilder.setTitle(item.getItemName());

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();



        buttonAccept.setOnClickListener(v -> {
            item.setRentStatus("not available");
            alertDialog.dismiss();
        });
        buttonReject.setOnClickListener(v -> {
            item.setRentStatus("available");
            alertDialog.dismiss();
        });
    }



}
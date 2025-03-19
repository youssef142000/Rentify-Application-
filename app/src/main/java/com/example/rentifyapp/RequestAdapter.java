package com.example.rentifyapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
//import java.util.Objects;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ItemViewHolder>{
    private List<Item> itemList; //  list of items that has been requested
    //private List<RentalRequest> rentingRequest;
    private RequestAdapter.OnItemClickListener listener; // Listener for item interactions
   // private FirebaseFirestore db = FirebaseFirestore.getInstance(); // to retrieve a collection from the database
   // private DatabaseReference categoriesRef = FirebaseDatabase.getInstance().getReference("categories"); // to fetch data from the categories
    //String itemId = categoriesRef.child(categoryId).child("items").push().getKey();
     //String name = categoriesRef.child(categoryId).child("items").child(itemId).getString("categoryName");
    // Interface for handling item click events
    public interface OnItemClickListener {
        void onItemClick(Item item);
    }


    // Constructor for adapter initialization
    public RequestAdapter(List<Item> itemList, RequestAdapter.OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;

    }
    public void onBindViewHolder(@NonNull RequestAdapter.ItemViewHolder holder, int position) {
        // Bind data to each item in RecyclerView
        Item item;
        item = itemList.get(position);

        holder.bind(item, listener);
    }
    public int getItemCount() {
        // Return total category count in RecyclerView
        return itemList.size();
    }
    @NonNull

    public RequestAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_requests_list, parent, false);
        return new RequestAdapter.ItemViewHolder(view);
    }




    // ViewHolder class for RecyclerView items
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textViewItemReqName;
        TextView textViewDescReq;
        TextView textViewPriceReq;
        TextView textViewCategoryTypeReq;
        TextView textViewItemTimePeriodReq;
        TextView textViewItemStatus;

        //TextView itemID;
        //TextView ownerID;
        //TextView renterID;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewItemReqName = itemView.findViewById(R.id.textViewItemReqName);
            textViewDescReq = itemView.findViewById(R.id.textViewItemDescriptionReq);
            textViewPriceReq = itemView.findViewById(R.id.textViewItemPriceReq);
            textViewCategoryTypeReq = itemView.findViewById(R.id.textViewCategoryTypeReq);
            textViewItemTimePeriodReq = itemView.findViewById(R.id.textViewItemTimePeriodReq);
            textViewItemStatus = itemView.findViewById(R.id.textViewItemStatus);
        }

        public void bind( Item item,  RequestAdapter.OnItemClickListener listener) {
            textViewItemReqName.setText(item.getItemName());
            textViewDescReq.setText(item.getItemDescription());
            textViewPriceReq.setText(String.valueOf(item.getPrice()));
            textViewCategoryTypeReq.setText(item.getCategoryName());
            textViewItemTimePeriodReq.setText(item.getTimePeriod());
            textViewItemStatus.setText(item.getRentStatus());
            itemView.setOnClickListener(v -> listener.onItemClick(item));

        }

    }
}


package com.example.rentifyapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private List<Item> itemList; // Original list of items
    private List<Item> filteredList; // Filtered list for search functionality
    private OnItemClickListener listener; // Listener for item interactions
    private String mode; // Mode to distinguish between "add" and "rent"

    // Interface for handling item click events
    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    // Constructor for adapter initialization
    public ItemAdapter(List<Item> itemList, OnItemClickListener listener, String mode) {
        this.itemList = itemList;
        this.filteredList = new ArrayList<>(itemList);
        this.listener = listener;
        this.mode = mode;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_list, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Bind data to each item in RecyclerView
        Item item;
        if(this.mode.equals("add")){
             item = itemList.get(position);
        }else{
             item = filteredList.get(position);
        }
        holder.bind(item, listener, mode);
    }

    @Override
    public int getItemCount() {
        // Return the size of the filtered list
        if(this.mode.equals("rent")){

            return filteredList.size();

        }else {
            return itemList.size();
        }
    }

    // Update the list displayed in the RecyclerView
    public void updateList(List<Item> newList) {
        filteredList.clear();
        filteredList.addAll(newList);
        notifyDataSetChanged();
    }

    // Restore the full list in the RecyclerView
    public void restoreFullList() {
        filteredList.clear();
        filteredList.addAll(itemList);
        notifyDataSetChanged();
    }

    // Replace the original list with a new one and refresh the RecyclerView
    public void setOriginalList(List<Item> newOriginalList) {
        itemList.clear();
        itemList.addAll(newOriginalList);
        restoreFullList();
    }

    // ViewHolder class for RecyclerView items
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewDesc, textViewPrice, textViewCategoryType, textViewItemTimePeriod;
        Button buttonRequestToRent;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewItemName);
            textViewDesc = itemView.findViewById(R.id.textViewItemDescription);
            textViewPrice = itemView.findViewById(R.id.textViewItemPrice);
            textViewCategoryType = itemView.findViewById(R.id.textViewCategoryType);
            textViewItemTimePeriod = itemView.findViewById(R.id.textViewItemTimePeriod);
            buttonRequestToRent = itemView.findViewById(R.id.buttonRequestToRent);
        }

        public void bind(final Item item, final OnItemClickListener listener, String mode) {
            textViewName.setText(item.getItemName());
            textViewDesc.setText(item.getItemDescription());
            textViewPrice.setText(String.valueOf(item.getPrice()));
            textViewCategoryType.setText(item.getCategoryName());
            textViewItemTimePeriod.setText(item.getTimePeriod());

            if (mode.equals("add")) {
                buttonRequestToRent.setVisibility(View.GONE); // Hide button in add mode
                itemView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onItemClick(item);
                    }
                });
            } else if (mode.equals("rent")) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if (currentUser != null && currentUser.getUid().equals(item.getLessorId())) {
                    buttonRequestToRent.setVisibility(View.GONE); // Hide button for lessor
                } else {
                    buttonRequestToRent.setVisibility(View.VISIBLE);

                    switch (item.getRentStatus()) {
                        case "pending":
                            buttonRequestToRent.setText("Pending");
                            buttonRequestToRent.setEnabled(false); // Disable the button
                            break;

                        case "accept":
                            buttonRequestToRent.setText("Not Available");
                            buttonRequestToRent.setEnabled(false); // Disable the button
                            break;

                        case "available":
                            buttonRequestToRent.setText("Request to Rent");
                            buttonRequestToRent.setEnabled(true); // Enable the button
                            buttonRequestToRent.setOnClickListener(v -> {
                                if (listener != null) {
                                    listener.onItemClick(item);
                                }
                            });
                            break;

                        case "deny":
                            // This case should already be updated to "available" in fetchAllItems
                            buttonRequestToRent.setText("Request to Rent");
                            buttonRequestToRent.setEnabled(true);
                            buttonRequestToRent.setOnClickListener(v -> {
                                if (listener != null) {
                                    listener.onItemClick(item);
                                }
                            });
                            break;

                        default:
                            buttonRequestToRent.setVisibility(View.GONE); // Hide for undefined statuses
                            break;
                    }
                }
            }
        }
    }
}

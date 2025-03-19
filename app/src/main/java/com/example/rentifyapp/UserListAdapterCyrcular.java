package com.example.rentifyapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserListAdapterCyrcular extends RecyclerView.Adapter<UserListAdapterCyrcular.UserViewHolder>{

    private List<User> userList;
    private UserListAdapterCyrcular.OnItemClickListener listener;

    // Interface for item click events
    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    // Constructor to initialize the item list and click listener
    public UserListAdapterCyrcular(List<User> userList, OnItemClickListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserListAdapterCyrcular.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_list, parent, false);
        return new UserListAdapterCyrcular.UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserListAdapterCyrcular.UserViewHolder holder, int position) {
        // Bind data to each item in RecyclerView
        User user = userList.get(position);
        holder.bind(user, listener);
    }


    public int getItemCount() {
        // Return total item count in RecyclerView
        return userList.size();
    }
    // ViewHolder class for RecyclerView items
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView firstName;
        TextView lastName;
        TextView userEmail;
        TextView userType;
        TextView userStatus;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            firstName = itemView.findViewById(R.id.userFirstName);
            lastName = itemView.findViewById(R.id.userLastName);
            userEmail = itemView.findViewById(R.id.userEmailAdd);
            userType = itemView.findViewById(R.id.userAccountType);

        }

        public void bind(final User user, final UserListAdapterCyrcular.OnItemClickListener listener) {
            firstName.setText(user.getFirstname());
            lastName.setText(user.getLastname());
            userEmail.setText(user.getEmail());
            userType.setText(user.getUserType());
            // Set item click listener
            itemView.setOnClickListener(v -> listener.onItemClick(user));
        }
    }
}

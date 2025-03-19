package com.example.rentifyapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categoryList;
    //private Context context;
    //private ItemAdapter.OnItemClickListener itemClickListener;


    // Interface for item click events on categories
    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    private OnCategoryClickListener categoryClickListener;

    // Constructor to initialize the category list and click listener
    public CategoryAdapter(List<Category> categoryList, OnCategoryClickListener categoryClickListener) {

        this.categoryList = categoryList;
        this.categoryClickListener = categoryClickListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each category item in RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_category_list, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        // Bind data to each category in RecyclerView
        Category category = categoryList.get(position);
        holder.bind(category, categoryClickListener);

//        // Set up the nested RecyclerView for items within each category
//        ItemAdapter itemAdapter = new ItemAdapter(category.getItems(), itemClickListener);
//        holder.recyclerViewItems.setLayoutManager(new LinearLayoutManager(context));
//        holder.recyclerViewItems.setAdapter(itemAdapter);
    }

    @Override
    public int getItemCount() {
        // Return total category count in RecyclerView
        return categoryList.size();
    }

    // ViewHolder class for category items with a nested RecyclerView for items
    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCategoryName;
        TextView textViewCategoryDescription;
        RecyclerView recyclerViewCategory;

        public CategoryViewHolder(@NonNull View categoryView) {
            super(categoryView);
            textViewCategoryName = categoryView.findViewById(R.id.textViewCategoryName);
            textViewCategoryDescription = categoryView.findViewById(R.id.textViewCategoryDes);
            recyclerViewCategory = categoryView.findViewById(R.id.recycler_view_category);
        }

        public void bind(final Category category, final OnCategoryClickListener categoryClickListener) {
            textViewCategoryName.setText(category.getCategoryName());
            textViewCategoryDescription.setText(category.getCategoryDescription());

            // Set category click listener
            itemView.setOnClickListener(v -> categoryClickListener.onCategoryClick(category));
        }
    }
}

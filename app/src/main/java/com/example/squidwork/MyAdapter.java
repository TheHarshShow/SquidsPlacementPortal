package com.example.squidwork;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private ArrayList<JobPosting> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView companyNameTextView;
        public TextView jobTitleTextView;
        public TextView jobDescriptionTextView;

        public MyViewHolder(View v) {
            super(v);
            companyNameTextView = v.findViewById(R.id.company_name);
            jobTitleTextView = v.findViewById(R.id.job_title);
            jobDescriptionTextView = v.findViewById(R.id.job_description);
        }
    }



    // Provide a suitable constructor (depends on the kind of dataset)

    @RequiresApi(api = Build.VERSION_CODES.N)
    public MyAdapter(ArrayList<JobPosting> jobs) {

        this.mDataset = jobs;
        this.notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view


        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comany_page1_cell, null, false);

        MyViewHolder vh = new MyViewHolder(v);
//        System.out.println("HODOR");
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.companyNameTextView.setText(mDataset.get(position).companyName);
        holder.jobTitleTextView.setText(mDataset.get(position).jobTitle);
        holder.jobDescriptionTextView.setText(mDataset.get(position).jobDescripion);

    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {

        return mDataset.size();
    }
}
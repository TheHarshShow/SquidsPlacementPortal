package com.example.squidwork;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;


//CCD page 1

public class MyAdapter3 extends RecyclerView.Adapter<MyAdapter3.MyViewHolder> {

    private ArrayList<JobPostingCCD> mDataset;
    private MyAdapter3.OnNoteListener mOnNoteListener;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case

        public TextView companyNameTextView;
        public TextView jobTitleTextView;
        public TextView jobDescriptionTextView;
        public TextView approvalStatusTextView;
        MyAdapter3.OnNoteListener onNoteListener;



        public MyViewHolder(View v, final OnNoteListener listener) {
            super(v);
            companyNameTextView = v.findViewById(R.id.company_name_text_view);
            jobTitleTextView = v.findViewById(R.id.job_title_text_view);
            jobDescriptionTextView = v.findViewById(R.id.job_description_text_view);
            approvalStatusTextView = v.findViewById(R.id.approval_status);

            this.onNoteListener=listener;

            itemView.setOnClickListener(this);



        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }



    // Provide a suitable constructor (depends on the kind of dataset)

    @RequiresApi(api = Build.VERSION_CODES.N)
    public MyAdapter3(ArrayList<JobPostingCCD> jobs, OnNoteListener setOnItemClickListener) {

        this.mOnNoteListener = setOnItemClickListener;
        this.mDataset = jobs;
        this.notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter3.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view


        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ccd_page1_cell, null, false);

        MyViewHolder vh = new MyViewHolder(v, mOnNoteListener);
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
        holder.approvalStatusTextView.setText(mDataset.get(position).approvalStatus);

    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {

        return mDataset.size();
    }


    public interface OnNoteListener{
        void onNoteClick(int position);
    }
}
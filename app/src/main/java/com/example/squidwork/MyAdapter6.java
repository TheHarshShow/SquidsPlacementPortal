package com.example.squidwork;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter6 extends RecyclerView.Adapter<MyAdapter6.MyViewHolder>  {

    private ArrayList<ApplicationToLoad> mDataset;
    private MyAdapter6.OnNoteListener mOnNoteListener;

    public interface OnNoteListener{
        void onNoteClick(int position);
    }



//    private MyAdapter.OnItemClickListener mListener;

//    public interface OnItemClickListener {
//
//        void onDeleteClick(int position);
//    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private static final String TAG = "MyAdapter6 view holder";
        // each data item is just a string in this case

        public TextView studentNameTextView;
        public TextView studentEmailTextView;
        public TextView jobTitleTextView;
        public TextView companyNameTextView;
        public TextView companyEmailTextView;
        public TextView statusTextView;
        OnNoteListener onNoteListener;



        public MyViewHolder(View v, MyAdapter6.OnNoteListener onNoteListener) {
            super(v);
            this.studentNameTextView = v.findViewById(R.id.student_name_text_view);
            this.studentEmailTextView = v.findViewById(R.id.student_email_text_view);
            this.jobTitleTextView = v.findViewById(R.id.job_title_text_view);
            this.companyNameTextView = v.findViewById(R.id.company_name_text_view);
            this.companyEmailTextView = v.findViewById(R.id.company_email_text_view);
            this.statusTextView = v.findViewById(R.id.status_text_view);
            this.onNoteListener = onNoteListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "click heard");
            onNoteListener.onNoteClick(getAdapterPosition());
        }

    }



    // Provide a suitable constructor (depends on the kind of dataset)

    @RequiresApi(api = Build.VERSION_CODES.N)
    public MyAdapter6(ArrayList<ApplicationToLoad> applications, MyAdapter6.OnNoteListener onNoteListener) {

        this.mDataset = applications;
        this.mOnNoteListener = onNoteListener;
        this.notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter6.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        // create a new view


        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_page2_cell, null, false);

        MyAdapter6.MyViewHolder vh = new MyAdapter6.MyViewHolder(v, mOnNoteListener);
//        System.out.println("HODOR");
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyAdapter6.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.studentNameTextView.setText(mDataset.get(position).studentName);
        holder.studentEmailTextView.setText(mDataset.get(position).studentEmail);
        holder.companyNameTextView.setText(mDataset.get(position).companyName);
        holder.companyEmailTextView.setText(mDataset.get(position).companyEmail);
        holder.jobTitleTextView.setText(mDataset.get(position).jobTitle);
        holder.statusTextView.setText("Status: "+mDataset.get(position).approvalStatus);

    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {

        return mDataset.size();
    }


}

package com.example.squidwork;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

class JobPostingCCD implements Serializable {

    String companyName;
    String jobTitle;
    String jobDescripion;
    String email;
    String approvalStatus;
    Long timestamp;
    String url;

    public JobPostingCCD(String a, String b, String c, Long d, String e, String app, String f){
        this.approvalStatus = app;
        this.companyName = a;
        this.jobTitle = b;
        this.jobDescripion = c;
        this.timestamp = d;
        this.email = e;
        this.url = f;
    }


}

public class ccdPage1 extends Fragment implements MyAdapter3.OnNoteListener {

    private RecyclerView applicationsRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter<MyAdapter3.MyViewHolder> mAdapter;
    private ArrayList<JobPostingCCD> jobs = new ArrayList<JobPostingCCD>();
    private String TAG = "ccdPage1";

    public ccdPage1() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        Log.d(TAG, "view created");

        View v=inflater.inflate(R.layout.fragment_ccdpage1, container, false);


        applicationsRecyclerView = (RecyclerView) v.findViewById(R.id.applications_recycler_view);
        applicationsRecyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getActivity(), 1);

        applicationsRecyclerView.setLayoutManager(layoutManager);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        final FirebaseUser currentUser = mAuth.getCurrentUser();
        mAdapter = new MyAdapter3(jobs, this);


        applicationsRecyclerView.setAdapter(mAdapter);

        db.collection("posts").whereEqualTo("approvalStatus","Waiting").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e!=null){

                    Log.d(TAG, "listen:error", e);
                    return;

                }
                System.out.println("TADADAAA");


                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                    switch (documentChange.getType()) {
                        case ADDED:{

                            Map docData = new HashMap();
                            docData = documentChange.getDocument().getData();
                            System.out.println("ADDDD "+docData);

                            JobPostingCCD job = new JobPostingCCD(docData.get("companyName").toString(), docData.get("jobTitle").toString(), docData.get("jobDescription").toString(), (Long) docData.get("timeStamp"),docData.get("companyEmail").toString(),docData.get("approvalStatus").toString(), docData.get("brochureURL").toString());

                            jobs.add(job);
                            jobs.sort(new Comparator<JobPostingCCD>() {
                                @Override
                                public int compare(JobPostingCCD o1, JobPostingCCD o2) {
                                    return o2.timestamp.compareTo(o1.timestamp);
                                }
                            });

                            mAdapter.notifyDataSetChanged();

                            break;}
                        case MODIFIED:{

                            Map docData = new HashMap();
                            docData = documentChange.getDocument().getData();

                            String ID=documentChange.getDocument().getId();

                            for(JobPostingCCD job:jobs){


                                if(ID.equals(job.email+"-"+job.timestamp.toString())){

                                    job.companyName = docData.get("companyName").toString();
                                    job.jobTitle = docData.get("jobTitle").toString();
                                    job.jobDescripion = docData.get("jobDescription").toString();
                                    job.timestamp = (Long) docData.get("timeStamp");
                                    job.email = docData.get("companyEmail").toString();
                                    job.approvalStatus = docData.get("approvalStatus").toString();
                                    job.url = docData.get("brochureURL").toString();
                                    break;

                                }




                            }
                            jobs.sort(new Comparator<JobPostingCCD>() {
                                @Override
                                public int compare(JobPostingCCD o1, JobPostingCCD o2) {
                                    return o2.timestamp.compareTo(o1.timestamp);
                                }
                            });

                            mAdapter.notifyDataSetChanged();


                            break;}
                        case REMOVED:{

                            Map docData = new HashMap();
                            docData = documentChange.getDocument().getData();
                            String ID=documentChange.getDocument().getId();

                            for(JobPostingCCD job:jobs){


                                if(ID.equals(job.email+"-"+job.timestamp.toString())){

                                    jobs.remove(job);
                                    break;

                                }




                            }
                            jobs.sort(new Comparator<JobPostingCCD>() {
                                @Override
                                public int compare(JobPostingCCD o1, JobPostingCCD o2) {
                                    return o2.timestamp.compareTo(o1.timestamp);
                                }
                            });
                            mAdapter.notifyDataSetChanged();

                            break;}
                    }
                }

            }
        });

        Button addPostingButton = (Button) v.findViewById(R.id.add_posting_button);

        addPostingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("NENENE");
                startActivity(new Intent(ccdPage1.this.getActivity(), Posting_add_through_ccd.class));

            }
        });



        return v;
    }

    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("863011320660-r32ro0lja4pjlu758sakd2ek25oio8fl.apps.googleusercontent.com").requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);


        FirebaseUser currentUser = mAuth.getCurrentUser();
    }


    @Override
    public void onNoteClick(int position) {

        JobPostingCCD job = jobs.get(position);

        Intent intent = new Intent(getActivity(), ShowJobToCCD.class);
        intent.putExtra("job to show", job);

        startActivity(intent);

//        return;

    }
}

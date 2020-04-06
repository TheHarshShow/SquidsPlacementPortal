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

class ApplicationToLoad implements Serializable {

    String studentName;
    String studentEmail;
    String studentBio;
    String skills;
    String companyEmail;
    String companyName;
    String jobTitle;
    String approvalStatus;
    String id;
    String url;

    Long timestamp;

    public ApplicationToLoad(String a, String b, String c, String d, String e, String f, String g, String h, String i, Long j, String z){

        this.studentName = a;
        this.studentEmail = b;
        this.studentBio = c;
        this.skills = d;
        this.companyEmail = e;
        this.companyName = f;
        this.jobTitle = g;
        this.approvalStatus = h;
        this.id = i;
        this.timestamp = j;
        this.url = z;


    }


}

public class StudentPage2 extends Fragment implements MyAdapter6.OnNoteListener{


    private RecyclerView applicationsRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<ApplicationToLoad> applications = new ArrayList<ApplicationToLoad>();
    private String TAG = "studentPage2";

    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private FirebaseUser currentUser;

    public StudentPage2() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_studentpage2, container, false);

        applicationsRecyclerView = (RecyclerView) v.findViewById(R.id.applications_recycler_view);
        applicationsRecyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getActivity(), 1);
        applicationsRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new MyAdapter6(applications, this);

        currentUser = mAuth.getCurrentUser();

        applicationsRecyclerView.setAdapter(mAdapter);

        db.collection("application").whereEqualTo("candidateEmail",currentUser.getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if(e!=null){
                    Log.d(TAG, "listen:error", e);
                    return;

                }

                for(DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()){

                    switch (documentChange.getType()){

                        case ADDED:{

                            Map docData = new HashMap();

                            docData = documentChange.getDocument().getData();

                            if(docData.containsKey("cvURL")){

                                ApplicationToLoad application = new ApplicationToLoad(docData.get("Name").toString(), docData.get("candidateEmail").toString(),
                                        docData.get("bio").toString(), docData.get("skills").toString(), docData.get("companyEmail").toString(),
                                        docData.get("companyName").toString(), docData.get("jobTitle").toString(), docData.get("approvalStatus").toString(),
                                        documentChange.getDocument().getId(), (Long) docData.get("timeStamp"), docData.get("cvURL").toString());
                                applications.add(application);

                            } else {

                                ApplicationToLoad application = new ApplicationToLoad(docData.get("Name").toString(), docData.get("candidateEmail").toString(),
                                        docData.get("bio").toString(), docData.get("skills").toString(), docData.get("companyEmail").toString(),
                                        docData.get("companyName").toString(), docData.get("jobTitle").toString(), docData.get("approvalStatus").toString(),
                                        documentChange.getDocument().getId(), (Long) docData.get("timeStamp"), "blank");
                                applications.add(application);

                            }


                            applications.sort(new Comparator<ApplicationToLoad>() {
                                @Override
                                public int compare(ApplicationToLoad o1, ApplicationToLoad o2) {
                                    return o2.timestamp.compareTo(o1.timestamp);
                                }
                            });

                            mAdapter.notifyDataSetChanged();


                            break;
                        }
                        case MODIFIED:{

                            Map docData = new HashMap();
                            docData = documentChange.getDocument().getData();
                            String ID = documentChange.getDocument().getId();

                            for(ApplicationToLoad sac: applications){

                                if(sac.id.equals(ID)){

                                    sac.id = ID;
                                    sac.timestamp = (Long) docData.get("timeStamp");
                                    sac.studentName = docData.get("Name").toString();
                                    sac.studentBio = docData.get("bio").toString();
                                    sac.studentEmail = docData.get("candidateEmail").toString();
                                    sac.companyName = docData.get("companyName").toString();
                                    sac.companyEmail = docData.get("companyEmail").toString();
                                    sac.jobTitle = docData.get("jobTitle").toString();
                                    sac.skills = docData.get("skills").toString();
                                    sac.approvalStatus = docData.get("approvalStatus").toString();
                                    break;


                                }


                            }

                            mAdapter.notifyDataSetChanged();

                            break;
                        }
                        case REMOVED:{


                            String ID = documentChange.getDocument().getId();

                            for(ApplicationToLoad sac: applications){

                                if(sac.id.equals(ID)){

                                    applications.remove(sac);
                                    break;


                                }


                            }

                            mAdapter.notifyDataSetChanged();

                            break;
                        }

                    }


                }


            }
        });


        Button signOutButton =  (Button) v.findViewById(R.id.sign_out_button2);

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make your toast here
                mAuth.signOut();
                mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(),
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getContext(), "Signed out successfully", Toast.LENGTH_SHORT).show();
                                getActivity().finish();

                            }

                        }

                );
            }
        });

        return v;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("863011320660-r32ro0lja4pjlu758sakd2ek25oio8fl.apps.googleusercontent.com").requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);


    }

    @Override
    public void onNoteClick(int position) {
        Log.d(TAG, "onNoteClick: clicked." + position);

        Intent intent = new Intent(getActivity(),StudentShowApplicationActivity.class);
        intent.putExtra("selected job", applications.get(position));
        startActivity(intent);

        return;
    }
}

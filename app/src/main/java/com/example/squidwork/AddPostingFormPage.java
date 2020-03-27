package com.example.squidwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddPostingFormPage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_posting_form_page);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        Button postButton = (Button) findViewById(R.id.post_button);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameTextView = (EditText) findViewById(R.id.name_text);
                EditText jobTextView = (EditText) findViewById(R.id.job_text);
                EditText descriptionTextView = (EditText) findViewById(R.id.description_text);

                if (nameTextView.getText().toString().equals("")){

                    Toast.makeText(AddPostingFormPage.this, "Fill Company Name Field", Toast.LENGTH_SHORT).show();

                } else if(jobTextView.getText().toString().equals("")){

                    Toast.makeText(AddPostingFormPage.this, "Fill Job Title Field", Toast.LENGTH_SHORT).show();
                } else {

                    final CollectionReference postsRef = db.collection("posts");
                    Map postDesc = new HashMap();

                    final Long tsLong = System.currentTimeMillis();

                    postDesc.put("companyName", nameTextView.getText().toString());
                    postDesc.put("jobTitle", jobTextView.getText().toString());
                    postDesc.put("jobDescription", descriptionTextView.getText().toString());
                    postDesc.put("timeStamp", tsLong);
                    postDesc.put("companyEmail", currentUser.getEmail());



                    postsRef.document(currentUser.getEmail()+"-"+tsLong).set(postDesc).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(AddPostingFormPage.this, "Added Post Successfully.", Toast.LENGTH_SHORT).show();
                                finish();

                            } else {

                                Toast.makeText(AddPostingFormPage.this, "Could not add post due to some error.", Toast.LENGTH_SHORT).show();

                            }


                        }
                    });




                }

            }
        });

    }

    public static class CompanyPage extends AppCompatActivity {

        private TabLayout tabLayout;
        private ViewPager viewPager;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_company_page);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);


            viewPager = (ViewPager) findViewById(R.id.viewpager);
            addTabs(viewPager);

            tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);

        }
        private void addTabs(ViewPager viewPager) {
            CompanyPage.ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
            adapter.addFrag(new CompanyPage1(), "Company Page 1");
            adapter.addFrag(new CompanyPage2(), "Company Page 2");
            viewPager.setAdapter(adapter);
        }

        class ViewPagerAdapter extends FragmentPagerAdapter {
            private final List<Fragment> mFragmentList = new ArrayList<>();
            private final List<String> mFragmentTitleList = new ArrayList<>();

            public ViewPagerAdapter(FragmentManager manager) {
                super(manager);
            }

            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }

            public void addFrag(Fragment fragment, String title) {
                mFragmentList.add(fragment);
                mFragmentTitleList.add(title);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentTitleList.get(position);
            }
        }


    }
}


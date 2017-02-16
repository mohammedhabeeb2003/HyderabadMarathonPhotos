package com.pingala.hyderabadmarathonphotos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pingala.hyderabadmarathonphotos.Fragments.SlidingSearchResultsFragment;
import com.pingala.hyderabadmarathonphotos.TinyDB.TinyDB;

import java.util.ArrayList;

public class SplashScreen extends AppCompatActivity {

    ArrayList<String> memberList;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
        setContentView(R.layout.activity_splash_screen);
        progressBar = (ProgressBar)findViewById(R.id.pb_main);
        new MemberIdTask().execute();
    }

    class MemberIdTask extends AsyncTask<Void,Void,Void> {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef;
        TinyDB tydb;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setProgress(0);
        }

        @Override
        protected Void doInBackground(Void... params) {
            myRef = database.getReferenceFromUrl("https://marathonphotos-b4348.firebaseio.com/MemberList");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    memberList = new ArrayList();
                    for(DataSnapshot data: dataSnapshot.getChildren()){

                        String members = data.child("memberId").getValue(String.class);
                        memberList.add(members);
                    }
                    if(!memberList.isEmpty()) {
                        progressBar.setMax(memberList.size());
                        Intent i = new Intent(SplashScreen.this,MainActivity.class);
                        i.putStringArrayListExtra("memberId",memberList);
                        startActivity(i);
                        finish();

                    }
                    Log.e("MemberId",""+memberList.size());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


        }
    }
}

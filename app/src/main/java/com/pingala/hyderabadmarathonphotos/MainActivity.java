package com.pingala.hyderabadmarathonphotos;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pingala.hyderabadmarathonphotos.Fragments.BaseFragment;
import com.pingala.hyderabadmarathonphotos.Fragments.SlidingSearchResultsFragment;
import com.pingala.hyderabadmarathonphotos.TinyDB.TinyDB;
import com.pingala.hyderabadmarathonphotos.models.MemberSuggestion;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements BaseFragment.BaseFragmentCallbacks {

    private final String TAG = "MainActivity";
    ArrayList<String> memberId;
    private DrawerLayout mDrawerLayout;
    public ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
         /*  NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
*/
        new MemberIdTask().execute();

    }

    @Override
    public void onAttachSearchViewToDrawer(FloatingSearchView searchView) {
        searchView.attachNavigationDrawerToMenuButton(mDrawerLayout);
    }
    @Override
    public void onBackPressed() {
        List fragments = getSupportFragmentManager().getFragments();
        BaseFragment currentFragment = (BaseFragment) fragments.get(fragments.size() - 1);

        if (!currentFragment.onActivityBackPress()) {
            super.onBackPressed();
        }
    }
    /*@Override
   public boolean onNavigationItemSelected(MenuItem menuItem) {
       mDrawerLayout.closeDrawer(GravityCompat.START);
       switch (menuItem.getItemId()) {
           case R.id.sliding_list_example:
               showFragment(new SlidingSearchResultsExampleFragment());
               return true;
           case R.id.sliding_search_bar_example:
               showFragment(new SlidingSearchViewExampleFragment());
               return true;
           case R.id.scrolling_search_bar_example:
               showFragment(new ScrollingSearchExampleFragment());
               return true;
           default:
               return true;
       }
   }
*/
    public void showFragment(Fragment fragment,ArrayList<String> memberId) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("memberId", memberId);
        Log.e("MemberIdMain",""+memberId);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment).commit();
           fragment.setArguments(bundle);
    }

    class MemberIdTask extends AsyncTask<Void,Void,Void>{

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef;
        TinyDB tydb;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Loading...");
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            myRef = database.getReferenceFromUrl("https://marathonphotos-b4348.firebaseio.com/MemberList");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                      memberId = new ArrayList();
                    for(DataSnapshot data: dataSnapshot.getChildren()){

                       String members = data.child("memberId").getValue(String.class);
                        memberId.add(members);
                    }
                    if(!memberId.isEmpty()) {
                        pd.dismiss();
                        showFragment(new SlidingSearchResultsFragment(), memberId);
                    }
                    Log.e("MemberId",""+memberId.size());
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

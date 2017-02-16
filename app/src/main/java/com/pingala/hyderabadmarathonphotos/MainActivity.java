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
        memberId = getIntent().getStringArrayListExtra("memberId");
         /*  NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
*/
        if(!memberId.isEmpty()){
            showFragment(new SlidingSearchResultsFragment(),memberId);
        }

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
             finish();
         }
        else{
             if (currentFragment.onActivityBackPress()) {
                 finish();
             }
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


}

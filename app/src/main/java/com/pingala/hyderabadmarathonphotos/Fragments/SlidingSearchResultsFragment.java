package com.pingala.hyderabadmarathonphotos.Fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pingala.hyderabadmarathonphotos.Data.DataHelper;
import com.pingala.hyderabadmarathonphotos.R;
import com.pingala.hyderabadmarathonphotos.adapter.StaggeredGridLayoutAdapter;
import com.pingala.hyderabadmarathonphotos.models.Images;
import com.pingala.hyderabadmarathonphotos.models.MemberSuggestion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SlidingSearchResultsFragment extends BaseFragment {
    private final String TAG = "BlankFragment";
    StaggeredGridLayoutAdapter adapter;
    StaggeredGridLayoutManager mLayoutManager;
    public static final long FIND_SUGGESTION_SIMULATED_DELAY = 250;
    private FloatingSearchView mSearchView;
    private ColorDrawable mDimDrawable;
    private RecyclerView recyclerViewMain;
    /*  private SearchResultsListAdapter mSearchResultsAdapter;*/
    private static final long ANIM_DURATION = 350;
    private String mLastQuery = "";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    ArrayList<String> memberId;
    public SlidingSearchResultsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        memberId = getArguments().getStringArrayList("memberId");
        /*Log.e("MemberId",""+memberId.size());*/
        Log.e("MemberId",""+memberId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sliding_search_results, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSearchView = (FloatingSearchView) view.findViewById(R.id.floating_search_view);
        recyclerViewMain = (RecyclerView) view.findViewById(R.id.search_results_list);
        mDimDrawable = new ColorDrawable(Color.BLACK);

        setupFloatingSearch();
       if(adapter==null){

           new ImageMainTask().execute("MainPhotos");
       }
    }

    private void setupFloatingSearch() {
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {

                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.clearSuggestions();
                } else {

                    //this shows the top left circular progress
                    //you can call it where ever you want, but
                    //it makes sense to do it when loading something in
                    //the background.
                    mSearchView.showProgress();
                }
                List<MemberSuggestion> memberList = new ArrayList();
                for (int i = 0; i <memberId.size() ; i++) {
                    memberList.add(new MemberSuggestion(memberId.get(i).toString()));
                }


                Log.e("MemberList",""+memberList.get(0).getBody());
              if(!memberList.isEmpty()){
                  DataHelper.findSuggestions(getActivity(), memberList, newQuery, 3, 350, new DataHelper.OnFindSuggestionsListener() {
                      @Override
                      public void onResults(List<MemberSuggestion> results) {
                          Log.e("Test", "" + results);
                          mSearchView.swapSuggestions(results);
                          mSearchView.hideProgress();
                      }
                  });
              }
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                String membersId = "Photos/"+searchSuggestion.getBody();
                if(adapter!=null){
                    adapter.clean();
                    new ImageMainTask().execute(membersId);
                }
            }

            @Override
            public void onSearchAction(String currentQuery) {
                mLastQuery = currentQuery;
                Log.e("currentQuery", "" + mLastQuery);
            }
        });

        mSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
                int headerHeight = getResources().getDimensionPixelOffset(R.dimen.sliding_search_view_header_height);
                ObjectAnimator anim = ObjectAnimator.ofFloat(mSearchView, "translationY",
                        headerHeight, 0);
                anim.setDuration(350);
                fadeDimBackground(0, 150, null);
                anim.addListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //show suggestions when search bar gains focus (typically history suggestions)
                       /* mSearchView.swapSuggestions(DataHelper.getHistory(getActivity(), 3));*/

                    }
                });
                anim.start();

                Log.d(TAG, "onFocus()");
            }

            @Override
            public void onFocusCleared() {
                int headerHeight = getResources().getDimensionPixelOffset(R.dimen.sliding_search_view_header_height);
                ObjectAnimator anim = ObjectAnimator.ofFloat(mSearchView, "translationY",
                        0, headerHeight);
                anim.setDuration(350);
                anim.start();
                fadeDimBackground(150, 0, null);

                //set the title of the bar so that when focus is returned a new query begins
                mSearchView.setSearchBarTitle(mLastQuery);

                //you can also set setSearchText(...) to make keep the query there when not focused and when focus returns
                //mSearchView.setSearchText(searchSuggestion.getBody());

                Log.d(TAG, "onFocusCleared()");
            }
        });
    }

    @Override
    public boolean onActivityBackPress() {
        //if mSearchView.setSearchFocused(false) causes the focused search
        //to close, then we don't want to close the activity. if mSearchView.setSearchFocused(false)
        //returns false, we know that the search was already closed so the call didn't change the focus
        //state and it makes sense to call supper onBackPressed() and close the activity
        if (!mSearchView.setSearchFocused(false)) {
            return false;
        }
        return true;
    }
    private void fadeDimBackground(int from, int to, Animator.AnimatorListener listener) {
        ValueAnimator anim = ValueAnimator.ofInt(from, to);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                int value = (Integer) animation.getAnimatedValue();
                mDimDrawable.setAlpha(value);
            }
        });
        if(listener != null) {
            anim.addListener(listener);
        }
        anim.setDuration(ANIM_DURATION);
        anim.start();
    }

    class ImageMainTask extends AsyncTask<String,String,String>{
        ArrayList<Images> mImageList;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            myRef = database.getReferenceFromUrl("https://marathonphotos-b4348.firebaseio.com/"+url);
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mImageList = new ArrayList();
                    for(DataSnapshot data: dataSnapshot.getChildren()){
                        String thumbNail = data.child("thumbnailUrl").getValue(String.class);
                        String imgUrl = data.child("url").getValue(String.class);
                        Log.e("FullImage",""+imgUrl);
                        Images im = new Images();
                        im.setImg(thumbNail);
                        im.setImgUrl(imgUrl);
                        mImageList.add(im);
                    }
                     try {
                         recyclerViewMain.setHasFixedSize(true);
                         mLayoutManager = new StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL);
                         recyclerViewMain.setLayoutManager(mLayoutManager);
                         adapter = new StaggeredGridLayoutAdapter(getActivity(), mImageList);
                         recyclerViewMain.setAdapter(adapter);
                         adapter.notifyDataSetChanged();
                         Log.e("List", "" + mImageList.size() + mImageList.get(0).getImgUrl());
                     }
                     catch (Exception e){

                     }
                    /*pd.dismiss();*/
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

}

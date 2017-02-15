package com.pingala.hyderabadmarathonphotos.Data;

/**
 * Copyright (C) 2015 Ari C.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.util.Log;
import android.widget.Filter;

import com.pingala.hyderabadmarathonphotos.models.MemberSuggestion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DataHelper {

    Context context;
    public static List<MemberSuggestion> myList;
    public interface OnFindSuggestionsListener{
        void onResults(List<MemberSuggestion> results);

    }




    public static void findSuggestions(Context context, final List<MemberSuggestion> mySuggestion, String query, final int limit, final long simulatedDelay, final OnFindSuggestionsListener listener){

        new Filter(){

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                try {
                    Thread.sleep(simulatedDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                    for (MemberSuggestion MemberSuggestion : mySuggestion) {
                        MemberSuggestion.setIsHistory(false);
                }                Log.e("aa",""+mySuggestion.size());
                List<MemberSuggestion> suggestionList = new ArrayList<>();
                if (!(constraint == null || constraint.length() == 0)) {

                    for (MemberSuggestion suggestion : mySuggestion) {
                        if (suggestion.getBody().toUpperCase()
                                .startsWith(constraint.toString().toUpperCase())) {

                            suggestionList.add(suggestion);
                            if (limit != -1 && suggestionList.size() == limit) {
                                break;
                            }
                        }
                    }
                }
                FilterResults results = new FilterResults();
                Collections.sort(suggestionList, new Comparator<MemberSuggestion>() {
                    @Override
                    public int compare(MemberSuggestion lhs, MemberSuggestion rhs) {
                        return lhs.getIsHistory() ? -1 : 0;
                    }
                });
                results.values = suggestionList;
                results.count = suggestionList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                  if(listener!=null){
                      listener.onResults((List<MemberSuggestion>)  results.values);
                  }
            }
        }.filter(query);


    }



}
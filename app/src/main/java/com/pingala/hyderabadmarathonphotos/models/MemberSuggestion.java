package com.pingala.hyderabadmarathonphotos.models;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

import java.util.ArrayList;

/**
 * Created by Habeeb on 2/13/2017.
 */

public class MemberSuggestion implements SearchSuggestion {

        private String mMemberName;
        private  ArrayList aa;
        private boolean mIsHistory = false;

        public MemberSuggestion(String suggestion) {
            this.mMemberName = suggestion.toLowerCase();
        }
        public MemberSuggestion(ArrayList aa){
            this.aa = aa;

        }

        public MemberSuggestion(Parcel source) {
            this.mMemberName = source.readString();
            this.mIsHistory = source.readInt() != 0;
        }

        public void setIsHistory(boolean isHistory) {
            this.mIsHistory = isHistory;
        }

        public boolean getIsHistory() {
            return this.mIsHistory;
        }

        @Override
        public String getBody() {
            return mMemberName;
        }

        public static final Creator<com.pingala.hyderabadmarathonphotos.models.MemberSuggestion> CREATOR = new Creator<com.pingala.hyderabadmarathonphotos.models.MemberSuggestion>() {
            @Override
            public com.pingala.hyderabadmarathonphotos.models.MemberSuggestion createFromParcel(Parcel in) {
                return new com.pingala.hyderabadmarathonphotos.models.MemberSuggestion(in);
            }

            @Override
            public com.pingala.hyderabadmarathonphotos.models.MemberSuggestion[] newArray(int size) {
                return new com.pingala.hyderabadmarathonphotos.models.MemberSuggestion[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mMemberName);
            dest.writeInt(mIsHistory ? 1 : 0);
        }
    }


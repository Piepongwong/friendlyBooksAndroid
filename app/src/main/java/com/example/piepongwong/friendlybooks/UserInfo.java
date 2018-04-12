package com.example.piepongwong.friendlybooks;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;


public class UserInfo extends Fragment {
    int position = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null){
            // Get back arguments
            if(getArguments() != null) {
                position = getArguments().getInt("position", 0);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.user_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView usernameView = (TextView) view.findViewById(R.id.username);
        TextView firstnameView = (TextView) view.findViewById(R.id.firstname);
        TextView lastnameView = (TextView) view.findViewById(R.id.lastname);
        TextView emailView = (TextView) view.findViewById(R.id.email);

        SharedPreferences sharedPref = this.getActivity().getSharedPreferences(getString(R.string.userData), Context.MODE_PRIVATE);

        usernameView.setText(sharedPref.getString(getString(R.string.username), "not available"));
        firstnameView.setText(sharedPref.getString(getString(R.string.firstname), "not available"));
        lastnameView.setText(sharedPref.getString(getString(R.string.lastname), "not available"));
        emailView.setText(sharedPref.getString(getString(R.string.email), "not available"));
    }
}

package com.jljsoluctions.rocketfun.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jljsoluctions.rocketfun.Class.Preferences;
import com.jljsoluctions.rocketfun.R;
import com.jljsoluctions.rocketfun.Class.Useful;

/**
 * Created by jever on 20/09/2017.
 */

public class LeaderboardsFragment extends Fragment {

    private WebView webViewLeaderboards;
    private View rootView;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    public LeaderboardsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_leaderboards, container, false);
        rootView.setVisibility(View.VISIBLE);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.tab_leaderboards_swipe_refresh_layout);

        Preferences preferencias = new Preferences(this.getActivity().getApplicationContext());
        boolean onlyWifiConnected = false;
        try {
            onlyWifiConnected = preferencias.getUpdateTrackerWifiConnected();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (onlyWifiConnected)
            if (!Useful.checkWifiConected(this.getActivity()))
                return rootView;


        DatabaseReference mDataBase;
        mDataBase = FirebaseDatabase.getInstance().getReference().child("Tracker");
        mDataBase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                updateLeaderBoards(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                updateLeaderBoards(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    private void updateLeaderBoards(final String url){
        webViewLeaderboards = (WebView) rootView.findViewById(R.id.webViewLeaderboards);
        webViewLeaderboards.getSettings().setJavaScriptEnabled(true);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webViewLeaderboards.loadUrl(url);
            }
        });
        webViewLeaderboards.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        webViewLeaderboards.loadUrl(url);
        mSwipeRefreshLayout.setRefreshing(true);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

}
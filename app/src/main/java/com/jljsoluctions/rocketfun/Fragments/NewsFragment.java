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
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jljsoluctions.rocketfun.Class.Preferences;
import com.jljsoluctions.rocketfun.R;
import com.jljsoluctions.rocketfun.Util;

/**
 * Created by jever on 20/09/2017.
 */

public class NewsFragment extends Fragment {


    private WebView webViewNews;
    private View rootView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public NewsFragment() {
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

        rootView = inflater.inflate(R.layout.fragment_news, container, false);
        rootView.setVisibility(View.VISIBLE);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.tab_news_swipe_refresh_layout);

        Preferences preferencias = new Preferences(this.getActivity().getApplicationContext());
        boolean onlyWifiConnected = false;
        try {
            onlyWifiConnected = preferencias.getUpdateNewsWifiConnected();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (onlyWifiConnected)
            if (!Util.checkWifiConected(this.getActivity()))
                return rootView;

        DatabaseReference mDataBase;
        mDataBase = FirebaseDatabase.getInstance().getReference().child("News");
        mDataBase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                updateNews(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                updateNews(dataSnapshot.getValue(String.class));
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

    private void updateNews(final String url){
        webViewNews = (WebView) rootView.findViewById(R.id.webViewNews);
        webViewNews.getSettings().setJavaScriptEnabled(true);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webViewNews.loadUrl(url);
            }
        });
        webViewNews.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        webViewNews.loadUrl(url);
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

}
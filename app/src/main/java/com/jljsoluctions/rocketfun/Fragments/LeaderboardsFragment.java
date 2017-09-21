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

import com.jljsoluctions.rocketfun.R;

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

        webViewLeaderboards = (WebView) rootView.findViewById(R.id.webViewLeaderboards);
        webViewLeaderboards.getSettings().setJavaScriptEnabled(true);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webViewLeaderboards.loadUrl("https://rocketleague.tracker.network/leaderboards/all");
            }
        });
        webViewLeaderboards.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        webViewLeaderboards.loadUrl("https://rocketleague.tracker.network/leaderboards/all");
        mSwipeRefreshLayout.setRefreshing(true);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

}
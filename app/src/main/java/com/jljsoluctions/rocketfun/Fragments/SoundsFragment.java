package com.jljsoluctions.rocketfun.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jljsoluctions.rocketfun.Adapters.ExpandableAdapterSounds;
import com.jljsoluctions.rocketfun.GroupSound;
import com.jljsoluctions.rocketfun.MainActivity;
import com.jljsoluctions.rocketfun.R;
import com.jljsoluctions.rocketfun.Sound;
import com.jljsoluctions.rocketfun.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.jljsoluctions.rocketfun.Util.APP_STORAGE_PATCH;

/**
 * Created by jever on 20/09/2017.
 */

public class SoundsFragment extends Fragment {



    private ExpandableListView mlvwSongs;
    private List<GroupSound> mlistDataHeader;
    private HashMap<String, List<Sound>> mlistDataChild;
    private ProgressBar mProgressBar;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private DatabaseReference mDataBase;
    private BaseExpandableListAdapter mlistAdapter;
    private View rootView;




    public SoundsFragment() {
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

        rootView = inflater.inflate(R.layout.fragment_sounds, container, false);
        rootView.setVisibility(View.VISIBLE);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReferenceFromUrl("gs://rocket-fun.appspot.com/");

        mlistDataHeader = new ArrayList<GroupSound>();
        mlistDataChild = new HashMap<String, List<Sound>>();
        loading(true);
        if (Util.checkWritePermission(this.getActivity()) && Util.checkReadPermission(this.getActivity())) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        prepareListData();
                    } catch (Exception e) {
                        Toast.makeText(SoundsFragment.this.getActivity(), e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }).start();
        }


        mlvwSongs = (ExpandableListView) rootView.findViewById(R.id.lvwSongs);

        //prepareListData();
        mlistAdapter = new ExpandableAdapterSounds(this.getActivity(), mlistDataHeader, mlistDataChild);
        mlvwSongs.setAdapter(mlistAdapter);


        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    private void updateAdapter() {
        SoundsFragment.this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mlistAdapter = new ExpandableAdapterSounds(SoundsFragment.this.getActivity(), mlistDataHeader, mlistDataChild);
                mlvwSongs.setAdapter(mlistAdapter);
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void removeSound(DataSnapshot dataSnapshot) {

        List<Sound> sounds = mlistDataChild.get(dataSnapshot.child("GroupDescription").getValue(String.class));
        if (sounds != null) {
            for (Sound sound : sounds) {
                if (sound.getId() == Long.parseLong(dataSnapshot.child("id").getValue(String.class))) {
                    sounds.remove(sound);
                    break;
                }
            }
        }
        for (GroupSound groupName : mlistDataHeader) {
            sounds = mlistDataChild.get(groupName.getGroupTitle());
            if (sounds != null) {
                if (sounds.size() == 0) {
                    mlistDataHeader.remove(groupName);
                    break;
                }
            }
        }
        mlistAdapter.notifyDataSetChanged();
    }

    private void addSound(DataSnapshot dataSnapshot) {
        boolean existsGroup = false;
        for (GroupSound groupName : mlistDataHeader) {
            if (groupName.getGroupTitle().equalsIgnoreCase(dataSnapshot.child("GroupDescription").getValue(String.class))) {
                existsGroup = true;
                if (Boolean.parseBoolean(dataSnapshot.child("newGroupSound").getValue(String.class)))
                    groupName.setNewGroupSound(true);
                break;
            }
        }
        if (!existsGroup)
            mlistDataHeader.add(new GroupSound(dataSnapshot.child("GroupDescription").getValue(String.class), Boolean.parseBoolean(dataSnapshot.child("newGroupSound").getValue(String.class))));
        List<Sound> sounds = mlistDataChild.get(dataSnapshot.child("GroupDescription").getValue(String.class));
        if (sounds == null)
            sounds = new ArrayList<Sound>();
        final String imageName = dataSnapshot.child("ImageName").getValue(String.class);
        final String soundName = dataSnapshot.child("SoundName").getValue(String.class);
        String imagePath = APP_STORAGE_PATCH + "/" + imageName;
        String soundPath = APP_STORAGE_PATCH + "/" + soundName;

        //Image downlaod
        if (!Util.fileExists(APP_STORAGE_PATCH + "/" + imageName)) {
            mStorageRef = mStorage.getReferenceFromUrl(dataSnapshot.child("ImageURL").getValue(String.class));
            new File(APP_STORAGE_PATCH).mkdirs();
            File imageFile = new File(APP_STORAGE_PATCH, imageName);
            Util.firebaseDownloadFile(this.getActivity(),mStorageRef, imageFile);
        }


        Sound newSound = new Sound(dataSnapshot.child("SoundDescription").getValue(String.class), soundName, Uri.parse(imagePath), Uri.parse(soundPath), Long.parseLong(dataSnapshot.child("id").getValue(String.class)),mStorage.getReferenceFromUrl(dataSnapshot.child("SoundURL").getValue(String.class)));
        boolean existsSound = false;
        for (Sound item : sounds) {
            if (item.getSoundTitle().equals(newSound.getSoundTitle()))
                existsSound = true;
        }
        if (!existsSound)
            sounds.add(newSound);


        mlistDataChild.put(dataSnapshot.child("GroupDescription").getValue(String.class), sounds); // Header, Child data
        mlistAdapter.notifyDataSetChanged();

    }

    private void loading(final boolean progressVisible) {
        try {
            SoundsFragment.this.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (progressVisible)
                        mProgressBar.setVisibility(View.VISIBLE);
                    else
                        mProgressBar.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            Toast.makeText(SoundsFragment.this.getActivity(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void prepareListData() {

        mDataBase = FirebaseDatabase.getInstance().getReference().child("Sounds");
        mDataBase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                loading(true);
                addSound(dataSnapshot);
                loading(false);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                loading(true);
                removeSound(dataSnapshot);
                addSound(dataSnapshot);
                loading(false);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                loading(true);
                removeSound(dataSnapshot);
                loading(false);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



}
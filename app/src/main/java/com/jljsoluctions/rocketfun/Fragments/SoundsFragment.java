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

/**
 * Created by jever on 20/09/2017.
 */

public class SoundsFragment extends Fragment {

    public String mAppURI;
    public static int CURRENT_DOWNLOADS;
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
        mAppURI = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RocketFun";
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReferenceFromUrl("gs://rocket-fun.appspot.com/");

        mlistDataHeader = new ArrayList<GroupSound>();
        mlistDataChild = new HashMap<String, List<Sound>>();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
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
        updateAdapter();
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
        String imagePath = mAppURI + "/" + imageName;
        String soundPath = mAppURI + "/" + soundName;

        if (!Util.fileExists(mAppURI + "/" + imageName)) {
            mStorageRef = mStorage.getReferenceFromUrl(dataSnapshot.child("ImageURL").getValue(String.class));
            new File(mAppURI).mkdirs();
            File imageFile = new File(mAppURI, imageName);
            downloadFile(mStorageRef, imageFile);
        }
        if (!Util.fileExists(mAppURI + "/" + dataSnapshot.child("SoundName").getValue(String.class))) {
            mStorageRef = mStorage.getReferenceFromUrl(dataSnapshot.child("SoundURL").getValue(String.class));
            new File(mAppURI).mkdirs();
            File soundFile = new File(mAppURI, soundName);
            downloadFile(mStorageRef, soundFile);
        }
        Sound newSound = new Sound(dataSnapshot.child("SoundDescription").getValue(String.class), Uri.parse(imagePath), Uri.parse(soundPath), Long.parseLong(dataSnapshot.child("id").getValue(String.class)));
        boolean existsSound = false;
        for (Sound item : sounds) {
            if (item.getSoundTitle().equals(newSound.getSoundTitle()))
                existsSound = true;
        }
        if (!existsSound)
            sounds.add(newSound);


        mlistDataChild.put(dataSnapshot.child("GroupDescription").getValue(String.class), sounds); // Header, Child data
        updateAdapter();
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

    public void downloadFile(final StorageReference storageRef, final File file) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (CURRENT_DOWNLOADS >= 5) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    CURRENT_DOWNLOADS++;
                    storageRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            CURRENT_DOWNLOADS--;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            CURRENT_DOWNLOADS--;
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(SoundsFragment.this.getActivity(), e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }

}
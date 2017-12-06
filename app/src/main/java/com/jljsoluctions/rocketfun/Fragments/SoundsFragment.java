package com.jljsoluctions.rocketfun.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jljsoluctions.rocketfun.Adapters.SoundsAdapter;
import com.jljsoluctions.rocketfun.Entities.SoundGroup;
import com.jljsoluctions.rocketfun.R;
import com.jljsoluctions.rocketfun.Entities.Sound;
import com.jljsoluctions.rocketfun.Class.Useful;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.jljsoluctions.rocketfun.Class.Useful.APP_STORAGE_PATCH;

/**
 * Created by jever on 20/09/2017.
 */

public class SoundsFragment extends Fragment {

    private RecyclerView recyclerView;
    private SoundsAdapter adapter;
    private ExpandableListView mlvwSongs;
    //private List<SoundGroup> mlistDataHeader;
    //private HashMap<String, List<Sound>> mlistDataChild;
    private ProgressBar mProgressBar;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private DatabaseReference mDataBase;
    //private BaseExpandableListAdapter mlistAdapter;
    private List<SoundGroup> mlistGroup;
    private List<SoundGroup> mlistGroupSearch;
    private View rootView;
    private SearchView mSearchView;


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

        ViewGroup root = rootView.findViewById(R.id.fragment_root);
        //TransitionManager.beginDelayedTransition(root, new AutoTransition());
        //mlistDataHeader = new ArrayList<SoundGroup>();
        // mlistDataChild = new HashMap<String, List<Sound>>();
        loading(true);
        if (Useful.checkStorageWritePermission(this.getActivity()) && Useful.checkStorageReadPermission(this.getActivity())) {
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


        //mlvwSongs = (ExpandableListView) rootView.findViewById(R.id.lvwSongs);

        //prepareListData();
        // mlistAdapter = new ExpandableAdapterSounds(this.getActivity(), mlistDataHeader, mlistDataChild);
        //mlvwSongs.setAdapter(mlistAdapter);


        mlistGroup = new ArrayList<SoundGroup>();

        recyclerView = rootView.findViewById(R.id.recyclerViewSoundsGroup);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        adapter = new SoundsAdapter(this.getActivity(), mlistGroup);
        recyclerView.setAdapter(adapter);


        return rootView;
    }

    private void search(String imput) {
        mlistGroupSearch = new ArrayList<SoundGroup>();
        for (SoundGroup soundGroup : mlistGroup)
            mlistGroupSearch.add(soundGroup.getClone());

        for (SoundGroup soundGroup : mlistGroupSearch) {
            Iterator<Sound> i = soundGroup.getSoundItemList().iterator();
            while (i.hasNext()) {
                Sound sound = i.next();
                if (!sound.getSoundTitle().toLowerCase().contains(imput.toLowerCase()) && !soundGroup.getGroupTitle().toLowerCase().contains(imput.toLowerCase()))
                    i.remove();
            }
        }

        Iterator<SoundGroup> i = mlistGroupSearch.iterator();
        while (i.hasNext()) {
            SoundGroup soundGroup = i.next();
            if (soundGroup.getSoundItemList().size() == 0)
                i.remove();
        }

        updateAdapter(mlistGroupSearch);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_sounds, menu);
        super.onCreateOptionsMenu(menu, inflater);

        mSearchView = (SearchView) menu.findItem(R.id.action_srv).getActionView();
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setQueryHint("Name of group or sound");
        //mSearchView.setFocusable(true);
        // mSearchView.setIconified(false);
        //mSearchView.requestFocusFromTouch();

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        updateAdapter(mlistGroup);
                    }
                }).start();
                return false;
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String imput) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        search(imput);
                    }
                }).start();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty())
                    updateAdapter(mlistGroup);
                return false;
            }
        });


    }


    private void updateAdapter(final List<SoundGroup> soundGroups) {
        SoundsFragment.this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter = new SoundsAdapter(SoundsFragment.this.getActivity(), soundGroups);
                recyclerView.setAdapter(adapter);
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private List<Sound> searchSoundsInGroup(String groupDescription) {
        for (SoundGroup itemGroup : mlistGroup) {
            if (itemGroup.getGroupTitle().equals(groupDescription))
                return itemGroup.getSoundItemList();

        }
        return null;
    }

    private void setSoundsInGroup(String groupDescription, List<Sound> sounds) {
        for (SoundGroup itemGroup : mlistGroup) {
            if (itemGroup.getGroupTitle().equals(groupDescription)) {
                itemGroup.setSoundItemList(sounds);
                return;
            }
        }

    }


    private void removeSound(DataSnapshot dataSnapshot) {

        List<Sound> sounds = searchSoundsInGroup(dataSnapshot.child("GroupDescription").getValue(String.class));
        if (sounds != null) {
            for (Sound sound : sounds) {
                if (sound.getId() == Long.parseLong(dataSnapshot.child("id").getValue(String.class))) {
                    sounds.remove(sound);
                    break;
                }
            }
        }
        for (SoundGroup groupName : mlistGroup) {
            sounds = searchSoundsInGroup(groupName.getGroupTitle());
            if (sounds != null) {
                if (sounds.size() == 0) {
                    mlistGroup.remove(groupName);
                    break;
                }
            }
        }
        //mlistAdapter.notifyDataSetChanged();
        //adapter.notifyDataSetChanged();
        updateAdapter(mlistGroup);
    }

    private void addSound(DataSnapshot dataSnapshot) {
        boolean existsGroup = false;
        for (SoundGroup groupName : mlistGroup) {
            if (groupName.getGroupTitle().equalsIgnoreCase(dataSnapshot.child("GroupDescription").getValue(String.class))) {
                existsGroup = true;
                if (Boolean.parseBoolean(dataSnapshot.child("newGroupSound").getValue(String.class)))
                    groupName.setNewGroupSound(true);
                break;
            }
        }
        if (!existsGroup)
            mlistGroup.add(new SoundGroup(dataSnapshot.child("GroupDescription").getValue(String.class), Boolean.parseBoolean(dataSnapshot.child("newGroupSound").getValue(String.class))));
        //  mlistDataHeader.add(new SoundGroup(dataSnapshot.child("GroupDescription").getValue(String.class), Boolean.parseBoolean(dataSnapshot.child("newGroupSound").getValue(String.class))));


        List<Sound> sounds = searchSoundsInGroup(dataSnapshot.child("GroupDescription").getValue(String.class));
        if (sounds == null)
            sounds = new ArrayList<Sound>();
        final String imageName = dataSnapshot.child("ImageName").getValue(String.class);
        final String soundName = dataSnapshot.child("SoundName").getValue(String.class);
        String imagePath = APP_STORAGE_PATCH + "/" + imageName;
        String soundPath = APP_STORAGE_PATCH + "/" + soundName;

        //Image downlaod
        if (!Useful.fileExists(APP_STORAGE_PATCH + "/" + imageName)) {
            mStorageRef = mStorage.getReferenceFromUrl(dataSnapshot.child("ImageURL").getValue(String.class));
            new File(APP_STORAGE_PATCH).mkdirs();
            File imageFile = new File(APP_STORAGE_PATCH, imageName);
            Useful.firebaseDownloadFile(this.getActivity(), mStorageRef, imageFile);
        }


        Sound newSound = new Sound(dataSnapshot.child("SoundDescription").getValue(String.class), soundName, Uri.parse(imagePath), Uri.parse(soundPath), Long.parseLong(dataSnapshot.child("id").getValue(String.class)), mStorage.getReferenceFromUrl(dataSnapshot.child("SoundURL").getValue(String.class)));
        boolean existsSound = false;
        for (Sound item : sounds) {
            if (item.getSoundTitle().equals(newSound.getSoundTitle()))
                existsSound = true;
        }
        if (!existsSound)
            sounds.add(newSound);

        setSoundsInGroup(dataSnapshot.child("GroupDescription").getValue(String.class), sounds);
        //mlistAdapter.notifyDataSetChanged();
        //adapter.notifyDataSetChanged();
        updateAdapter(mlistGroup);

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
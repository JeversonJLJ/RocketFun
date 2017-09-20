package com.jljsoluctions.rocketfun;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public String mAppURI;
    public static int CURRENT_DOWNLOADS;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private StorageReference mStorageRef;
    private FirebaseStorage mStorage;
    private DatabaseReference mDataBase;
    private BaseExpandableListAdapter mlistAdapter;
    private ExpandableListView mlvwSongs;
    private List<GroupSound> mlistDataHeader;
    private HashMap<String, List<Sound>> mlistDataChild;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mAppURI = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RocketFun";
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReferenceFromUrl("gs://rocket-fun.appspot.com/");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    // Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    //Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            //Log.w(TAG, "signInAnonymously", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

        mlistDataHeader = new ArrayList<GroupSound>();
        mlistDataChild = new HashMap<String, List<Sound>>();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        loading(true);
        if (Util.checkWritePermission(this) && Util.checkReadPermission(this)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        prepareListData();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }).start();
        }

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest;
        if (BuildConfig.DEBUG) {
            adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        } else {
            adRequest = new AdRequest.Builder().build();
        }

        adView.loadAd(adRequest);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //navigationView.setBackgroundResource(R.color.white);
        int colorPrimaryDark = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            colorPrimaryDark = getResources().getColor(R.color.colorPrimary, null);
        } else {
            colorPrimaryDark = getResources().getColor(R.color.colorPrimary);
        }

        navigationView.setBackgroundColor(colorPrimaryDark);
        navigationView.setItemTextColor(ColorStateList.valueOf(Color.WHITE));
        navigationView.setItemIconTintList(ColorStateList.valueOf(Color.WHITE));

        mlvwSongs = (ExpandableListView) findViewById(R.id.lvwSongs);

        prepareListData();
        mlistAdapter = new ExpandableAdapterSounds(this, mlistDataHeader, mlistDataChild);
        mlvwSongs.setAdapter(mlistAdapter);


    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void saveFile(String filePath, String fileName, byte[] file) {
        new File(filePath).mkdirs();
        File soundFile = new File(filePath, fileName);
        try {
            FileOutputStream outputStream = new FileOutputStream(soundFile);
            outputStream.write(file);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    private void updateAdapter() {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mlistAdapter = new ExpandableAdapterSounds(MainActivity.this, mlistDataHeader, mlistDataChild);
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
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (progressVisible)
                        mProgressBar.setVisibility(View.VISIBLE);
                    else
                        mProgressBar.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, e.getMessage(),
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
                    Toast.makeText(MainActivity.this, e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (id == R.id.nav_sounds) {

        } else if (id == R.id.nav_email) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"jljsoluctions@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.subject_email));
            intent.putExtra(Intent.EXTRA_TEXT, "");
            try {
                startActivity(Intent.createChooser(intent, "Send Email"));
            } catch (Exception e) {

            }
        }
        return true;
    }
}
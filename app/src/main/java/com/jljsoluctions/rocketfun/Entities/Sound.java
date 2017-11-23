package com.jljsoluctions.rocketfun.Entities;

import android.net.Uri;

import com.google.firebase.storage.StorageReference;

/**
 * Created by jever on 18/03/2017.
 */

public class Sound {

    private String soundTitle;
    private String soundName;
    private StorageReference firebaseStorageRef;
    private boolean playing;
    private Uri imageUri;
    private Uri soundUri;
    private long id;


    public Sound(String soundTitle,String soundName, Uri imageUri, Uri soundUri, long id, StorageReference firebaseStorageRef){
        this.soundTitle = soundTitle;
        this.soundName = soundName;
        this.imageUri = imageUri;
        this.soundUri = soundUri;
        this.id = id;
        this.firebaseStorageRef = firebaseStorageRef;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public Uri getSoundUri() {
        return soundUri;
    }

    public void setSoundUri(Uri soundUri) {
        this.soundUri = soundUri;
    }

    public String getSoundTitle() {
        return soundTitle;
    }

    public void setSoundTitle(String soundTitle) {
        this.soundTitle = soundTitle;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public String getSoundName() {
        return soundName;
    }

    public void setSoundName(String soundName) {
        this.soundName = soundName;
    }

    public StorageReference getFirebaseStorageRef() {
        return firebaseStorageRef;
    }

    public void setFirebaseStorageRef(StorageReference firebaseStorageRef) {
        this.firebaseStorageRef = firebaseStorageRef;
    }
}

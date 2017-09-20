package com.jljsoluctions.rocketfun;

import android.net.Uri;

/**
 * Created by jever on 18/03/2017.
 */

public class Sound {

    private String soundTitle;
    private boolean playing;
    private Uri imageUri;
    private Uri soundUri;
    private long id;


    public Sound(String soundTitle, Uri imageUri, Uri soundUri, long id){
        this.soundTitle = soundTitle;
        this.imageUri = imageUri;
        this.soundUri = soundUri;
        this.id = id;
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
}

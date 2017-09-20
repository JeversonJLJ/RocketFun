package com.jljsoluctions.rocketfun;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.support.v4.app.ActivityCompat.requestPermissions;


/**
 * Created by Programacao on 19/01/2017.
 */

public class AdapterSounds extends ArrayAdapter<Sound> {
    private LayoutInflater mInflater;
    private List<Sound> soundList;
    private MediaPlayer currentSound;
    private ImageButton currentPlayingButton;
    private Activity activity;
    private InterstitialAd interstitialAd;
    private AdRequest adRequest;
    private static final int MY_READ_EXTERNAL_STORAGE = 1;
    private static final int MY_WRITE_EXTERNAL_STORAGE = 2;

    public AdapterSounds(Activity activity, int textViewResourceId,
                         List<Sound> soundList) {
        super(activity, textViewResourceId, soundList);
        this.soundList = new ArrayList<Sound>();
        this.soundList.addAll(soundList);
        this.activity = activity;
        mInflater = LayoutInflater.from(activity);

        if (BuildConfig.DEBUG) {
            adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        } else {
            adRequest = new AdRequest.Builder().build();
        }

        interstitialAd = new InterstitialAd(activity);
        interstitialAd.setAdUnitId("ca-app-pub-3845382773372401/8243182574");
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {

            }

            @Override
            public void onAdLoaded() {
                interstitialAd.show();
            }
        });

        interstitialAd.loadAd(adRequest);


    }

    private class ViewHolder {
        TextView soundTitle;
        ImageView soundImage;
        ImageButton playStop;
        ImageButton setSound;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AdapterSounds.ViewHolder holder = null;

        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.sound_list_item, null);

            holder = new AdapterSounds.ViewHolder();
            holder.soundTitle = (TextView) convertView.findViewById(R.id.sound_title);
            holder.soundImage = (ImageView) convertView.findViewById(R.id.sound_image);
            holder.playStop = (ImageButton) convertView.findViewById(R.id.play_stop);
            holder.setSound = (ImageButton) convertView.findViewById(R.id.set);
            convertView.setTag(holder);

            holder.playStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Sound clickedSound = (Sound) v.getTag();
                    if (!clickedSound.isPlaying()) {
                        if (currentSound != null) {
                            currentSound.reset();
                            currentPlayingButton.setImageResource(R.drawable.play);
                        }
                        currentPlayingButton = (ImageButton) v;
                        currentPlayingButton.setImageResource(R.drawable.stop);
                       //currentSound = MediaPlayer.create(mInflater.getContext(), clickedSound.getSoundRes());
                        currentSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                if (currentPlayingButton != null)
                                    currentPlayingButton.setImageResource(R.drawable.play);
                            }
                        });
                        clickedSound.setPlaying(true);
                        currentSound.start();

                    } else {
                        currentPlayingButton = (ImageButton) v;
                        currentPlayingButton.setImageResource(R.drawable.play);
                        clickedSound.setPlaying(false);
                        currentSound.reset();
                    }
                }
            });

            holder.setSound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    final Sound clickedSound = (Sound) v.getTag();

                    /** Instantiating PopupMenu class */
                    PopupMenu popup = new PopupMenu(activity, v);

                    /** Adding menu items to the popumenu */
                    popup.getMenuInflater().inflate(R.menu.set_sound, popup.getMenu());

                    /** Defining menu item click listener for the popup menu */
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (BuildConfig.DEBUG) {
                                adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
                            } else {
                                adRequest = new AdRequest.Builder().build();
                            }
                            interstitialAd.loadAd(adRequest);

                            if (interstitialAd.isLoaded()) {
                                interstitialAd.show();
                            } else {
                                int id = item.getItemId();

                                if (id == R.id.set_ringtone) {
                                    setRingtone(clickedSound, true, false, false);
                                } else if (id == R.id.set_alarm) {
                                    setRingtone(clickedSound, false, false, true);
                                } else if (id == R.id.set_notification) {
                                    setRingtone(clickedSound, false, true, false);
                                }
                            }
                            return true;
                        }
                    });

                    /** Showing the popup menu */
                    popup.show();
                }


            });

        } else {
            holder = (AdapterSounds.ViewHolder) convertView.getTag();
        }

        Sound sound = soundList.get(position);
        holder.soundTitle.setText(sound.getSoundTitle());
        //holder.soundImage.setImageResource(sound.getImageRes());
        holder.playStop.setTag(sound);
        holder.setSound.setTag(sound);

        return convertView;

    }

    /* File saveRingtone(Sound sound) {
        String ringtoneURI = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES) + "/" + getContext().getString(R.string.app_name);
        new File(ringtoneURI).mkdirs();
        File soundFile = new File(ringtoneURI, sound.getSoundTitle());
        InputStream inputStream = getContext().getResources().openRawResource(sound.getSoundRes());
        try {
            FileOutputStream outputStream = new FileOutputStream(soundFile);
            byte[] buffer = new byte[1024];
            while (true) {
                int read = inputStream.read(buffer);
                if (read > 0) {
                    outputStream.write(buffer, 0, read);
                } else {
                    inputStream.close();
                    outputStream.close();
                    return soundFile;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return null;
    }*/

    public boolean checkWritePermission() {

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_WRITE_EXTERNAL_STORAGE);
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(activity)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:com.jljsoluctions.rocketfun"));
                activity.startActivity(intent);
            }
        }
        return true;
    }

    public boolean checkReadPermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_READ_EXTERNAL_STORAGE);
            return false;
        }
        return true;
    }

    private void setRingtone(Sound sound, boolean ringtone, boolean notification, boolean alarm) {
        if (checkWritePermission() && checkReadPermission()) {
            File soundFile = new File(sound.getSoundUri().toString(), sound.getSoundTitle());
            if (soundFile != null) {
                ContentValues values = new ContentValues();
                values.put("_data", soundFile.getAbsolutePath());
                values.put("title", sound.getSoundTitle());
                values.put("mime_type", "audio/mp3");
                values.put("_size", Long.valueOf(soundFile.length()));
                values.put("artist", sound.getSoundTitle());
                values.put("is_ringtone", ringtone);
                values.put("is_notification", notification);
                values.put("is_alarm", alarm);
                Uri uri = MediaStore.Audio.Media.getContentUriForPath(soundFile.getAbsolutePath());
                activity.getContentResolver().delete(uri, "_data=\"" + soundFile.getAbsolutePath() + "\"", null);
                Uri newUri = activity.getContentResolver().insert(uri, values);
                try {
                    if (ringtone) {
                        RingtoneManager.setActualDefaultRingtoneUri(activity, RingtoneManager.TYPE_RINGTONE, newUri);
                    }
                    if (notification) {
                        RingtoneManager.setActualDefaultRingtoneUri(activity, RingtoneManager.TYPE_NOTIFICATION, newUri);
                    }
                    if (alarm) {
                        RingtoneManager.setActualDefaultRingtoneUri(activity, RingtoneManager.TYPE_ALARM, newUri);
                    }
                    if (ringtone || notification || alarm) {
                        // this.mAdsManager.showInterstitial();
                    }
                    Toast.makeText(activity, "Successfully set.", Toast.LENGTH_LONG).show();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }

        }
    }


}


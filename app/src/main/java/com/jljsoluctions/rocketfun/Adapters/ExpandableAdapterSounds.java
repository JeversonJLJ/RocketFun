package com.jljsoluctions.rocketfun.Adapters;

import android.app.Activity;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.jljsoluctions.rocketfun.BuildConfig;
import com.jljsoluctions.rocketfun.Fragments.SoundsFragment;
import com.jljsoluctions.rocketfun.GroupSound;
import com.jljsoluctions.rocketfun.R;
import com.jljsoluctions.rocketfun.Sound;
import com.jljsoluctions.rocketfun.Util;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import static com.jljsoluctions.rocketfun.Util.APP_STORAGE_PATCH;


/**
 * Created by Programacao on 19/01/2017.
 */

public class ExpandableAdapterSounds extends BaseExpandableListAdapter {
    private LayoutInflater mInflater;
    private HashMap<String, List<Sound>> soundList;
    private List<GroupSound> soundGroupList;
    private MediaPlayer currentSound;
    private ImageButton currentPlayingButton;
    private ProgressBar currentProgressBar;
    private Activity activity;
    private InterstitialAd interstitialAd;
    private AdRequest adRequest;
    private boolean downloadingSound = false;


    public ExpandableAdapterSounds(Activity activity, List<GroupSound> soundGroupList, HashMap<String, List<Sound>> soundList) {
        this.soundList = soundList;
        this.soundGroupList = soundGroupList;
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
                //interstitialAd.show();
            }
        });

        interstitialAd.loadAd(adRequest);


    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.soundList.get(this.soundGroupList.get(groupPosition).getGroupTitle())
                .get(childPosititon);

    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.soundList.get(this.soundGroupList.get(groupPosition).getGroupTitle())
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.soundGroupList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.soundGroupList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }


    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        GroupSound groupSound = (GroupSound) getGroup(groupPosition);
        String headerTitle = groupSound.getGroupTitle();
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.activity
                    .getSystemService(activity.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.sound_list_group, null);
        }
        ImageView imgNewGroup = (ImageView) convertView
                .findViewById(R.id.group_image);
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.group_sound_title);
        if (groupSound.isNewGroupSound())
            imgNewGroup.setVisibility(View.VISIBLE);
        else
            imgNewGroup.setVisibility(View.INVISIBLE);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }


    private class ViewHolder {
        TextView soundTitle;
        ImageView soundImage;
        ImageButton playStop;
        ImageButton setSound;
        ProgressBar progressBar;
    }

    private void playSound(Sound clickedSound, View viewHolder){
        if (currentSound != null) {
            currentSound.reset();
            currentPlayingButton.setImageResource(R.drawable.play);
        }
        currentPlayingButton = (ImageButton) viewHolder;
        currentPlayingButton.setImageResource(R.drawable.stop);
        currentSound = MediaPlayer.create(mInflater.getContext(), clickedSound.getSoundUri());
        currentSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (currentPlayingButton != null)
                    currentPlayingButton.setImageResource(R.drawable.play);
            }
        });
        clickedSound.setPlaying(true);
        currentSound.start();
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, final ViewGroup parent) {

        ExpandableAdapterSounds.ViewHolder holder = null;

        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.sound_list_item, null);

            holder = new ExpandableAdapterSounds.ViewHolder();
            holder.soundTitle = (TextView) convertView.findViewById(R.id.sound_title);
            holder.soundImage = (ImageView) convertView.findViewById(R.id.sound_image);
            holder.playStop = (ImageButton) convertView.findViewById(R.id.play_stop);
            holder.setSound = (ImageButton) convertView.findViewById(R.id.set);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
            convertView.setTag(holder);

            final ViewHolder finalHolder = holder;
            holder.playStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View viewHolder) {
                    final Sound clickedSound = (Sound) viewHolder.getTag();
                    if (Util.fileExists(clickedSound.getSoundUri().getPath())) {
                        if (!clickedSound.isPlaying()) {
                            playSound(clickedSound,viewHolder);
                        } else {
                            currentPlayingButton = (ImageButton) viewHolder;
                            currentPlayingButton.setImageResource(R.drawable.play);
                            clickedSound.setPlaying(false);
                            currentSound.reset();
                        }
                    } else {
                        //Sound download
                        if (!downloadingSound) {
                            if (!Util.fileExists(clickedSound.getSoundUri().getPath())) {
                                downloadingSound = true;
                                finalHolder.playStop.setVisibility(View.GONE);
                                finalHolder.progressBar.setVisibility(View.VISIBLE);
                                new File(APP_STORAGE_PATCH).mkdirs();
                                File soundFile = new File(APP_STORAGE_PATCH, clickedSound.getSoundName());


                                Util.asynchronousFirebaseDownloadFile(activity, clickedSound.getFirebaseStorageRef(), soundFile, new Util.OnAsynchronousFirebaseDownloadFile() {
                                    @Override
                                    public void onAsynchronousFirebaseDownloadFile(String filePatch, boolean sucess, Exception e) {
                                        finalHolder.playStop.setVisibility(View.VISIBLE);
                                        finalHolder.progressBar.setVisibility(View.GONE);
                                        downloadingSound = false;
                                        if (sucess) {
                                            playSound(clickedSound,viewHolder);
                                        } else {
                                            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        }
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
            holder = (ExpandableAdapterSounds.ViewHolder) convertView.getTag();
        }


        // new Thread(new Runnable() {
        //    @Override
        //      public void run() {
        Sound sound = (Sound) getChild(groupPosition, childPosition);
        holder.soundTitle.setText(sound.getSoundTitle());


        holder.soundImage.setImageURI(null);
        holder.soundImage.setImageURI(sound.getImageUri());
        holder.playStop.setTag(sound);
        holder.setSound.setTag(sound);
        //       }
        //   }).start();


        return convertView;

    }

   /* private File saveRingtone(Sound sound) {
        String ringtoneURI = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES) + "/" + activity.getString(R.string.app_name);
        new File(ringtoneURI).mkdirs();
        File soundFile = new File(ringtoneURI, sound.getSoundTitle());
        InputStream inputStream = activity.getResources().openRawResource(sound.getSoundRes());
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


    private void setRingtone(Sound sound, boolean ringtone, boolean notification, boolean alarm) {
        if (Util.checkWritePermission(activity) && Util.checkReadPermission(activity)) {
            File soundFile = new File(sound.getSoundUri().toString());
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


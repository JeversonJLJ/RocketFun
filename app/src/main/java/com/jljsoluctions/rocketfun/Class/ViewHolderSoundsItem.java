package com.jljsoluctions.rocketfun.Class;

import android.app.Activity;
import android.content.ContentValues;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.jljsoluctions.rocketfun.BuildConfig;
import com.jljsoluctions.rocketfun.Dialog.Dialog;
import com.jljsoluctions.rocketfun.Entities.Sound;
import com.jljsoluctions.rocketfun.R;

import java.io.File;

import static com.jljsoluctions.rocketfun.Class.Useful.APP_STORAGE_PATCH;

public class ViewHolderSoundsItem extends ChildViewHolder {

    private TextView soundTitle;
    private ImageView soundImage;
    private ImageButton playStop;
    private ImageButton setSound;
    private ProgressBar progressBarPlayStop;
    private ProgressBar progressBarRingtone;
    private Activity activity;
    private InterstitialAd interstitialAd;
    private AdRequest adRequest;
    private static MediaPlayer currentMediaSound;
    private static ImageButton currentPlayingButton;
    private static Sound currentSound;
    private boolean downloadingSound = false;
    private Sound sound;


    public ViewHolderSoundsItem(Activity activity, View itemView) {
        super(itemView);
        this.activity = activity;
        this.soundTitle = itemView.findViewById(R.id.sound_title);
        this.soundImage = itemView.findViewById(R.id.sound_image);
        this.playStop = itemView.findViewById(R.id.play_stop);
        this.setSound = itemView.findViewById(R.id.set);
        this.progressBarPlayStop = itemView.findViewById(R.id.progressBarPlayStop);
        this.progressBarRingtone = itemView.findViewById(R.id.progressBarRingtone);

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


    public void bind(@NonNull Sound sound) {
        soundTitle.setText(sound.getSoundTitle());
        soundImage.setImageURI(null);
        soundImage.setImageURI(sound.getImageUri());
        this.sound = sound;
        playStop.setTag(this);
        setSound.setTag(this);

        playStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final ViewHolderSoundsItem clickedSoundHolder = (ViewHolderSoundsItem) view.getTag();
                final Sound clickedSound = clickedSoundHolder.sound;
                if (Useful.fileExists(clickedSound.getSoundUri().getPath())) {
                    playSound(clickedSound, view);
                } else {
                    //Sound download
                    if (!downloadingSound) {
                        if (!Useful.fileExists(clickedSound.getSoundUri().getPath())) {
                            downloadingSound = true;
                            clickedSoundHolder.playStop.setVisibility(View.INVISIBLE);
                            clickedSoundHolder.progressBarPlayStop.setVisibility(View.VISIBLE);
                            new File(APP_STORAGE_PATCH).mkdirs();
                            File soundFile = new File(APP_STORAGE_PATCH, clickedSound.getSoundName());
                            Useful.asynchronousFirebaseDownloadFile(activity, clickedSound.getFirebaseStorageRef(), soundFile, new Useful.OnAsynchronousFirebaseDownloadFile() {
                                @Override
                                public void onAsynchronousFirebaseDownloadFile(String filePatch, boolean sucess, Exception e) {
                                    clickedSoundHolder.playStop.setVisibility(View.VISIBLE);
                                    clickedSoundHolder.progressBarPlayStop.setVisibility(View.INVISIBLE);
                                    downloadingSound = false;
                                    if (sucess) {
                                        playSound(clickedSound, view);
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

        setSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final ViewHolderSoundsItem clickedSoundHolder = (ViewHolderSoundsItem) v.getTag();
                final Sound clickedSound = clickedSoundHolder.sound;
                if (!Useful.fileExists(clickedSound.getSoundUri().getPath())) {
                    if (!downloadingSound) {
                        if (!Useful.fileExists(clickedSound.getSoundUri().getPath())) {
                            downloadingSound = true;
                            clickedSoundHolder.setSound.setVisibility(View.INVISIBLE);
                            clickedSoundHolder.progressBarRingtone.setVisibility(View.VISIBLE);
                            new File(APP_STORAGE_PATCH).mkdirs();
                            File soundFile = new File(APP_STORAGE_PATCH, clickedSound.getSoundName());
                            Useful.asynchronousFirebaseDownloadFile(activity, clickedSound.getFirebaseStorageRef(), soundFile, new Useful.OnAsynchronousFirebaseDownloadFile() {
                                @Override
                                public void onAsynchronousFirebaseDownloadFile(String filePatch, boolean sucess, Exception e) {
                                    clickedSoundHolder.setSound.setVisibility(View.VISIBLE);
                                    clickedSoundHolder.progressBarRingtone.setVisibility(View.INVISIBLE);
                                    downloadingSound = false;
                                    if (sucess) {
                                        openBottomSheet(v,clickedSound);
                                        //showPopupMenu(v, clickedSound);
                                    } else {
                                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    }
                } else {
                    openBottomSheet(v,clickedSound);
                    //showPopupMenu(v, clickedSound);
                }


            }


        });


    }


    public void openBottomSheet(View v, final Sound clickedSound) {

        View view = activity.getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        TextView txtAlarm = (TextView) view.findViewById(R.id.tv_alarm);
        TextView txtNotification = (TextView) view.findViewById(R.id.tv_notification);
        TextView txtRingtone = (TextView) view.findViewById(R.id.tv_ringtone);

        final android.app.Dialog mBottomSheetDialog = new android.app.Dialog(activity, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();

        txtAlarm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setRingtone(clickedSound, false, false, true);
                mBottomSheetDialog.dismiss();
                loadInterstitialAd();
            }
        });

        txtNotification.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setRingtone(clickedSound, false, true, false);
                mBottomSheetDialog.dismiss();
                loadInterstitialAd();
            }
        });

        txtRingtone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setRingtone(clickedSound, true, false, false);
                mBottomSheetDialog.dismiss();
                loadInterstitialAd();

            }
        });

    }

    private void loadInterstitialAd(){
        if (BuildConfig.DEBUG) {
            adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        } else {
            adRequest = new AdRequest.Builder().build();
        }
        interstitialAd.loadAd(adRequest);
    }

    private void showPopupMenu(View v, final Sound clickedSound) {
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


                int id = item.getItemId();

                if (id == R.id.set_ringtone) {
                    setRingtone(clickedSound, true, false, false);
                } else if (id == R.id.set_alarm) {
                    setRingtone(clickedSound, false, false, true);
                } else if (id == R.id.set_notification) {
                    setRingtone(clickedSound, false, true, false);
                }

                return true;
            }
        });

        /** Showing the popup menu */
        popup.show();
    }


    private void playSound(Sound clickedSound, View view) {
        if (currentMediaSound == null) {
            currentPlayingButton = (ImageButton) view;
            currentPlayingButton.setImageResource(R.drawable.ic_stop);
            currentMediaSound = MediaPlayer.create(activity, clickedSound.getSoundUri());
            currentSound = clickedSound;
            if (currentMediaSound != null) {
                currentMediaSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (currentPlayingButton != null)
                            currentPlayingButton.setImageResource(R.drawable.ic_play);
                        currentMediaSound = null;

                    }
                });
                currentMediaSound.start();
            }

        } else {
            if (currentPlayingButton != null)
                currentPlayingButton.setImageResource(R.drawable.ic_play);
            currentMediaSound.reset();
            currentMediaSound = null;
            if (!currentSound.equals(clickedSound))
                playSound(clickedSound, view);
        }

    }

    private void setRingtone(Sound sound, boolean ringtone, boolean notification, boolean alarm) {
        if (Useful.checkStorageWritePermission(activity) && Useful.checkStorageReadPermission(activity) &&
                Useful.checkWriteSettingsPermission(activity)) {
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
                        Dialog.showDialogMessage(activity, activity.getString(R.string.sound_successful_set_ringtone), new Dialog.OnClickOkDialogMessage() {
                            @Override
                            public void onClickOkDialogMessage() {
                                if (interstitialAd.isLoaded())
                                    interstitialAd.show();
                            }
                        });
                    }
                    if (notification) {
                        RingtoneManager.setActualDefaultRingtoneUri(activity, RingtoneManager.TYPE_NOTIFICATION, newUri);
                        Dialog.showDialogMessage(activity, activity.getString(R.string.sound_successful_set_notification), new Dialog.OnClickOkDialogMessage() {
                            @Override
                            public void onClickOkDialogMessage() {
                                if (interstitialAd.isLoaded())
                                    interstitialAd.show();
                            }
                        });
                    }
                    if (alarm) {
                        RingtoneManager.setActualDefaultRingtoneUri(activity, RingtoneManager.TYPE_ALARM, newUri);
                        Dialog.showDialogMessage(activity, activity.getString(R.string.sound_successful_set_alarm), new Dialog.OnClickOkDialogMessage() {
                            @Override
                            public void onClickOkDialogMessage() {
                                if (interstitialAd.isLoaded())
                                    interstitialAd.show();
                            }
                        });
                    }
                    if (ringtone || notification || alarm) {
                        // this.mAdsManager.showInterstitial();
                    }

                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }

        }
    }

}

package com.jljsoluctions.rocketfun.Adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jljsoluctions.rocketfun.Class.ViewHolderSoundsItem;
import com.jljsoluctions.rocketfun.Entities.Sound;
import com.jljsoluctions.rocketfun.R;

import java.util.List;

/**
 * Created by jever on 30/11/2017.
 */

public class RecyclerAdapterSoundsItem extends RecyclerView.Adapter<ViewHolderSoundsItem> {

    private List<Sound> list;
    private Activity activity;

    public RecyclerAdapterSoundsItem(List<Sound> list, Activity activity) {
        this.list = list;
        this.activity = activity;
    }

    @Override
    public ViewHolderSoundsItem onCreateViewHolder(ViewGroup parent, int viewType) {
        try {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_sound_item, parent, false);
            return new ViewHolderSoundsItem(activity,view);
        }catch (Exception e){
            Toast.makeText(activity,e.getMessage(),Toast.LENGTH_LONG);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final ViewHolderSoundsItem holder, int position) {
        final Sound sound = list.get(position);
/*
        holder.soundTitle.setText(sound.getSoundTitle());
        holder.soundImage.setImageURI(null);
        holder.soundImage.setImageURI(sound.getImageUri());
        holder.playStop.setTag(sound);
        holder.setSound.setTag(sound);

        //if(holder.soundTitle.getParent()!=null)
         //   ((ViewGroup)holder.soundTitle.getParent()).removeView(holder.soundTitle);
*/
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}

package com.jljsoluctions.rocketfun.Adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.transition.AutoTransition;
import android.support.transition.TransitionInflater;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jljsoluctions.rocketfun.Class.ViewHolderSoundsGroup;
import com.jljsoluctions.rocketfun.Entities.Sound;
import com.jljsoluctions.rocketfun.Entities.SoundGroup;
import com.jljsoluctions.rocketfun.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jever on 30/11/2017.
 */

public class RecyclerAdapterSoundsGroup extends RecyclerView.Adapter<ViewHolderSoundsGroup> {

    private List<SoundGroup> list;
    private Activity activity;

    public RecyclerAdapterSoundsGroup(List<SoundGroup> list, Activity activity) {
        this.list = list;
        this.activity = activity;
    }

    @Override
    public ViewHolderSoundsGroup onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_sound_group, parent, false);

        return new ViewHolderSoundsGroup(activity, view);
    }

    @Override
    public void onBindViewHolder(final ViewHolderSoundsGroup holder, int position) {
        final SoundGroup groupItem = list.get(position);

       /* holder.sounds = groupItem.getSoundItemList();
        holder.name.setText(groupItem.getGroupTitle());
        holder.imageView.setImageURI(null);
        holder.imageView.setImageURI(groupItem.getSoundItemList().get(0).getImageUri());
        holder.btnExpand.setTag(holder);
        holder.btnExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewHolderSoundsGroup viewHolderSoundsGroup = (ViewHolderSoundsGroup) view.getTag();
                changeScene(viewHolderSoundsGroup, viewHolderSoundsGroup.sounds);
            }
        });*/


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

/*
    public void changeScene(ViewHolderSoundsGroup holder, List<Sound> sounds) {

        View imageView = holder.imageView;
        ViewGroup.LayoutParams paramsImageView = imageView.getLayoutParams();

        holder.adapter = new RecyclerAdapterSoundsItem(sounds, activity);
        holder.recyclerViewSounds.setAdapter(holder.adapter);

        //TransitionManager.beginDelayedTransition(holder.root, new AutoTransition());
        if (!holder.expanded) {
            paramsImageView.width = (int) activity.getResources().getDimension(R.dimen.image_group_size_small);
            paramsImageView.height = (int) activity.getResources().getDimension(R.dimen.image_group_size_small);
            imageView.setLayoutParams(paramsImageView);
            setTextAppearance(activity, holder.name, android.R.style.TextAppearance_Medium);
            holder.btnExpand.animate().rotation(180).start();
            holder.soundList.setVisibility(View.VISIBLE);
            holder.expanded = true;
        } else {
            paramsImageView.width = (int) activity.getResources().getDimension(R.dimen.image_group_size_normal);
            paramsImageView.height = (int) activity.getResources().getDimension(R.dimen.image_group_size_normal);
            imageView.setLayoutParams(paramsImageView);
            setTextAppearance(activity, holder.name, android.R.style.TextAppearance_Large);
            holder.btnExpand.animate().rotation(0).start();
            holder.soundList.setVisibility(View.INVISIBLE);
            holder.expanded = false;
        }
    }

    public void setTextAppearance(Context context, TextView textView, int resId) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            textView.setTextAppearance(context, resId);
        } else {
            textView.setTextAppearance(resId);
        }
    }
    */
}
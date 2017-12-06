package com.jljsoluctions.rocketfun.Adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.jljsoluctions.rocketfun.Class.ViewHolderSoundsGroup;
import com.jljsoluctions.rocketfun.Class.ViewHolderSoundsItem;
import com.jljsoluctions.rocketfun.Entities.Sound;
import com.jljsoluctions.rocketfun.Entities.SoundGroup;
import com.jljsoluctions.rocketfun.R;

import java.util.List;

/**
 * Created by jever on 30/11/2017.
 */

public class SoundsAdapter extends ExpandableRecyclerAdapter<SoundGroup, Sound, ViewHolderSoundsGroup, ViewHolderSoundsItem> {


    private static final int PARENT = 0;
   // private static final int PARENT_NORMAL = 1;
    private static final int CHILD = 1;
    //private static final int CHILD_NORMAL = 3;


    private List<SoundGroup> list;
    private Activity activity;
    private LayoutInflater mInflater;

    public SoundsAdapter(Activity activity, @NonNull List<SoundGroup> list) {
        super(list);
        this.list = list;
        this.activity = activity;
        mInflater = LayoutInflater.from(activity);
    }


    @UiThread
    @NonNull
    @Override
    public ViewHolderSoundsGroup onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View groupView = mInflater.inflate(R.layout.recycler_sound_group, parentViewGroup, false);
        return new ViewHolderSoundsGroup(activity, groupView);
    }

    @UiThread
    @NonNull
    @Override
    public ViewHolderSoundsItem onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View soundItemView = mInflater.inflate(R.layout.recycler_sound_item, childViewGroup, false);
        return new ViewHolderSoundsItem(activity, soundItemView);
    }


   /* @Override
    public void onBindViewHolder(final ViewHolderSoundsGroup holder, int position) {
        final SoundGroup groupItem = list.get(position);

        holder.sounds = groupItem.getSoundItemList();
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
        });


    }*/


    @UiThread
    @Override
    public void onBindParentViewHolder(@NonNull ViewHolderSoundsGroup recipeViewHolder, int parentPosition, @NonNull SoundGroup soundGroup) {
        recipeViewHolder.bind(soundGroup);
    }

    @UiThread
    @Override
    public void onBindChildViewHolder(@NonNull ViewHolderSoundsItem ingredientViewHolder, int parentPosition, int childPosition, @NonNull Sound sound) {
        ingredientViewHolder.bind(sound);
    }

    @Override
    public int getParentViewType(int parentPosition) {
       /* if (mRecipeList.get(parentPosition).isVegetarian()) {
            return PARENT_VEGETARIAN;
        } else {
            return PARENT_NORMAL;
        }
        */
       return PARENT;
    }

    @Override
    public int getChildViewType(int parentPosition, int childPosition) {
        /*Ingredient ingredient = mRecipeList.get(parentPosition).getIngredient(childPosition);
        if (ingredient.isVegetarian()) {
            return CHILD_VEGETARIAN;
        } else {
            return CHILD_NORMAL;
        }*/
        return CHILD;
    }

    @Override
    public boolean isParentViewType(int viewType) {
        return viewType == PARENT ;
    }





























}
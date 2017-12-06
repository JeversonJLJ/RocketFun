package com.jljsoluctions.rocketfun.Entities;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jever on 08/07/2017.
 */

public class SoundGroup implements Parent<Sound>, Cloneable {
    private String groupTitle;
    private String groupImage;
    private boolean newGroupSound;
    private List<Sound> soundItemList;
    private boolean expanded = false;


    public SoundGroup(String groupTitle, boolean newGroupSound) {
        this.groupTitle = groupTitle;
        this.newGroupSound = newGroupSound;
        this.groupImage = "";
    }

    public SoundGroup(String groupTitle, List<Sound> soundItemList, String groupImage, boolean newGroupSound) {
        this.groupTitle = groupTitle;
        this.newGroupSound = newGroupSound;
        this.soundItemList = soundItemList;
        this.groupImage = groupImage;
    }

    public SoundGroup(String groupTitle, String groupImage, boolean newGroupSound) {
        this.groupTitle = groupTitle;
        this.newGroupSound = newGroupSound;
        this.groupImage = groupImage;
    }

    public SoundGroup getClone() {
        try {
            // call clone in Object.
            SoundGroup soundGroup = (SoundGroup) super.clone();
            soundGroup.setSoundItemList(new ArrayList<Sound>());
            for (Sound sound : this.getSoundItemList())
                soundGroup.getSoundItemList().add(sound.getClone());

            return soundGroup;
        } catch (CloneNotSupportedException e) {
            System.out.println(" Cloning not allowed. ");
            return this;
        }
    }

    @Override
    public List<Sound> getChildList() {
        return soundItemList;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }


    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public boolean isNewGroupSound() {
        return newGroupSound;
    }

    public void setNewGroupSound(boolean newGroupSound) {
        this.newGroupSound = newGroupSound;
    }

    public String getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }

    public List<Sound> getSoundItemList() {
        return soundItemList;
    }

    public void setSoundItemList(List<Sound> soundItemList) {
        this.soundItemList = soundItemList;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}

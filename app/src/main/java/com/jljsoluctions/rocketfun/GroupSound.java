package com.jljsoluctions.rocketfun;


/**
 * Created by jever on 08/07/2017.
 */

public class GroupSound {
    private String groupTitle;
    private boolean newGroupSound;

    public GroupSound(String groupTitle, boolean newGroupSound){
        this.groupTitle = groupTitle;
        this.newGroupSound = newGroupSound;
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
}

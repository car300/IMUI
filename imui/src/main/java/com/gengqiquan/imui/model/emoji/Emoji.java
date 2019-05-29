package com.gengqiquan.imui.model.emoji;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by teambition on 15/8/27.
 */
public class Emoji implements Serializable {


    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    private String filter;
    private Bitmap icon;

}

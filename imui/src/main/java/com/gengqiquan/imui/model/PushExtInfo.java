package com.gengqiquan.imui.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author mazhichao
 * @date 2019/06/14 15:32
 */
public class PushExtInfo implements Parcelable {
    private String identifier;
    private String nick_name;
    private int conversation_type;

    public PushExtInfo(String identifier, String nick_name, int conversation_type) {
        this.identifier = identifier;
        this.nick_name = nick_name;
        this.conversation_type = conversation_type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public int getConversation_type() {
        return conversation_type;
    }

    public void setConversation_type(int conversation_type) {
        this.conversation_type = conversation_type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.identifier);
        dest.writeString(this.nick_name);
        dest.writeInt(this.conversation_type);
    }

    public PushExtInfo() {
    }

    protected PushExtInfo(Parcel in) {
        this.identifier = in.readString();
        this.nick_name = in.readString();
        this.conversation_type = in.readInt();
    }

    public static final Parcelable.Creator<PushExtInfo> CREATOR = new Parcelable.Creator<PushExtInfo>() {
        @Override
        public PushExtInfo createFromParcel(Parcel source) {
            return new PushExtInfo(source);
        }

        @Override
        public PushExtInfo[] newArray(int size) {
            return new PushExtInfo[size];
        }
    };
}

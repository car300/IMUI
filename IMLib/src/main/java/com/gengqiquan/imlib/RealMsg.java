package com.gengqiquan.imlib;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.gengqiquan.imlib.model.CustomElem;
import com.gengqiquan.imlib.model.PreCustomElem;
import com.gengqiquan.imui.help.IMHelp;
import com.gengqiquan.imui.interfaces.IPathListener;
import com.gengqiquan.imui.interfaces.IimMsg;
import com.gengqiquan.imui.model.ImImage;
import com.gengqiquan.imui.model.ImVideo;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMImage;
import com.tencent.imsdk.TIMImageElem;
import com.tencent.imsdk.TIMImageType;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageStatus;
import com.tencent.imsdk.TIMSnapshot;
import com.tencent.imsdk.TIMSoundElem;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.TIMVideo;
import com.tencent.imsdk.TIMVideoElem;
import com.tencent.imsdk.ext.message.TIMMessageExt;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import androidx.annotation.MainThread;

public class RealMsg implements IimMsg {
    TIMMessage timMsg;
    Date time;
    TIMElem elem;
    int timeTag = 0;//0-没有初始化,1-初始化,并且展示时间,2-初始化,不展示时间
    String nickName;//姓名
    boolean isRevoke = false;//是否是撤回消息

    private RealMsg(TIMMessage timMsg, TIMElem elem, Date time) {
        this.timMsg = timMsg;
        this.elem = elem;
        this.time = time;
    }

    public static List<RealMsg> create(TIMMessage timMsg) {

        List<RealMsg> list = new ArrayList<>();
        if (timMsg.status() == TIMMessageStatus.HasDeleted) {
            return list;
        }
        if (timMsg.getElementCount() == 0L) {
            return list;
        }
        for (int i = 0; i < timMsg.getElementCount(); i++) {
            TIMElem elem = timMsg.getElement(i);
            Date time = i == timMsg.getElementCount() - 1 ? new Date(timMsg.timestamp() * 1000) : null;
            RealMsg realMsg = new RealMsg(timMsg, elem, time);
            list.add(realMsg);
        }
        if (timMsg.getElementCount() > 1) {
            Log.e("getElementCount", timMsg.getElementCount() + "");
        }
        return list;
    }

    public static RealMsg decorate(TIMMessage timMsg) {
        TIMElem elem = timMsg.getElement(0);
        Date time = new Date(timMsg.timestamp() * 1000);
        return new RealMsg(timMsg, elem, time);
    }

    @Override
    public String text() {
        if (elem.getType() != TIMElemType.Text) {
            throw new IllegalArgumentException("can not call img() that is not of the type: Text");
        }
        return ((TIMTextElem) elem).getText();
    }

    @Override
    public ImImage img() {
        if (elem.getType() != TIMElemType.Image) {
            throw new IllegalArgumentException("can not call img() that is not of the type: image");
        }
        TIMImageElem timImageElem = ((TIMImageElem) elem);
        if (timImageElem.getImageList().size() == 0) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(timImageElem.getPath(), options);
            int imgWidth = options.outWidth;
            int imgHeight = options.outHeight;
            return new ImImage(timImageElem.getPath(), null, imgWidth, imgHeight);
        }

        String Thumb = null;
        String Original = null;
        for (TIMImage image : timImageElem.getImageList()) {
            if (image.getType() == TIMImageType.Thumb) {
                Thumb = image.getUrl();
                continue;
            }
            if (image.getType() == TIMImageType.Original) {
                Original = image.getUrl();
            }
        }
        TIMImage timImage = timImageElem.getImageList().get(0);
        return new ImImage(Original, Thumb, timImage.getWidth(), timImage.getHeight());
    }

    @NotNull
    @Override
    public ImVideo video() {
        if (elem.getType() != TIMElemType.Video) {
            throw new IllegalArgumentException("can not call img() that is not of the type: Video");
        }
        final TIMVideoElem videoElem = (TIMVideoElem) elem;
        TIMSnapshot snapshot = videoElem.getSnapshotInfo();
        TIMVideo videoInfo = videoElem.getVideoInfo();
        return new ImVideo(videoElem, new ImImage(videoElem.getSnapshotPath(), null, snapshot.getWidth(), snapshot.getHeight()));
    }

    @NotNull
    @Override
    public void sound(@NotNull final IPathListener listener) {
        if (elem.getType() != TIMElemType.Sound) {
            throw new IllegalArgumentException("can not call img() that is not of the type: sound");
        }
        String path = ((TIMSoundElem) elem).getPath();

        if (!path.isEmpty()){
            listener.path(path);
            return;
        }
        final String savePath = IMHelp.getAudioPath()+((TIMSoundElem) elem).getUuid();
        ((TIMSoundElem) elem).getSoundToFile(savePath, null, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                listener.path("");
            }

            @Override
            public void onSuccess() {
                IMHelp.getMsgBuildPolicy().buildAudioMessage(
                        savePath,
                        (int) ((TIMSoundElem) elem).getDuration());
                listener.path(savePath);
            }
        });
    }

    @Override
    public long duration() {
        if (elem.getType() != TIMElemType.Sound) {
            throw new IllegalArgumentException("can not call img() that is not of the type: sound");
        }
        return ((TIMSoundElem) elem).getDuration();
    }

    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat formatYear = new SimpleDateFormat("yyyy年MM月dd日");
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat formatMonth = new SimpleDateFormat("MM-dd HH:mm");
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat formatHours = new SimpleDateFormat("HH:mm");

    @MainThread
    public static String format(Date record) {
        Date now = new Date();
        int days = now.getDay() - record.getDay();

        if (days == 0) {
            return formatHours.format(record);
        }
        if (days == 1) {
            return "昨天 " + formatHours.format(record);
        }
        /*if (days == 2) {
            return "前天 " + formatHours.format(record);
        }*/
        if (days <= 7) {
            Calendar c = Calendar.getInstance();
            c.setTime(record);
            int weekday = c.get(Calendar.DAY_OF_WEEK)-1;
            return "星期" + weekStr[weekday] + " " + formatHours.format(record);
        }
        return formatYear.format(record) + " " + formatHours.format(record);
    }

    final static String[] weekStr = new String[]{"日", "一", "二", "三", "四", "五","六", };

    @NotNull
    @Override
    public String time() {
        if (time != null)
            return format(time);
        return null;
    }

    /**
     * getCustomInt 自定义消息字段（本地有效）  等于-1的情况是这个消息为待发送的一个消息，等于3代表消息发送失败
     * <p>
     * 返回类型直接加上1000 解析对应view时判断是否千位为1可知这个消息为待发送的一个消息
     * 返回-1 代表撤回的消息
     *
     * @author gengqiquan
     * @date 2019-05-09 14:57
     */
    @Override
    public int uiType() {
//        Log.w("im_help",elem.getType().value() + "");
        if (timMsg.status() == TIMMessageStatus.HasRevoked || isRevoke) {
            return -1;
        }

        if (elem.getType() == TIMElemType.Custom) {
            TIMCustomElem customElem = (TIMCustomElem) elem;
            PreCustomElem preCustomElem = PreCustomElem.create(new String(customElem.getData()));
//            customData = CustomElem.create(new String(customElem.getData()));
            int type = 0;
            switch (preCustomElem.getType()) {
                case share:
                    type = 5;
                    break;
                case revoke:
                    type = -1;
                    break;

            }
            if (preCustomElem.getShowType() == PreCustomElem.ShowType.preSend) {
                type = 1000 + type;
            }
            /*if (new TIMMessageExt(timMsg).getCustomInt() == -1) {
                type = 1000 + type;
            }*/
            customData = preCustomElem;
            return type;
        }
        switch (elem.getType()) {
            case Text:
                return 1;
            case Image:
                return 2;
            case Video:
                return 3;
            case Sound:
                return 4;
            default:
                return 0;
        }
    }


    @Override
    public TIMMessage realData() {
        return timMsg;
    }

    @Override
    public boolean isSelf() {
        return timMsg.isSelf();
    }


    @NotNull
    @Override
    public String sender() {
        TIMUserProfile userProfile = timMsg.getSenderProfile(new TIMValueCallBack<TIMUserProfile>() {
            @Override
            public void onError(int i, String s) {
            }

            @Override
            public void onSuccess(TIMUserProfile timUserProfile) {
                //此处返回的信息,可能是异步的,当前没有处理异步的,同步为空就取identify
                if( timUserProfile != null ){
                    timUserProfile.getNickName();
                }
            }
        });

        if ( userProfile == null || userProfile.getNickName() == null || userProfile.getNickName().isEmpty() ){
            return timMsg.getSender();
        }

        return userProfile.getNickName();
    }

    @NotNull
    @Override
    public String identifier() {
        return timMsg.getSender();
    }

    CustomElem customData;

    @NotNull
    @Override
    public Object extra() {
        return customData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RealMsg)) return false;

        RealMsg realMsg = (RealMsg) o;
        if (timMsg == null || realMsg.timMsg == null || elem == null || realMsg.elem == null) {
            return false;
        }
        return timMsg.getMsgUniqueId() == realMsg.timMsg.getMsgUniqueId() || timMsg.getMsgId().equals(realMsg.timMsg.getMsgId());
    }

    @Override
    public int hashCode() {
        int result = timMsg != null ? timMsg.hashCode() : 0;
        result = 31 * result + (elem != null ? elem.hashCode() : 0);
        return result;
    }

    int status = 0;

    public void failure() {
        status = 3;
    }

    public void success() {
        status = 2;
    }

    /**
     * 由于SDK有bug，消息发送失败的时候并不会将状态改为SendFail，所以这里根据自定义字段矫正
     * getCustomInt 自定义消息字段（本地有效） 等于-1的情况是这个消息为待发送的一个消息，等于3代表消息发送失败
     *
     * @author gengqiquan
     * @date 2019-05-15 15:25
     */
    @Override
    public @IimMsg.Companion.SendType
    int status() {
//        if (status > 1) {
//            return status;
//        }

        if (TIMMessageStatus.Sending == timMsg.status()) {
            if (new TIMMessageExt(timMsg).getCustomInt() == 3) {
                status = 3;
            } else {
                status = 1;
            }
        }
        if (TIMMessageStatus.SendSucc == timMsg.status()) {
            status = 2;
        }
        if (TIMMessageStatus.SendFail == timMsg.status()) {
            status = 3;
        }
        return status;
    }

    @Override
    public boolean setTimeTag(int tt) {
        timeTag = tt;
        return true;
    }

    @Override
    public int getTimeTag() {
        return timeTag;
    }

    @NotNull
    @Override
    public Date getTime() {
        return time;
    }

    @Override
    public boolean setNickName(@NotNull String nickName) {
        this.nickName = nickName;
        return true;
    }

    @NotNull
    @Override
    public String getNickName() {
        return nickName;
    }

    @NotNull
    @Override
    public boolean isRevoke() {
        return isRevoke;
    }

    @NotNull
    @Override
    public boolean setRevoke(boolean revoke) {
        isRevoke = revoke;
        return true;
    }
}

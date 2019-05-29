package com.gengqiquan.imui.model.emoji;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.widget.EditText;
import android.widget.TextView;

import com.gengqiquan.imui.R;
import com.gengqiquan.imui.help.ThreadDispatcher;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EmojiManager {
    private static ArrayList<Emoji> emojiList = new ArrayList<>();
    private static LruCache<String, Bitmap> drawableCache = new LruCache<>(1024);
    private static Context context;
    private static String[] emojiFilters;
    private static int drawableWidth;

    public static ArrayList<Emoji> getEmojiList() {
        return emojiList;
    }

    public static void init(Context context) {
        EmojiManager.context = context.getApplicationContext();
        emojiFilters = context.getResources().getStringArray(R.array.emoji_filter);
        drawableWidth = dip2px(context, 32);
        loadFaceFiles();
    }

    static void loadFaceFiles() {
        ThreadDispatcher.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < emojiFilters.length; i++) {
                    String emojiFilter = emojiFilters[i];
                    loadAssetBitmap(emojiFilter, "emoji/" + emojiFilter + "@2x.png", true);
                }
            }
        });
    }


    private static Emoji loadAssetBitmap(String filter, String assetPath, boolean isEmoji) {
        InputStream is = null;
        try {
            Emoji emoji = new Emoji();
            Resources resources = context.getResources();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inDensity = DisplayMetrics.DENSITY_XXHIGH;
            options.inScreenDensity = resources.getDisplayMetrics().densityDpi;
            options.inTargetDensity = resources.getDisplayMetrics().densityDpi;
            is = context.getAssets().open(assetPath);
            Bitmap bitmap = BitmapFactory.decodeStream(is, new Rect(0, 0, drawableWidth, drawableWidth), options);
            if (bitmap != null) {
                drawableCache.put(filter, bitmap);

                emoji.setIcon(bitmap);
                emoji.setFilter(filter);
                if (isEmoji)
                    emojiList.add(emoji);

            }
            return emoji;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static boolean isFaceChar(String faceChar) {
        return drawableCache.get(faceChar) != null;
    }

    public static Spanned compatEmojiText(String content) {
        SpannableStringBuilder sb = new SpannableStringBuilder(content);
        String regex = "\\[(\\S+?)\\]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        Iterator<Emoji> iterator;
        Emoji emoji = null;
        while (m.find()) {
            iterator = emojiList.iterator();
            String tempText = m.group();
            while (iterator.hasNext()) {
                emoji = iterator.next();
                if (tempText.equals(emoji.getFilter())) {
                    //转换为Span并设置Span的大小
                    sb.setSpan(new ImageSpan(context, drawableCache.get(tempText)),
                            m.start(), m.end(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    break;
                }
            }
        }
        return  sb;
    }

    public static void handlerEmojiText(TextView comment, String content) {
        Spanned sb = compatEmojiText(content);
        int selection = comment.getSelectionStart();
        comment.setText(sb);
        if (comment instanceof EditText) {
            ((EditText) comment).setSelection(selection);
        }
    }
}

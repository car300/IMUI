package com.gengqiquan.imlib.model;

import com.gengqiquan.imlib.uitls.JsonUtil;

/**
 * @author mazhichao
 * @date 2019/06/11 14:56
 */
public class PreCustomElem extends CustomElem {

    public enum ShowType {
        normal("normal"),
        preSend("preSend");

        private String key;

        ShowType(String s) {
            this.key = s;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    private ShowType showType = ShowType.normal;

    public ShowType getShowType() {
        return showType;
    }

    public void setShowType(ShowType showType) {
        this.showType = showType;
    }

    public PreCustomElem(CustomType type, Platform platform, Object data, String type_desc,ShowType showType) {
        super(type, platform, data, type_desc);
        this.showType = showType;
    }

    public static PreCustomElem create(String json) {

        PreCustomElem customElem = (PreCustomElem) JsonUtil.fromJson(json, PreCustomElem.class);
        String data = JsonUtil.getString(json, "data");
        if (customElem.type == null) {
            customElem.type = CustomType.invalid;
        }
        if (customElem.showType == null){
            customElem.showType = ShowType.normal;
        }
        switch (customElem.type) {
            case share:
                customElem.data = JsonUtil.fromJson(data, ShareElem.class);
                break;
            default:
        }
        return customElem;
    }
}

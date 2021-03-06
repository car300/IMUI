package com.gengqiquan.imlib.model;

import com.gengqiquan.imlib.uitls.JsonUtil;

public class CustomElem {

    protected CustomType type = CustomType.invalid;
    protected Platform platform;
    protected Object data;
    protected String type_desc;

    public CustomElem(CustomType type, Platform platform, Object data,String type_desc) {
        this.type = type;
        this.platform = platform;
        this.data = data;
        this.type_desc = type_desc;
    }

    public CustomType getType() {
        return type;
    }

    public void setType(CustomType type) {
        this.type = type;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getType_desc() {
        return type_desc;
    }

    public void setType_desc(String type_desc) {
        this.type_desc = type_desc;
    }

    public static CustomElem create(String json) {

        CustomElem customElem = (CustomElem) JsonUtil.fromJson(json, CustomElem.class);
        String data = JsonUtil.getString(json, "data");
        if (customElem.type == null) {
            customElem.type = CustomType.invalid;
        }
        switch (customElem.type) {
            case share:
                customElem.data = JsonUtil.fromJson(data, ShareElem.class);
                break;
            default:
        }
        return customElem;
    }

    @Override
    public String toString() {
        return "CustomElem{" +
                "type=" + type +
                ", platform=" + platform +
                ", data=" + data +
                ", type_desc='" + type_desc + '\'' +
                '}';
    }
}

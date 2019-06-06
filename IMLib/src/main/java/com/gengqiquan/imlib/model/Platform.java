package com.gengqiquan.imlib.model;

public class Platform {

    private String from;
    private String os;
    private String version;
    private String device_info;
    private String sender_user_id;
    private String receive_user_id;
    private String latitude;
    private String longitude;

    public Platform(String from, String os, String version, String device_info, String sender_user_id, String receive_user_id, String latitude, String longitude) {
        this.from = from;
        this.os = os;
        this.version = version;
        this.device_info = device_info;
        this.sender_user_id = sender_user_id;
        this.receive_user_id = receive_user_id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDevice_info() {
        return device_info;
    }

    public void setDevice_info(String device_info) {
        this.device_info = device_info;
    }


    public String getReceive_user_id() {
        return receive_user_id;
    }

    public void setReceive_user_id(String receive_user_id) {
        this.receive_user_id = receive_user_id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getSender_user_id() {
        return sender_user_id;
    }

    public void setSender_user_id(String sender_user_id) {
        this.sender_user_id = sender_user_id;
    }

    @Override
    public String toString() {
        return "Platform{" +
                "from='" + from + '\'' +
                ", os='" + os + '\'' +
                ", version='" + version + '\'' +
                ", device_info='" + device_info + '\'' +
                ", sender_user_id='" + sender_user_id + '\'' +
                ", receive_user_id='" + receive_user_id + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }
}

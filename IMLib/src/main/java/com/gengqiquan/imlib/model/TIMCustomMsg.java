package com.gengqiquan.imlib.model;


import com.tencent.imsdk.TIMCustomElem;

/**
 * @author mazhichao
 * @date 2019/06/10 15:03
 */
public class TIMCustomMsg extends TIMCustomElem {


    public enum Status{
        normal("normal"),
        preSend("preSend");

        private String key;

        Status(String s) {
            this.key = s;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    private Status status = Status.normal;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}

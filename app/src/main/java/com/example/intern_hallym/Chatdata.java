package com.example.intern_hallym;
import java.io.Serializable;
public class Chatdata {
    private String msg;
    private String nickname;
    public Chatdata(String msg){
        this.msg = msg;
    }
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

}

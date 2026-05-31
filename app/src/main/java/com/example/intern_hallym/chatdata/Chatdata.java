package com.example.intern_hallym.chatdata;
import java.io.Serializable;
public class Chatdata implements Serializable{
    private String msg;
    private String nick;
    public Chatdata(){

    }
    public Chatdata(String msg,String nick){
        this.msg = msg;
        this.nick =nick;
    }
    public String getMsg() {

        return msg;
    }

    public void setMsg(String msg) {

        this.msg = msg;
    }

    public String getNickname() {

        return nick;
    }

    public void setNickname(String nick) {

        this.nick = nick;
    }

}

package com.example.intern_hallym.activity_output;

public class ChatRoom{
    public String roomName;
    public String password;
    public int maxCount;
    public int currentCount;

    public ChatRoom() {}

    public ChatRoom(String roomName,String password,int maxCount,int cuurentCount){
        this.roomName = roomName;
        this.password = password;
        this.maxCount = maxCount;
        this.currentCount = cuurentCount;
    }
}

package com.example.intern_hallym.adapter;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intern_hallym.chatdata.Chatdata;
import com.example.intern_hallym.R;

import java.util.ArrayList;

// 🌟 파일 이름과 똑같이 ChatAdp로 대장 이름을 맞춰주었어!
public class ChatAdp extends RecyclerView.Adapter<ChatAdp.ChatViewHolder> {

    private ArrayList<Chatdata> chatlist;
    private android.content.Context context;
    private String myNick;

    // 생성자 이름도 파일 이름과 똑같이 ChatAdp로 변경!
    public ChatAdp(ArrayList<Chatdata> chatlist,android.content.Context context,String myNick) {
        this.chatlist = chatlist;
        this.context = context;
        this.myNick = myNick;
    }

    // 1️⃣ [기차 칸 찍어내기] item_chat.xml 도면을 눈에 보이는 부품으로 만드는 곳
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    // 2️⃣ [글자 배달하기] 만들어진 부품 상자에 진짜 채팅 글씨를 적어주는 곳
    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chatdata data = chatlist.get(position);
        holder.tvMessage.setText(data.getMsg());

        if(data.getNickname() != null){
            holder.tvNickname.setText(data.getNickname());
        }
        else{
            holder.tvNickname.setText(("알 수 없음"));
        }

        if(data.getNickname() != null && data.getNickname().equals(myNick)){
            holder.layoutContainer.setGravity(Gravity.END);
            holder.tvNickname.setGravity(Gravity.END);
            holder.tvNickname.setVisibility(View.GONE);
        }
        else{
            holder.layoutContainer.setGravity(Gravity.START);
            holder.tvNickname.setVisibility(View.VISIBLE);
        }
    }

    // 3️⃣ [개수 세기] 총 몇 개의 말풍선을 그려야 하는지 세어주는 곳
    @Override
    public int getItemCount() {
        return chatlist != null ? chatlist.size() : 0;
    }

    // ⭐ [멱살잡이 주머니] 글자 상자(TextView)를 꽉 붙잡고 있는 주머니방
    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        TextView tvNickname;
        LinearLayout layoutContainer;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.Tvmsg);
            tvNickname = itemView.findViewById(R.id.Textnickname);
            layoutContainer = itemView.findViewById(R.id.layout_container);
        }
    }
}
package com.example.intern_hallym.activity_output;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intern_hallym.adapter.ChatAdp;
import com.example.intern_hallym.chatdata.Chatdata;
import com.example.intern_hallym.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class chatActivity2 extends AppCompatActivity {


    private RecyclerView recyclerView;
    private ArrayList<Chatdata> chatlist;
    private String nick = "익명";
    private String roomName ="기본방";
    private EditText edmsg;
    private Button btnSend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.content_chat2);

        if(getIntent()!= null && getIntent().hasExtra("userNick")){
            nick = getIntent().getStringExtra("userNick");
        }
        if(getIntent().hasExtra("roomName")){
            roomName = getIntent().getStringExtra("roomName");
        }
        // 리사이클러뷰(화면 레일) 연결 및 매니저 설정
        recyclerView = findViewById(R.id.recycleView); // XML 이름표와 일치 확인!
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 진짜 채팅 데이터 바구니 생성
        chatlist = new ArrayList<>();

        //  어댑터 로봇 조립 및 레일에 장착
        ChatAdp myAdp = new ChatAdp(chatlist, chatActivity2.this, nick);
        recyclerView.setAdapter(myAdp);

        // 입력창과 버튼 연결
        edmsg = findViewById(R.id.et);
        btnSend = findViewById(R.id.btnSend);

        ChatSendLisner chatSendLisner = new ChatSendLisner(edmsg,chatlist,myAdp,recyclerView);
        btnSend.setOnClickListener(chatSendLisner);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://intern-hallym-default-rtdb.firebaseio.com/");
        DatabaseReference myRef = database.getReference("rooms").child(roomName).child("chats");

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = edmsg.getText().toString().trim();

                if(!message.isEmpty()){
                    Chatdata chat = new Chatdata(message,nick);

                    myRef.push().setValue(chat);

                    edmsg.setText("");
                }
            }
        });

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Chatdata chat = snapshot.getValue(Chatdata.class);

                if(chat != null){
                    chatlist.add(chat);
                    myAdp.notifyItemInserted(chatlist.size()-1);
                    recyclerView.scrollToPosition(chatlist.size()-1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }
}
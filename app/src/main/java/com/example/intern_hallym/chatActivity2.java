package com.example.intern_hallym;

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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class chatActivity2 extends AppCompatActivity {

    // 🧼 쓰지 않는 유령 변수들 깔끔하게 정리!
    private RecyclerView recyclerView;
    private ArrayList<Chatdata> chatlist;
    private String nick = "내닉네임";
    private EditText edmsg;
    private Button btnSend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.content_chat2);

        // 1️⃣ 리사이클러뷰(화면 레일) 연결 및 매니저 설정
        recyclerView = findViewById(R.id.recycleView); // XML 이름표와 일치 확인!
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 2️⃣ 진짜 채팅 데이터 바구니 생성
        chatlist = new ArrayList<>();

        // 3️⃣ 어댑터 로봇 조립 및 레일에 장착
        ChatAdp myAdp = new ChatAdp(chatlist, chatActivity2.this, nick);
        recyclerView.setAdapter(myAdp);

        // 4️⃣ 입력창과 버튼 연결
        edmsg = findViewById(R.id.et);
        btnSend = findViewById(R.id.btnSend);

        ChatSendLisner chatSendLisner = new ChatSendLisner(edmsg,chatlist,myAdp,recyclerView);
        btnSend.setOnClickListener(chatSendLisner);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://intern-hallym-default-rtdb.firebaseio.com/");
        DatabaseReference myRef = database.getReference("message");
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
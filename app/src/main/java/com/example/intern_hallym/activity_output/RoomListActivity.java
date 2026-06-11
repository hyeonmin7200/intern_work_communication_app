package com.example.intern_hallym.activity_output;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intern_hallym.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RoomListActivity extends AppCompatActivity{
    private RecyclerView recyclerView;
    private ArrayList<String> roomList;
    private RoomAdapter adapter;
    private EditText etNewRoomName;
    private Button btCreateRoom;
    private String myNick = "익명";
    private DatabaseReference roomRef;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        Toast.makeText(this,"방 목록",Toast.LENGTH_LONG).show();
        setContentView(R.layout.activity_room);

        if(getIntent()!= null && getIntent().hasExtra("userNick")){
            myNick = getIntent().getStringExtra("userNick");
        }

        etNewRoomName = findViewById(R.id.etNewroom);
        btCreateRoom = findViewById(R.id.btCreateRoom);
        recyclerView = findViewById(R.id.roomRecyclerView);

        roomList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RoomAdapter(roomList);
        recyclerView.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://intern-hallym-default-rtdb.firebaseio.com/");
        roomRef = database.getReference("rooms");

        btCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomName = etNewRoomName.getText().toString().trim();
                if(!roomName.isEmpty()){
                    roomRef.child(roomName).setValue("방이 개설되었습니다.");
                    etNewRoomName.setText("");
                    Toast.makeText(RoomListActivity.this,
                            roomName + "방이 생성되었습니다!", Toast.LENGTH_SHORT).show();

                }
            }
        });
        roomRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String roomName = snapshot.getKey();
                if(roomName!= null &&!roomList.contains(roomName)){
                    roomList.add(roomName);
                    adapter.notifyItemInserted(roomList.size()-1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder>{
        private ArrayList<String> list;

        public RoomAdapter(ArrayList<String> list){
            this.list = list;
        }
        @NonNull
        @Override
        public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room,parent,false);
            return new RoomViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull RoomViewHolder holder,int position){
            String roomName = list.get(position);
            holder.tvRoomName.setText(roomName);

            holder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RoomListActivity.this, chatActivity2.class);
                    intent.putExtra("userNick", myNick);     // 내 닉네임 배달
                    intent.putExtra("roomName", roomName);   // 클릭한 방 이름 배달
                    startActivity(intent);
                }
            });
        }
        @Override
        public int getItemCount(){
            return list != null ?list.size():0;
        }
        public class RoomViewHolder extends RecyclerView.ViewHolder {
            TextView tvRoomName;
            public RoomViewHolder(@NonNull View itemView) {
                super(itemView);
                tvRoomName = itemView.findViewById(R.id.tvRoomName);
            }
        }
    }







}
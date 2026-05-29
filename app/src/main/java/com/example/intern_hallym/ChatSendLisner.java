package com.example.intern_hallym;

import android.view.View;
import android.widget.EditText;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
public class ChatSendLisner  implements View.OnClickListener{
    private EditText edmsg;
    private ArrayList<Chatdata> chatlist;
    private  ChatAdp myAdp;
    private RecyclerView recycle;

    public ChatSendLisner(EditText edmsg,ArrayList<Chatdata> chatlist,ChatAdp myAdp,RecyclerView recycle){
        this.edmsg = edmsg;
        this.chatlist = chatlist;
        this.myAdp = myAdp;
        this.recycle = recycle;
    }
    @Override
    public void onClick(View v){
        String msg = edmsg.getText().toString().trim();

        if(!msg.isEmpty()){
            Chatdata newData = new Chatdata(msg);


            FirebaseDatabase database = FirebaseDatabase.getInstance("https://intern-hallym-default-rtdb.firebaseio.com/");
            DatabaseReference myRef = database.getReference("message");

            myRef.push().setValue(newData);

            edmsg.setText("");
        }
    }
}

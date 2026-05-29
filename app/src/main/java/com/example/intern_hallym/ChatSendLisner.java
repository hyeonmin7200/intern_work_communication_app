package com.example.intern_hallym;

import android.view.View;
import android.widget.EditText;
import androidx.recyclerview.widget.RecyclerView;
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
            chatlist.add(newData);

            myAdp.notifyItemInserted(chatlist.size()-1);
            recycle.scrollToPosition(chatlist.size()-1);

            edmsg.setText("");
        }
    }
}

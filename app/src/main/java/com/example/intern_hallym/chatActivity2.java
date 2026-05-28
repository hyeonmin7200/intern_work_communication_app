package com.example.intern_hallym;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.intern_hallym.databinding.ActivityChat2Binding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class chatActivity2 extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppBarConfiguration appBarConfiguration;
    private ActivityChat2Binding binding;
    private Chatdata Chatdata;
    private ChatAdp adapter;
    private ArrayList<String> msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.content_chat2);

        recyclerView = findViewById(R.id.recyview);


        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://intern-hallym-default-rtdb.firebaseio.com/");
        DatabaseReference myRef = database.getReference("message");

        msg = new ArrayList<>();
        msg.add("123132");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ChatAdp(msg);
        recyclerView.setAdapter(adapter);
    }
}
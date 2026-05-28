package com.example.intern_hallym;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
// ... 필요한 import들 ...
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // 첫 화면 레이아웃

        // 🌟여기에 파이어베이스 코드를 똑같이 넣어주세요!🌟
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://intern-hallym-default-rtdb.firebaseio.com/");
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");
    }
}
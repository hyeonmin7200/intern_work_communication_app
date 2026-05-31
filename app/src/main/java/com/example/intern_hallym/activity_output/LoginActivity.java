package com.example.intern_hallym.activity_output;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.intern_hallym.R;

public class LoginActivity extends AppCompatActivity{
    private EditText etNickname;
    private Button enter_bt;

    @Override

    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);

        setContentView(R.layout.activity_main);

        etNickname = findViewById(R.id.et);
        enter_bt = findViewById(R.id.enter_btn);

        enter_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = etNickname.getText().toString().trim();

                if(nickname.isEmpty()){
                    Toast.makeText(LoginActivity.this ,"닉네임을 입력하셔야합니다.",Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(LoginActivity.this,chatActivity2.class);
                    startActivity(intent);
                }
            }
        });
    }

}

package com.example.intern_hallym.activity_output;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.intern_hallym.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity{
    private EditText etLoginID,etLoginpwd;
    private Button login_Btn, sign_up_btn;
    private FirebaseAuth mAuth;

    @Override

    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        etLoginID = findViewById(R.id.etLoginID);
        etLoginpwd = findViewById(R.id.etLoginpwd);
        login_Btn = findViewById(R.id.login_Btn);
        sign_up_btn = findViewById(R.id.sign_up_btn);

        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,Sign_up_activity.class);
                startActivity(intent);
            }
        });

        login_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etLoginID.getText().toString().trim();
                String password = etLoginpwd.getText().toString().trim();

                if(username.isEmpty() || password.isEmpty()){
                    Toast.makeText(LoginActivity.this, "아이디와 비밀번호를 입력해 주세요",Toast.LENGTH_SHORT).show();
                    return;
                }
                String fakeEmail = username +"@myapp.com";

                mAuth.signInWithEmailAndPassword(username,password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    final com.google.firebase.auth.FirebaseUser user = mAuth.getCurrentUser();
                                    String uid = mAuth.getCurrentUser().getUid();

                                    FirebaseDatabase.getInstance("https://intern-hallym-default-rtdb.firebaseio.com/")
                                            .getReference("users").child(uid).child("nickname")
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    String nickname = snapshot.getValue(String.class);
                                                    if(nickname==null) nickname = "익명";

                                                    Intent intent = new Intent(LoginActivity.this,RoomListActivity.class);
                                                    intent.putExtra("userNick",nickname);
                                                    startActivity(intent);
                                                    finish();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {}
                                            });
                                }else{
                                    String errorMessage = task.getException() != null ? task.getException().getMessage(): "알 수 없음";
                                    Toast.makeText(LoginActivity.this,errorMessage,Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }

}

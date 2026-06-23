package com.example.intern_hallym.Login_sign_up_tool;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.intern_hallym.R;
import com.example.intern_hallym.activity_output.RoomListActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity{
    private EditText etLoginID,etLoginpwd;
    private Button login_Btn, sign_up_btn;
    private FirebaseAuth mAuth;

    private com.google.android.gms.common.SignInButton btGoogleLogin; // XML에 있는 구글 버튼용
    private Google_login_tool googleLoginTool;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        googleLoginTool = new Google_login_tool(LoginActivity.this);

        etLoginID = findViewById(R.id.etLoginID);
        etLoginpwd = findViewById(R.id.etLoginpwd);
        login_Btn = findViewById(R.id.login_Btn);
        sign_up_btn = findViewById(R.id.sign_up_btn);


        btGoogleLogin = findViewById(R.id.btnGoogleLogin);

        btGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleLoginTool.SignIn();
            }
        });
        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, Sign_up_activity.class);
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
                String fakeEmail;
                if(username.contains("@")){
                    fakeEmail = username;
                }else{
                    fakeEmail = username +"@myapp.com";
                }

                mAuth.signInWithEmailAndPassword(fakeEmail,password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    final com.google.firebase.auth.FirebaseUser user = mAuth.getCurrentUser();
                                    String uid = mAuth.getCurrentUser().getUid();

                                    FirebaseDatabase.getInstance()
                                            .getReference("users").child(uid).child("nickname")
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    String nickname = snapshot.getValue(String.class);
                                                    if(nickname==null) nickname = "익명";

                                                    Intent intent = new Intent(LoginActivity.this, RoomListActivity.class);
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

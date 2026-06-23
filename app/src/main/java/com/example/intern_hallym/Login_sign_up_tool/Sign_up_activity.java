package com.example.intern_hallym.Login_sign_up_tool;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
public class Sign_up_activity extends AppCompatActivity{
    private EditText etsignUp_id, etSignUp_pwd,etSignUpNickname;
    private Button btnSignUpRegister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        etsignUp_id = findViewById(R.id.etsignUp_id);
        etSignUp_pwd = findViewById(R.id.etSignUp_pwd);
        etSignUpNickname = findViewById(R.id.etSignUpNickname);
        btnSignUpRegister = findViewById(R.id.btnSignUpRegister);

        btnSignUpRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etsignUp_id.getText().toString().trim();
                String password = etSignUp_pwd.getText().toString().trim();
                String nickname = etSignUpNickname.getText().toString().trim();

                if(email.isEmpty() ||password.isEmpty() || nickname.isEmpty()){
                    Toast.makeText(Sign_up_activity.this,"필수 정보를 입력하셔야 합니다",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(password.length()<8){
                    Toast.makeText(Sign_up_activity.this,"비밀번호는 8자리 이상이어야합니다",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!email.contains("@")){
                    Toast.makeText(Sign_up_activity.this,"올바른 이메일 형식을 지켜주십시오.",Toast.LENGTH_SHORT).show();
                    return;
                }

                final String finalEmail = email;

                mAuth.createUserWithEmailAndPassword(finalEmail,password)
                        .addOnCompleteListener(Sign_up_activity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){

                                    final FirebaseUser user = mAuth.getCurrentUser();

                                    if(user !=null){
                                        user.sendEmailVerification()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> verification) {
                                                        if(verification.isSuccessful()){
                                                            Toast.makeText(Sign_up_activity.this,
                                                                    "인증 메일을 발송했습니다",
                                                                    Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                    }
                                    String uid = mAuth.getCurrentUser().getUid();

                                    HashMap<String, Object> userMap = new HashMap<>();
                                    userMap.put("uid",uid);
                                    userMap.put("email",finalEmail);
                                    userMap.put("nickname",nickname);

                                    FirebaseDatabase.getInstance("https://intern-hallym-default-rtdb.firebaseio.com/")
                                            .getReference("users")
                                            .child(uid)
                                            .setValue(userMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(Sign_up_activity.this,"회원가입 완료!",Toast.LENGTH_SHORT).show();
                                                        finish();

                                                    }else{
                                                        Toast.makeText(Sign_up_activity.this,"db저장실패",Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            });

                                }else{
                                    Toast.makeText(Sign_up_activity.this,"회원가입 실패"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}

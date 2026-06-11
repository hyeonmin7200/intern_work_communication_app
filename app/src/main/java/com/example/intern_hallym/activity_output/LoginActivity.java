package com.example.intern_hallym.activity_output;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.credentials.CreateCredentialRequest;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.example.intern_hallym.R;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class LoginActivity extends AppCompatActivity{
    private EditText etLoginID,etLoginpwd;
    private Button login_Btn, sign_up_btn;
    private SignInButton btGoogleLogin;
    private FirebaseAuth mAuth;
    private com.google.android.gms.auth.api.identity.SignInClient mSignInClient;
    private CredentialManager credentialManager;
    private GetCredentialRequest getCredentialRequest;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.default_web_client_id))
                .build();

        credentialManager = CredentialManager.create(LoginActivity.this);
        getCredentialRequest = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        etLoginID = findViewById(R.id.etLoginID);
        etLoginpwd = findViewById(R.id.etLoginpwd);
        login_Btn = findViewById(R.id.login_Btn);
        sign_up_btn = findViewById(R.id.sign_up_btn);
        btGoogleLogin = findViewById(R.id.btnGoogleLogin);

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
        btGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                credentialManager.getCredentialAsync(
                        LoginActivity.this,
                        getCredentialRequest,
                        null,
                        androidx.core.content.ContextCompat.getMainExecutor(LoginActivity.this),
                        new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                            @Override
                            public void onResult(GetCredentialResponse getCredentialResponse) {
                                handleSignInCredential(getCredentialResponse.getCredential());
                            }

                            @Override
                            public void onError(@NonNull GetCredentialException e) {
                                Toast.makeText(LoginActivity.this,"구글 로그인 실패:"+e.getLocalizedMessage()
                                        ,Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                );
            }
        });
    }
    private void handleSignInCredential(Credential credential){
        if(credential instanceof GoogleIdTokenCredential){
            try{
                GoogleIdTokenCredential googleIdTokenCredential =(GoogleIdTokenCredential) credential;
                String idToken = googleIdTokenCredential.getIdToken();

                if(idToken!=null){
                    firebaseAuthWithGoogle(idToken);
                }
            }catch (Exception e){
                Toast.makeText(this,"토큰 파싱 실패:"+  e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "구글 인증 성공! DB 조회 중...", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user!= null){
                                final String uid = user.getUid();
                                final String displayName = user.getDisplayName()!=null? user.getDisplayName(): "구글유저";
                                final String email = user.getEmail();

                                FirebaseDatabase.getInstance()
                                        .getReference("users").child(uid)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String finalNickname = displayName;
                                                if(!snapshot.exists()){
                                                    HashMap<String,Object> userMap = new HashMap<>();
                                                    userMap.put("uid",uid);
                                                    userMap.put("email",email);
                                                    userMap.put("nickname",displayName);
                                                    snapshot.getRef().setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Intent intent = new Intent(LoginActivity.this, RoomListActivity.class);
                                                            intent.putExtra("userNick",displayName);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    });
                                                }else{
                                                    if(snapshot.hasChild("nickname")){
                                                        String dbNick = snapshot.child("nickname").getValue(String.class);
                                                        if(dbNick !=null && !dbNick.isEmpty()){
                                                            finalNickname =dbNick;
                                                        }
                                                    }


                                                Intent intent = new Intent(LoginActivity.this, RoomListActivity.class);
                                                intent.putExtra("userNick",finalNickname);
                                                startActivity(intent);
                                                finish();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Intent intent = new Intent(LoginActivity.this, RoomListActivity.class);
                                                intent.putExtra("userNick",displayName);
                                                startActivity(intent);
                                                finish();

                                            }
                                        });
                            }
                        }else{
                            Toast.makeText(LoginActivity.this,"파이어베이스 구글 연동 실패",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

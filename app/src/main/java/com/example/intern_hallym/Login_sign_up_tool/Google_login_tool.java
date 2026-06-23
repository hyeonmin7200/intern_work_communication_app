package com.example.intern_hallym.Login_sign_up_tool;
import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.example.intern_hallym.R;
import com.example.intern_hallym.activity_output.RoomListActivity;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
public class Google_login_tool {
    private final Activity activity;
    private final FirebaseAuth mAuth;
    private final CredentialManager credentialManager;
    private final GetCredentialRequest getCredentialRequest;


    public Google_login_tool(Activity activity){
        this.activity = activity;
        this.mAuth = FirebaseAuth.getInstance();

        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(activity.getString(R.string.default_web_client_id))
                .build();

        this.credentialManager = CredentialManager.create(activity);
        this.getCredentialRequest = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();
    }

    public void SignIn(){
        credentialManager.getCredentialAsync(
                activity,
                getCredentialRequest,
                null,
                ContextCompat.getMainExecutor(activity),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse getCredentialResponse) {
                        handleSignInCredential(getCredentialResponse.getCredential());
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        Toast.makeText(activity,"구글 로그인 실패:"+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
    private void handleSignInCredential(Credential credential){
        if(credential instanceof GoogleIdTokenCredential){
            try{
                GoogleIdTokenCredential googleIdTokenCredential = (GoogleIdTokenCredential) credential;
                String idToken = googleIdTokenCredential.getIdToken();
                if (idToken!= null){
                    firebaseAuthWithGoogle(idToken);
                }
            }catch (Exception e){
                Toast.makeText(activity,"토큰 파싱 실패"+ e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        } else {
            android.util.Log.e("Google_login_error","자격 증명 타입 불일치" +credential.getType());
            Toast.makeText(activity,"구글 인증서(SHA-1) 오류 가능성 타입"+credential.getType(),Toast.LENGTH_LONG).show();
        }
    }

    private void firebaseAuthWithGoogle(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(activity,"구글 인증 성공",Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null){
                                final String uid = user.getUid();
                                final String displayName = user.getDisplayName() !=null? user.getDisplayName() : "구글유저";
                                final String email = user.getEmail();

                                FirebaseDatabase.getInstance()
                                        .getReference("users").child(uid)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String finalNickname = displayName;

                                                if(!snapshot.exists()){
                                                    HashMap<String, Object> userMap = new HashMap<>();
                                                    userMap.put("uid",uid);
                                                    userMap.put("email",email);
                                                    userMap.put("nickname",displayName);

                                                    snapshot.getRef().setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            moveToRoomList(displayName);
                                                        }
                                                    });
                                                } else{
                                                    if(snapshot.hasChild("nickname")){
                                                        String dbNick = snapshot.child("nickname").getValue(String.class);
                                                        if(dbNick!=null && !dbNick.isEmpty()){
                                                            finalNickname = dbNick;
                                                        }
                                                    }
                                                    moveToRoomList(finalNickname);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                moveToRoomList(displayName);
                                            }
                                        });
                            }
                        }else{
                            String errorMsg = task.getException() != null?task.getException().getMessage():"알 수 없음";
                            Toast.makeText(activity,"파이어베이스 연동 실패:"+errorMsg,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void moveToRoomList(String nickname){
        Intent intent = new Intent(activity,RoomListActivity.class);
        intent.putExtra("userNick",nickname);
        activity.startActivity(intent);
        activity.finish();
    }
}

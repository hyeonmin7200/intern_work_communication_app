package com.example.intern_hallym.Image_File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.UUID;

public class Image_tool {
    private AppCompatActivity activity;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private Uri imageUri;
    private OnImageUploadListener uploadListener;

    public interface OnImageUploadListener {
        void onUploadSuccess(String imageUrl);
        void onUploadFailure(String errorMessage);
    }

    public Image_tool(@NonNull AppCompatActivity activity, OnImageUploadListener listener) {
        this.activity = activity;
        this.uploadListener = listener;

        // 런처 정상적으로 바인딩 완료
        this.galleryLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        uploadImageToFirebase(); // 사진 선택되면 업로드 함수 호출
                    }
                }
        );
    }

    // 📸 갤러리 앱을 여는 메서드
    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void uploadImageToFirebase() {
        if (imageUri != null) {
            // 구글 서버에 겹치지 않게 저장할 파일명 만들기 (시간 + 랜덤ID)
            String filename = System.currentTimeMillis() + "_" + UUID.randomUUID().toString() + ".jpg";
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("chat_images/" + filename);

            // 파일 업로드 시작
            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // 업로드 성공 시, 인터넷에서 볼 수 있는 이미지 다운로드 URL 주소 따오기
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            if (uploadListener != null) {
                                uploadListener.onUploadSuccess(uri.toString());
                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        if (uploadListener != null) {
                            uploadListener.onUploadFailure(e.getMessage());
                        }
                    });
        }
    }
}
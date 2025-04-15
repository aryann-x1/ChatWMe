package com.example.firstapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FaceActivity extends AppCompatActivity {
    private PreviewView cameraPreviewView;
    private Button detectEmotionButton;
    private TextView emotionResultText;
    private ExecutorService cameraExecutor;
    private EmotionClassifierFace emotionClassifierFace;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);
        Log.d("DEBUG_LIFECYCLE", "onCreate: FaceActivity started");
        cameraPreviewView = findViewById(R.id.cameraPreviewView);
        detectEmotionButton = findViewById(R.id.detectEmotionButton);
        emotionResultText = findViewById(R.id.emotionResultText);

        cameraExecutor = Executors.newSingleThreadExecutor();
        Log.d("DEBUG_LIFECYCLE", "onCreate: FaceActivity started");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
        }
        Log.d("DEBUG_PERMISSION", "Camera permission granted: " +
                (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED));



        detectEmotionButton.setOnClickListener(v -> captureFrame());
        Log.d("DEBUG_UI", "Detect Emotion button clicked");
    }

    private void startCamera() {
        Log.d("DEBUG_CAMERA", "startCamera() called"); // Add this log

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Log.d("DEBUG_CAMERA", "CameraProvider retrieved successfully"); // Add this log

                CameraSelector cameraSelector;
                try {
                    cameraSelector = new CameraSelector.Builder()
                            .requireLensFacing((CameraSelector.LENS_FACING_FRONT))
                            .build();
                    Log.d("DEBUG_CAMERA", "Default camera selector used");
                } catch (IllegalArgumentException e) {
                    Log.e("DEBUG_CAMERA", "Front camera not available, switching to back camera.");
                    cameraSelector = new CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                            .build();

                }

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(cameraPreviewView.getSurfaceProvider());
                Log.d("DEBUG_CAMERA", "Preview set to surface provider");

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, image -> processImage(image));

                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
                Log.d("DEBUG_CAMERA", "Camera successfully bound to lifecycle"); // Add this log

            } catch (Exception e) {
                Log.e("DEBUG_CAMERA", "Failed to start camera", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void processImage(ImageProxy imageProxy) {
        Image image = imageProxy.getImage();
        if (image != null) {
            Bitmap bitmap = BitmapUtils.imageToBitmap(image);
            if (bitmap != null) {
                Log.d("DEBUG_PROCESS", "Bitmap created from image");
                try {
                    if (emotionClassifierFace == null) {
                        emotionClassifierFace = new EmotionClassifierFace(getApplicationContext());
                    }
                    Log.d("DEBUG_PROCESS", "Initialized EmotionClassifierFace");

                    String predictedEmotion = emotionClassifierFace.classify(bitmap);
                    Log.d("DEBUG_RESULT", "Detected Emotion: " + predictedEmotion);

                    runOnUiThread(() -> emotionResultText.setText("Detected Emotion: " + predictedEmotion));
                } catch (Exception e) {
                    Log.e("DEBUG_PROCESS", "Emotion classification failed", e);
                }
            } else {
                Log.e("DEBUG_PROCESS", "Bitmap conversion failed. Bitmap is null.");
            }
        } else {
            Log.e("DEBUG_PROCESS", "Image is null.");
        }

        imageProxy.close();
    }



    private void sendToFacePlusPlus(String base64Image) {
        String API_KEY = "0joyR2mLsVPXWeeDwtaEQUBLsr4hvGX4";
        String API_SECRET = "9V2XZkXw4qIf5F0kfMYS0bu6u3iYp0SE";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api-us.faceplusplus.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FacePlusPlusAPI api = retrofit.create(FacePlusPlusAPI.class);

        Call<ResponseBody> call = api.detectEmotion(API_KEY, API_SECRET, base64Image, "emotion");

        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        runOnUiThread(() -> emotionResultText.setText("Response:\n" + responseBody));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() -> emotionResultText.setText("API Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                runOnUiThread(() -> emotionResultText.setText("Failed: " + t.getMessage()));
            }
        });
    }



    private void captureFrame() {
        Log.d("DEBUG_UI", "captureFrame() called");
        Toast.makeText(this, "Captured frame for emotion analysis!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

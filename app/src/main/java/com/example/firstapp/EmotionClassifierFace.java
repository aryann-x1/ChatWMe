package com.example.firstapp;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class EmotionClassifierFace {
    private final Interpreter tflite;
    private final List<String> labels;

    public EmotionClassifierFace(Context context) throws IOException {
        Log.d("EMOTION_MODEL", "Initializing EmotionClassifierFace...");

        Interpreter.Options options = new Interpreter.Options();

        // Load the model and log success
        try {
            tflite = new Interpreter(loadModelFile(context), options);
            Log.d("EMOTION_MODEL", "Model loaded successfully.");
        } catch (IOException e) {
            Log.e("EMOTION_MODEL", "Failed to load model.", e);
            throw e; // Re-throw if necessary
        }

        // Load labels and log success
        try {
            labels = loadLabels(context);
            Log.d("EMOTION_MODEL", "Labels loaded: " + labels.toString());
        } catch (IOException e) {
            Log.e("EMOTION_MODEL", "Failed to load labels.", e);
            throw e;
        }
    }

    private MappedByteBuffer loadModelFile(Context context) throws IOException {
        Log.d("EMOTION_MODEL", "Loading model file from assets...");
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd("model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        Log.d("EMOTION_MODEL", "Model file opened. Size: " + declaredLength + " bytes.");
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private List<String> loadLabels(Context context) throws IOException {
        Log.d("EMOTION_MODEL", "Loading labels...");
        List<String> labelList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open("labels.txt")));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
            Log.d("EMOTION_MODEL", "Label loaded: " + line);
        }
        reader.close();
        return labelList;
    }

    public String classify(Bitmap bitmap) {
        // Resize bitmap to 224x224 if the model expects a larger input size
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true); // depends on your model
        ByteBuffer input = ByteBuffer.allocateDirect(4 * 224 * 224 * 3); // For RGB image with 3 channels (224x224x3)
        input.order(ByteOrder.nativeOrder());

        // Convert the image to a grayscale value for each pixel (if your model uses RGB)
        for (int y = 0; y < 224; y++) {
            for (int x = 0; x < 224; x++) {
                int pixel = scaledBitmap.getPixel(x, y);
                float red = Color.red(pixel) / 255f;
                float green = Color.green(pixel) / 255f;
                float blue = Color.blue(pixel) / 255f;

                // Assuming the model uses RGB input
                input.putFloat(red);
                input.putFloat(green);
                input.putFloat(blue);
            }
        }

        // Create the output array (ensure it matches the model's output size)
        float[][] output = new float[1][labels.size()];  // Adjust labels.size() to match the model's output
        tflite.run(input, output);

        // Log the confidence scores for each label
        for (int i = 0; i < output[0].length; i++) {
            String label = labels.get(i);
            float confidence = output[0][i];
            Log.d("EMOTION_CONFIDENCE", label + ": " + (confidence * 100f) + "%");
        }

        // Find the label with the highest confidence
        int maxIdx = 0;
        float maxConf = output[0][0];
        for (int i = 1; i < output[0].length; i++) {
            if (output[0][i] > maxConf) {
                maxConf = output[0][i];
                maxIdx = i;
            }
        }

        String predictedLabel = labels.get(maxIdx);
        Log.d("EMOTION_PREDICTION", "Predicted Emotion: " + predictedLabel + " with confidence: " + (maxConf * 100f) + "%");
        return predictedLabel;
    }

}


package com.mzm.sample.digit_recognizer;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileChannel;

/**
 * This image classifier classifies each drawing as one of the 10 digits
 */
public class Classifier {

    private static final String LOG_TAG = Classifier.class.getSimpleName();

    // Name of the model file (under assets folder)
    private static final String MODEL_PATH = "numbers_custom_model_v2.tflite";

    // TensorFlow Lite interpreter for running inference with the tflite model
    private final Interpreter interpreter;

    /* Input */
    // A ByteBuffer to hold image data for input to model
    private final ByteBuffer inputImage;

    private final int[] imagePixels = new int[DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y];// * DIM_PIXEL_SIZE];

    // Input size
    private static final int DIM_BATCH_SIZE = 1;    // batch size
    public static final int DIM_IMG_SIZE_X = 64;   // height
    public static final int DIM_IMG_SIZE_Y = 128;   // width
    private static final int DIM_PIXEL_SIZE = 3;    // 1 for gray scale & 3 for color images

    /* Output*/
    // Output size is 10 (number of digits)
    private static final int MAX_NUMBERS = 4;
    private static final int DIGITS = 10 + 1;

    // Output array [batch_size, number of digits]
    // 10 floats, each corresponds to the probability of each digit
    private float[][][] outputArray = new float[DIM_BATCH_SIZE][MAX_NUMBERS][DIGITS];

    public Classifier(Activity activity) throws IOException {
        interpreter = new Interpreter(loadModelFile(activity));
        inputImage = ByteBuffer.allocateDirect(
                4 * DIM_BATCH_SIZE * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE
        );
        inputImage.order(ByteOrder.nativeOrder());
    }

    // Memory-map the model file in Assets
    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_PATH);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    /**
     * To classify an image, follow these steps:
     * 1. pre-process the input image
     * 2. run inference with the model
     * 3. post-process the output result for display in UI
     *
     * @param bitmap
     * @return the digit with the highest probability
     */
    public String classify(Bitmap bitmap) {
        preprocess(bitmap);
        runInference();
        return postprocess();
    }

    /**
     * Preprocess the bitmap by converting it to ByteBuffer & grayscale
     *
     * @param bitmap
     */
    private void preprocess(Bitmap bitmap) {
        convertBitmapToByteBuffer(bitmap);
    }

    private void convertBitmapToByteBuffer(Bitmap bitmap) {
        if (inputImage == null) {
            return;
        }
        inputImage.rewind();

//        int w =  bitmap.getWidth();
//        int h =  bitmap.getHeight();
        bitmap.getPixels(imagePixels, 0, bitmap.getWidth(), 0, 0,
                bitmap.getWidth(), bitmap.getHeight());

        int pixel = 0;
        for (int i = 0; i < DIM_IMG_SIZE_X; ++i) {
            for (int j = 0; j < DIM_IMG_SIZE_Y; ++j) {
//                for (int k = 0; k < DIM_PIXEL_SIZE; ++k) {
//                    final int val = imagePixels[pixel++];
//                    inputImage.putFloat(val / 255.0f);
//                }
                final int val = imagePixels[pixel++];
//                inputImage.putFloat(val / 255.0f);
                inputImage.putFloat(((val >> 16) & 0xFF) / 255.0f);
                inputImage.putFloat(((val >> 8) & 0xFF) / 255.0f);
                inputImage.putFloat(((val) & 0xFF) / 255.0f);
            }
        }
    }

//    private float convertToGreyScale(int color) {
//        float r = ((color >> 16) & 0xFF);
//        float g = ((color >> 8) & 0xFF);
//        float b = ((color) & 0xFF);
//
//        int grayscaleValue = (int) (0.299f * r + 0.587f * g + 0.114f * b);
//        float preprocessedValue = grayscaleValue / 255.0f; // normalize the value by dividing by 255.0f
//        return preprocessedValue;
//    }

    /**
     * Run inference with the classifier model
     * Input is image
     * Output is an array of probabilities
     */
    private void runInference() {
        interpreter.run(inputImage, outputArray);
    }

    /**
     * Figure out the prediction of digit by finding the index with the highest probability
     *
     * @return
     */
    private String postprocess() {
        int houseNumberLen = -1;
        float maxProb = 0.0f;
//        int h = outputArray[0].length;
//        int w = outputArray[0][0].length;
        for (int j = 0; j < MAX_NUMBERS; j++) {
            if (outputArray[0][j][0] > maxProb) {
                maxProb = outputArray[0][j][0];
                houseNumberLen = j + 1;
            }
        }
        String houseNumber = "";
        for (int n = 0; n < houseNumberLen ; n++){
            maxProb = 0.0f;
            int currentDigit = 0;
            for (int j = 1; j < DIGITS; j++) {
                if (outputArray[0][n][j] > maxProb) {
                    maxProb = outputArray[0][n][j];
                    currentDigit = j - 1;
                }
            }
            houseNumber = houseNumber + String.valueOf(currentDigit);
        }
        return houseNumber;
    }

}

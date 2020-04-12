package com.mzm.sample.digit_recognizer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG_TAG = Classifier.class.getSimpleName();

//    private CustomView customView;
    private TextView resultTextView;
    private Classifier classifier;
    private Bitmap houseNumbersBitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button_clear).setOnClickListener(this);
        findViewById(R.id.button_classify).setOnClickListener(this);

        ImageView imageView = findViewById(R.id.sampleHouseNumbersImage);
//        houseNumbersBitmap = BitmapFactory.decodeResource(
//                getResources(), R.drawable.house_numbers_sample);
//        houseNumbersBitmap = Bitmap.createScaledBitmap(
//                houseNumbersBitmap, 128, 64, false);
//        imageView.setImageBitmap(houseNumbersBitmap);

        BitmapDrawable bitmapDrawable = ((BitmapDrawable) imageView.getDrawable());
        houseNumbersBitmap = bitmapDrawable.getBitmap();
        houseNumbersBitmap = Bitmap.createScaledBitmap(
                houseNumbersBitmap, 128, 64, false);

//        customView = findViewById(R.id.customView);
        resultTextView = findViewById(R.id.result);

        try {
            classifier = new Classifier(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_classify:
//                Bitmap scaledBitmap = customView.getBitmap(
//                        classifier.DIM_IMG_SIZE_X, classifier.DIM_IMG_SIZE_Y);
                String houseNumber = classifier.classify(houseNumbersBitmap);
                Log.i(LOG_TAG, houseNumber);
                resultTextView.setText(houseNumber);
                break;
            case R.id.button_clear:
//                customView.clear();
                resultTextView.setText("");
                break;
        }

    }

}

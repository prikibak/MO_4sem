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
import android.widget.Toast;

import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, IPickResult {

    private static final String LOG_TAG = Classifier.class.getSimpleName();

    private TextView resultTextView;
    private Classifier classifier;
    private ImageView sampleImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button_clear).setOnClickListener(this);
        findViewById(R.id.button_classify).setOnClickListener(this);

        sampleImageView = findViewById(R.id.sampleHouseNumbersImage);

        // Default image
        Bitmap houseNumbersBitmap = BitmapFactory.decodeResource(
                getResources(), R.drawable.house_numbers_sample);
        houseNumbersBitmap = Bitmap.createScaledBitmap(
                houseNumbersBitmap, 128, 64, false);
        sampleImageView.setImageBitmap(houseNumbersBitmap);

        sampleImageView.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View v) {
                    if (v.equals(sampleImageView)) {
                        Toast toast = Toast.makeText(
                                getApplicationContext(),
                                "Для лучшего результата нужно горизонтальное фото",
                                Toast.LENGTH_LONG
                        );
                        toast.show();
                        PickImageDialog.build(MainActivity.this).show(MainActivity.this);
//                        PickImageDialog.build(new PickSetup()).show(MainActivity.this);
                    }
                }
            }
        );

        resultTextView = findViewById(R.id.result);

        try {
            classifier = new Classifier(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            Bitmap houseNumbersBitmap = Bitmap.createScaledBitmap(
                    r.getBitmap(), 128, 64, false);
            sampleImageView.setImageBitmap(houseNumbersBitmap);

//            or
//            imageView.setImageURI(r.getUri());
        } else {
            //TODO: Handle possible errors;
        }
    }

    @Override
    public void onClick(View view) {
        Bitmap houseNumbersBitmap;
        switch (view.getId()) {
            case R.id.button_classify:
                BitmapDrawable bitmapDrawable = ((BitmapDrawable) sampleImageView.getDrawable());
                houseNumbersBitmap = bitmapDrawable.getBitmap();
//                houseNumbersBitmap = Bitmap.createScaledBitmap(
//                        houseNumbersBitmap, 128, 64, false);
                String houseNumber = classifier.classify(houseNumbersBitmap);
                Log.i(LOG_TAG, houseNumber);
                resultTextView.setText(houseNumber);
                break;
            case R.id.button_clear:
                houseNumbersBitmap = BitmapFactory.decodeResource(
                        getResources(), R.drawable.house_numbers_sample);
                houseNumbersBitmap = Bitmap.createScaledBitmap(
                        houseNumbersBitmap, 128, 64, false);
                sampleImageView.setImageBitmap(houseNumbersBitmap);
                resultTextView.setText("");
                break;
        }

    }

}

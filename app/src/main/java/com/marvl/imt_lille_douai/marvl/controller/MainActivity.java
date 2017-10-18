package com.marvl.imt_lille_douai.marvl.controller;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.marvl.imt_lille_douai.marvl.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    final String TAG = MainActivity.class.getName();

    final int captureActivityResult = 100;
    final int libraryActivityResult = 200;
    final int analyseActivityResult = 300;

    final int photoRequestActivityResult = 400;
    final int resultCodeActivityResult = 500;

    Button captureButton;
    Button libraryButton;
    Button analyseButton;

    ImageView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);

        captureButton = (Button) findViewById(R.id.captureButton);
        captureButton.setOnClickListener(this);

        libraryButton = (Button) findViewById(R.id.libraryButton);
        libraryButton.setOnClickListener(this);

        analyseButton = (Button) findViewById(R.id.analyseButton);
        analyseButton.setOnClickListener(this);

        photoView = (ImageView) findViewById(R.id.imageAnalysed);
        //photoView.setImageURI();
    }

    public void onClick(View view){
        if(view == findViewById(R.id.captureButton)){
            startCaptureActivity();
        }else if(view == findViewById(R.id.libraryButton)){
            startLibraryActivity();
        }else if(view == findViewById(R.id.analyseButton)){
            startAnalyseActivity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        if(requestCode == libraryActivityResult && resultCode == Activity.RESULT_OK){
            Uri photoUri = intent.getData();
            photoView.setImageURI(photoUri);
            Log.i(TAG,photoUri.toString());
        }
    }

    protected void startCaptureActivity(){
        Intent intent = new Intent();

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent,captureActivityResult);
    }

    protected void startLibraryActivity(){
        Intent intent = new Intent();

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent,libraryActivityResult);
    }

    protected void startAnalyseActivity(){
        Intent intent = new Intent();

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent,analyseActivityResult);
    }
}

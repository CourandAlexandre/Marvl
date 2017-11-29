package com.marvl.imt_lille_douai.marvl.controller;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.marvl.imt_lille_douai.marvl.BuildConfig;
import com.marvl.imt_lille_douai.marvl.R;
import com.marvl.imt_lille_douai.marvl.comparison.tools.GlobalVariables;
import com.marvl.imt_lille_douai.marvl.comparison.tools.SimilitudeTools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    final String TAG = MainActivity.class.getName();

    protected static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0;
    final int captureActivityResult = 100;
    final int libraryActivityResult = 200;
    final int analyseActivityResult = 300;

    final int photoRequestActivityResult = 400;
    final int resultCodeActivityResult = 500;

    Button captureButton;
    Button libraryButton;
    Button analyseButton;
    Button websiteButton;

    ImageView photoView;

    String photoTakenPath;
    Uri photoTakenUri;

    private static final String SHARED_PROVIDER_AUTHORITY = BuildConfig.APPLICATION_ID + ".fileprovider";

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

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            Log.i(TAG,"Permission wasn't allowed");
            captureButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE} , 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode == 0){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED ){
                captureButton.setEnabled(true); // If user doesn't have the permission the button is hidden
                Log.i(TAG,"Permission wasn't allowed but now is granted");
            }
        }
    }

    public void onClick(View view){
        if(view == findViewById(R.id.captureButton)){
            startCaptureActivity();
        }else if(view == findViewById(R.id.libraryButton)){
            startLibraryActivity();
        }else if(view == findViewById(R.id.analyseButton)){
            startAnalyseActivity();
        }else if(view == findViewById(R.id.websiteButton)){
            startWebsiteActivity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        switch (resultCode){
            case Activity.RESULT_OK :
                switch (requestCode){
                    case captureActivityResult :
                        photoView.setImageURI(photoTakenUri);

                    break;

                    case libraryActivityResult :
                        //processPhotoLibrary(intent);

                        //Log.i(TAG,intent.toString());

                        Uri photoUri = intent.getData();
                        photoView.setImageURI(photoUri);

                        Log.i(TAG,photoUri.toString());
                    break;

                    case analyseActivityResult :

                    break;
                }
            break;
        }
    }

    protected void processPhotoLibrary(Intent intent){
        Uri photoUri = intent.getData();
        String pathToPhoto = getRealPath(getApplicationContext(),photoUri);

        File pathToFile = new File(pathToPhoto);
        Bitmap photoBitmap = decodeFile(pathToFile); // err -> Maybe on path

        photoView.setImageBitmap(photoBitmap);

        Log.i(TAG,pathToPhoto);
    }

    protected void startCaptureActivity(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if(intent.resolveActivity(getPackageManager()) != null){
            File photoFile = null; // Create the File where the photo should go
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Continue only if the File was successfully created
            if(photoFile != null){
                //Uri photoUri = FileProvider.getUriForFile(this,SHARED_PROVIDER_AUTHORITY,photoFile);

                Uri photoUri = FileProvider.getUriForFile(this.getApplicationContext(), "com.marvl.imt_lille_douai.marvl.fileprovider", photoFile);

                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);



                startActivityForResult(intent, captureActivityResult);
            }
        }
    }

    protected void startLibraryActivity(){
        Intent intent = new Intent();

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent,libraryActivityResult);
    }

    protected void startAnalyseActivity(){

        String path = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath().substring(0,(int) getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath().length()-15) + "/app/src/main/res/drawable/TestImage/";

        Log.i(TAG,"truuuuuuc : " + getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath().substring(0,(int) getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath().length()-15) + "/app/src/main/res/drawable/TestImage/");

        String pathToBestComparisonImage = SimilitudeTools.getMostSimilitudeImageComparedToDataBank(photoTakenPath, path);

        Log.i("BestImgPath :",pathToBestComparisonImage);

        setContentView(R.layout.analyse_layout);

        websiteButton = (Button) findViewById(R.id.websiteButton);
        websiteButton.setOnClickListener(this);
    }

    protected void startWebsiteActivity(){



        //lance internet
        Uri uri = Uri.parse("http://www.google.com/#q=fish");

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);


        startActivity(intent);
    }

    protected String getRealPath(Context context, Uri uri) {
        Cursor cursor;

            String[] projection = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(
                uri,
                projection,
                null,
                null,
                null
            );

            int dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            return cursor.getString(dataIndex);
    }

    protected Bitmap decodeFile(File file){
        Bitmap bitmap = null;

        try{
            FileInputStream inputStream = new FileInputStream(file); // System.err

            BitmapFactory.Options options = new BitmapFactory.Options();
            bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    // Create an image file name
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName,".jpg",storageDir);

        // Save a file : path for use with ACTION_VIEW intents
        photoTakenPath = image.getAbsolutePath();
        photoTakenUri = Uri.fromFile(image);

        //galleryAddPic(image);

        //folder stuff
        /*File imagesFolder = new File(Environment.getExternalStorageDirectory(), "MyImages");
        imagesFolder.mkdirs();

        File image = new File(Environment.getExternalStorageDirectory(),"fname_" +
                String.valueOf(System.currentTimeMillis()) + ".jpg");
        Uri uriSavedImage = Uri.fromFile(image);*/

        return image;
    }

    private void galleryAddPic(File f) {
        Log.i(TAG,"TRUUUUC : " + f.getAbsolutePath());
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(photoTakenUri);
        this.sendBroadcast(mediaScanIntent);
    }
}

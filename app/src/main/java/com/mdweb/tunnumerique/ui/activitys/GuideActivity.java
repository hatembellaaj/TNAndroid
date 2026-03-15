package com.mdweb.tunnumerique.ui.activitys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.mdweb.tunnumerique.R;
import com.mdweb.tunnumerique.tools.SessionManager;
import com.mdweb.tunnumerique.tools.style.EditTextTypo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents a Guide Activity .
 *
 * @author SOFIENE ELBLAAZI
 * @author http://www.mdsoft-int.com
 * @version 1.0
 * @since 03-05-2017
 */
public class GuideActivity extends Activity implements View.OnClickListener {
    String filePath = null;
    private ImageView back;
    private ImageView imageAttachment;
    private Dialog dialog;
    private LinearLayout guideLayout;
    private TextView sendToServer;
    private TextView cancel;
    private EditTextTypo imageTitle;
    private int GaleryOrCamera;
    private long totalSize = 0;
    public File mFileTemp;

   // private UploadFileToServer uploadFTS;

    ////
    private Bitmap FixBitmap;
    private Bitmap btm;

    public static final String TEMP_PHOTO_FILE_NAME = "DCIM/Camera/20160307_143545.jpg";
    private static ProgressDialog pleaseWaitDialog;

    private ByteArrayOutputStream byteArrayOutputStream;

    String imageName = "imageName";
    String imageTag = "userfile";
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

     //   uploadFTS =new UploadFileToServer();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(GuideActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET}, 0);
        }
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME);
        }
        else {
            mFileTemp = new File(getFilesDir(), TEMP_PHOTO_FILE_NAME);
        }

        // get intent
        Intent intent = getIntent();
        GaleryOrCamera = (int) intent.getExtras().getSerializable("GaleryOrCamera");
        // initialize views
        initView();

    }

    /**
     * initialize view
     */
    public void initView() {
        guideLayout = (LinearLayout) findViewById(R.id.activity_guide);
        back = (ImageView) findViewById(R.id.back);

        sendToServer = (TextView) findViewById(R.id.send_to_server);
        imageTitle =(EditTextTypo) findViewById(R.id.image_name);
        imageAttachment = (ImageView) findViewById(R.id.imageView);
        cancel = (TextView) findViewById(R.id.cancel);
        guideLayout.setVisibility(View.INVISIBLE);
        byteArrayOutputStream = new ByteArrayOutputStream();

        sendToServer.setOnClickListener(this);
        cancel.setOnClickListener(this);
        back.setOnClickListener(this);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (GaleryOrCamera == 1) {
                guideLayout.setVisibility(View.VISIBLE);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


                try {
                    Uri mImageCaptureUri = null;
                    String state = Environment.getExternalStorageState();
                    if (Environment.MEDIA_MOUNTED.equals(state)) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
//                            mImageCaptureUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", mFileTemp);
//                        else
                        mImageCaptureUri = Uri.fromFile(mFileTemp);

                    } else {
                        mImageCaptureUri = Uri.parse("content://eu.janmuller.android.simplecropimage.example/");
                    }

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                    intent.putExtra("return-data", true);
//                startActivityForResult(Intent.createChooser(intent, "Sélectionner une image à  partir de la caméra"), REQUEST_CAMERA);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } catch (ActivityNotFoundException e) {

                }
            } else {
                guideLayout.setVisibility(View.VISIBLE);
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Sélectionner une image à  partir de la galerie"), SELECT_FILE);
            }
        }
    }


    @Override
    public void onClick(View v) {

//        switch (v.getId()) {
//
//            case R.id.send_to_server:
//                cancel.setEnabled(false);
//                // uploading the file to server
////                if (new Utils(this).getNetworkState()) {
////                    new UploadFileToServer().execute();
////                } else {
////                    cancel.setEnabled(true);
////                    dialogFinishUpload(getResources().getString(R.string.not_connected),false);
////                }
//                if (!new Utils(this).getNetworkState() ) {
//                    cancel.setEnabled(true);
//                    dialogFinishUpload(getResources().getString(R.string.not_connected), false);
//                }else if(uploadFTS.getStatus() != AsyncTask.Status.RUNNING)
//                    uploadFTS.execute();
//                else
//                    dialogFinishUpload(getResources().getString(R.string.envoie_on_cours), false);
//                break;
//            case R.id.cancel:
//                sendToServer.setEnabled(false);
//                finish();
//                break;
//            case R.id.back:
//                finish();
//                break;
//        }
    }


    /**
     * capture image from camera
     */
    @SuppressLint("SuspiciousIndentation")
    private void onCaptureImageResult() {

        filePath = mFileTemp.getPath();
        FixBitmap = BitmapFactory.decodeFile(filePath);
        btm = FixBitmap;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        FixBitmap = thumbnail;
        if(FixBitmap!=null )
        FixBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        else
        finish();

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageAttachment.setImageBitmap(FixBitmap);

    }

    @Override
    protected void onActivityResult(int RC, int RQC, Intent I) {
        super.onActivityResult(RC, RQC, I);
        if (RC == SELECT_FILE && RQC == RESULT_OK && I != null && I.getData() != null) {
            try {

                InputStream inputStream = getContentResolver().openInputStream(I.getData());
                FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
                copyStream(inputStream, fileOutputStream);
                fileOutputStream.close();
                if (inputStream != null) {
                    inputStream.close();
                }
                filePath = mFileTemp.getPath();
                FixBitmap = BitmapFactory.decodeFile(filePath);
                imageAttachment.setImageBitmap(FixBitmap);
            } catch (Exception e) {
            }
        } else if (RC == REQUEST_CAMERA) {
            if(RQC==RESULT_CANCELED){
                finish();
            }else {
                onCaptureImageResult();
            }

        } else
            finish();
    }

    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    /**
     *
     * @param text
     * @param isFinish
     */
    public void dialogFinishUpload(String text, final boolean isFinish) {
        //dismiss dialog
        if (dialog != null)
            dialog.dismiss();
        //cerate alert builder dialog
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.DialogSlideAnim);
        View view = (this).getLayoutInflater().inflate(R.layout.finish_upload_photo, null);
        alertDialog.setView(view);
        alertDialog.setCancelable(false);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        TextView message = (TextView) view.findViewById(R.id.load_photo);
        message.setText(text);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(isFinish)
                    finish();

            }
        });
        dialog = alertDialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int paddingPixel = 300;
        float density = getResources().getDisplayMetrics().density;
        int paddingDp = (int) (paddingPixel * density);
        //fix size of dialog
        dialog.getWindow().setLayout(paddingDp,
                WindowManager.LayoutParams.WRAP_CONTENT);
        //save parametre guide in SharedPreferences
        SessionManager.getInstance().setSavedGuide(this, "1");
    }


    /**
     * Uploading the file to server
     */
//    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
//        @Override
//        protected void onPreExecute() {
//            // setting progress bar to zero
//            cancel.setEnabled(false);
//            sendToServer.setEnabled(false);
//            if (pleaseWaitDialog == null )
//                pleaseWaitDialog = ProgressDialog.show(GuideActivity.this,
//                        "L'image est en cours de chargement",
//                        "S'il vous plaît, attendez",
//                        false);
//
//            super.onPreExecute();
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... progress) {
//
//            //Close the splash screen
//
//        }
//
//        @Override
//        protected String doInBackground(Void... params) {
//
//           // return uploadFile();
//        }

        @SuppressWarnings("deprecation")
//        private String uploadFile() {
//            String responseString = null;
//
//            HttpClient httpclient = getNewHttpClient();
//            HttpPost httppost = new HttpPost(Communication.URL_UPLOAD_IMAGE);
//
//
//            try {
//                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
//                        new AndroidMultiPartEntity.ProgressListener() {
//
//                            @Override
//                            public void transferred(long num) {
//                                publishProgress((int) ((num / (float) totalSize) * 100));
//                            }
//                        });
//
//                File sourceFile = new File(filePath);
//                // Adding file data to http body
//                entity.addPart(imageTag, new FileBody(sourceFile));
//                // Extra parameters if you want to pass to server
//                entity.addPart(imageName, new StringBody(imageTitle.getText().toString()));
//
//                totalSize = entity.getContentLength();
//               // httppost.setEntity(entity);
//
//                // Making server call
//              //  HttpResponse response = httpclient.execute(httppost);
//                HttpEntity r_entity = response.getEntity();
//
//                int statusCode = response.getStatusLine().getStatusCode();
//                if (statusCode == 200||statusCode == 201||statusCode == 202||statusCode == 203||statusCode == 204||statusCode == 205||statusCode == 206||statusCode == 207||statusCode == 208||statusCode == 226) {
//                    // Server response
//                    responseString = EntityUtils.toString(r_entity);
//                } else {
//                    responseString = "Error occurred! Http Status Code: "
//                            + statusCode;
//                }
//
//            } catch (ClientProtocolException e) {
//                responseString = e.toString();
//            } catch (IOException e) {
//                responseString = e.toString();
//            }
//
//            return responseString;
//
//        }

//        @Override
//        protected void onPostExecute(String result) {
//            cancel.setEnabled(true);
//            sendToServer.setEnabled(true);
//
//            if (pleaseWaitDialog != null) {
//                pleaseWaitDialog.dismiss();
//                pleaseWaitDialog = null;
//            }
//            // showing the server response in an alert dialog
//
//            if (result.contains("True")){
//                dialogFinishUpload(getResources().getString(R.string.partage_message),true);
//            }
//
//            else
//                dialogFinishUpload(getResources().getString(R.string.erreur_upload),false);
//
//            super.onPostExecute(result);
//        }
//
//    }

    /**
     *
     * @return
     */
//    public HttpClient getNewHttpClient() {
//        try {
//            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            trustStore.load(null, null);
//
//            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
//            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//
//            HttpParams params = new BasicHttpParams();
//            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
//            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
//
//            SchemeRegistry registry = new SchemeRegistry();
//            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//            registry.register(new Scheme("https", sf, 443));
//
//            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
//
//            return new DefaultHttpClient(ccm, params);
//        } catch (Exception e) {
//            return new DefaultHttpClient();
//        }
//    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 0:
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    finish();
                }
                else{
                    if (GaleryOrCamera == 1) {
                        guideLayout.setVisibility(View.VISIBLE);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


                        try {
                            Uri mImageCaptureUri = null;
                            String state = Environment.getExternalStorageState();
                            if (Environment.MEDIA_MOUNTED.equals(state)) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                    mImageCaptureUri =  FileProvider.getUriForFile(getApplicationContext(),getApplicationContext().getPackageName() + ".provider", mFileTemp);
                                else
                                    mImageCaptureUri = Uri.fromFile(mFileTemp);

                            } else {
                                mImageCaptureUri = Uri.parse("content://eu.janmuller.android.simplecropimage.example/");
                            }

                            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                            intent.putExtra("return-data", true);
//                startActivityForResult(Intent.createChooser(intent, "Sélectionner une image à  partir de la caméra"), REQUEST_CAMERA);
                            startActivityForResult(intent, REQUEST_CAMERA);
                        } catch (ActivityNotFoundException e) {

                        }
                    } else {
                        guideLayout.setVisibility(View.VISIBLE);
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Sélectionner une image à  partir de la galerie"), SELECT_FILE);
                    }

                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cancel.setEnabled(true);
        sendToServer.setEnabled(true);


    }
}

package com.example.administrator.pictureclipdemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    private char chars[]={'1','2'};
    private String[] strings={"拍照","相册"};
    private static  final int  ROAD_PICTURE = 2;
    private static final int CUT_PHOTO_REQUEST_CODE = 3;
    private String picName="";
    private String TAG = "MainActivity";
    private String imageUrl;
    private Bitmap uploadBitmap = null;
    private float dp;
    private ImageView show_cot_imv;
    private String path = "";
    private Uri photoUri = null;
    private static final int TAKE_PICTURE = 0;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 3:
                    Log.e(TAG,"uploadbitmap ="+uploadBitmap);
                    show_cot_imv.setImageBitmap(ImageCacheUtil
                            .toRoundBitmap(uploadBitmap));
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button selectBtn = (Button) findViewById(R.id.select_pic_resource_btn);
        show_cot_imv = (ImageView) findViewById(R.id.show_cut_picture);
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("MainActivity()","click choice btn");
                showChooseDialog();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG,"onResume()");

    }

    private void showChooseDialog(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle(R.string.choice_pic_resource);
        alertDialog.setItems(R.array.choice_pic_selector, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which ==0){
                    takePhoto();
                }else{
                    Intent mIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(mIntent,ROAD_PICTURE);
                }
            }
        });
        alertDialog.show();


    }

    private void takePhoto(){
        try {
            Intent openCameraIntent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);

            String sdcardState = Environment.getExternalStorageState();
            String sdcardPathDir = android.os.Environment
                    .getExternalStorageDirectory().getPath() + "/tempImage/";
            File file = null;
            if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
                // ��sd�����Ƿ���myImage�ļ���
                File fileDir = new File(sdcardPathDir);
                if (!fileDir.exists()) {
                    fileDir.mkdirs();
                }
                // �Ƿ���headImg�ļ�
                file = new File(sdcardPathDir + System.currentTimeMillis()
                        + ".JPEG");
            }
            if (file != null) {
                path = file.getPath();
                photoUri = Uri.fromFile(file);
                Log.e("=====", "��ȡ����ͼƬ��ַ ��" + photoUri);
                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                startActivityForResult(openCameraIntent, TAKE_PICTURE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                picName = startPhotoZoom(photoUri);
                break;
            case ROAD_PICTURE:
                if (data != null) {
                    Uri uri = data.getData();
                    Log.e(TAG,"uri = "+uri.toString());
                    if (uri != null) {
                        picName = startPhotoZoom(uri);
                    }
                }
                break;
            case CUT_PHOTO_REQUEST_CODE:
                if (resultCode == RESULT_OK && null != data) {
                    Bitmap bitmap = ImageCacheUtil.getLoacalBitmap(imageUrl);
                    System.out.println("bitmap = " + bitmap);
                    FileUtils.deleteDir(FileUtils.SDPATH);
                    uploadBitmap = ImageCacheUtil.createFramedPhoto(480, 480,
                            bitmap, (int) (dp * 1.6f)); //480*480 是保存图片的分辨率
                    FileUtils.saveBitmap(uploadBitmap, picName);
                    File file = new File(FileUtils.SDPATH, picName + ".JPEG");

                    //获取到这个file 文件后进行网络上传等操作，这里只是显示界面上！
                    if(file ==null){
                        Log.e(TAG,"file == null");}
                    else if(file != null)
                    {
                        Log.e(TAG,"file != null");
                        //show_cot_imv.setBackgroundResource(R.mipmap.ic_launcher);
                        show_cot_imv.setImageBitmap(ImageCacheUtil.toRoundBitmap(uploadBitmap));
                        Log.e(TAG,"filedir ="+FileUtils.SDPATH+picName+".JPEG");
                        /*String fileName =FileUtils.SDPATH+picName+".JPEG" ;
                        Bitmap bm = BitmapFactory.decodeFile(fileName);
                        show_cot_imv.setImageBitmap(bm);*/
                    }

                }
                break;
        }
    }

    private String startPhotoZoom(Uri uri) {
        try {
            Log.e("====", "uri = " + uri);

            SimpleDateFormat sDateFormat = new SimpleDateFormat(
                    "yyyyMMddhhmmss");
            String address = sDateFormat.format(new java.util.Date());
            if (!FileUtils.isFileExist("")) {
                FileUtils.createSDDir("");

            }
            imageUrl = FileUtils.SDPATH + address + ".JPEG";
            Log.e(TAG,"imageUrl ="+imageUrl);
            //1.从Uri获得文件路径
            Uri imageUri = Uri.fromFile(new File(imageUrl));

            //调用以下代码会跳转到Android系统自带的一个图片剪裁页面，点击确定之后就会得到一张图片
            final Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("crop", "true"); //cropString 发送裁剪信号
            intent.putExtra("aspectX", 1);     //aspectXintX 方向上的比例
            intent.putExtra("aspectY", 1);  // aspectYintY 方向上的比例
            intent.putExtra("outputX", 480);//outputXint  裁剪区的宽
            intent.putExtra("outputY", 480); //outputYint 裁剪区的高
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            intent.putExtra("outputFormat",
                    Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("noFaceDetection", false);
            intent.putExtra("return-data", false);
            startActivityForResult(intent, CUT_PHOTO_REQUEST_CODE);
            return address;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

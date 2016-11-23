package m.mcoupledate.classes.funcs;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

/**
 * Created by user on 2016/11/21.
 */

public class PinkPictureUploader
{
    private FragmentActivity context;

    private String siteTypeName;

    private Actioner picOutputer = null;




    //常數
    private final static int UPLOADPIC_CAMERA = 1, UPLOADPIC_ALBUM = 2;
    //照片路徑
    private String strImage;
    //php位置
    private ProgressDialog dialog = null;
    private int serverResponseCode = 0;

    //存圖片uri 之後拿來將圖片設定給相簿使用
    private Uri uriMyImage;


    public PinkPictureUploader(FragmentActivity context, String siteTypeName)
    {
        this.context = context;

        this.siteTypeName = siteTypeName;
    }


    public View.OnClickListener getUploadByCameraOnClickListener()
    {
        return  new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //開啟相簿相片集，須由startActivityForResult且帶入requestCode進行呼叫，原因為點選相片後返回程式呼叫onActivityResult
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                context.startActivityForResult(intent, UPLOADPIC_ALBUM);
            }
        };
    }

    public View.OnClickListener getUploadByAlbumOnClickListener()
    {
        return  new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //先判斷內置SD存不存在
                if(existSDCard()){
                    if(getSDFreeSize() > 25) {//看有沒有容量
                        //取得時間
                        Calendar c = Calendar.getInstance();

                        String path = getExtermalStoragePublicDir("Love%%GO").getPath();
                        //照片路徑
                        strImage = path + "/" + c.get(Calendar.YEAR) + (c.get(Calendar.MONTH) + 1) + c.get(Calendar.DAY_OF_MONTH) + c.get(Calendar.HOUR_OF_DAY) + c.get(Calendar.MINUTE) + c.get(Calendar.SECOND) + ".jpg";
                        File myImage = new File(strImage);
                        uriMyImage = Uri.fromFile(myImage);

                        Log.v("path", strImage);

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriMyImage);

                        context.startActivityForResult(intent, UPLOADPIC_CAMERA);
                    }
                    else
                        Toast.makeText(view.getContext(), "空間不足", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    public void setPicOutputer(Actioner picOutputer)
    {
        this.picOutputer = picOutputer;
    }


    public void submit(final String id, final ArrayList<Pair<String, Character>> pics)
    {
        /*       ↓       飛       ↓       */                //將資料送入資料庫
        dialog = ProgressDialog.show(context, "", "Uploading file...", true);
        new Thread(new Runnable() {
            public void run() {
                for (Pair<String, Character> pic : pics)
                {
                    uploadFile(pic.first, pic.second, id);
                }
                dialog.dismiss();
            }
        }).start();
                        /*       ↑       飛       ↑       */

    }








    /*       ↓       飛       ↓       */
    /**
     * 將圖片設定給相簿使用
     * @param contentUri
     */
    private void galleryAddPic(Uri contentUri) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    /**
     * 在公用資料夾創資料夾
     * @param albumName
     * @return
     */
    private File getExtermalStoragePublicDir(String albumName) {
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File f = new File(file, albumName);
        if(!f.exists()){
            f.mkdir();
            return f;
        }
        else
            return new File(file, albumName);
    }
    /**
     * 判斷SD存不存在
     * @return
     */
    private boolean existSDCard(){

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        else
            return false;
    }

    /**
     * SD卡剩餘空間
     * @return 傳回剩下的MB
     */
    public int getSDFreeSize(){
        //取得SD卡路徑
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        //獲取單個數據大小
        long blockSize = sf.getBlockSize();
        //空閒數據塊數量
        long freeBlocks = sf.getAvailableBlocks();

        Log.v("剩餘空間",""+(freeBlocks * blockSize)/1024/1024);

        return (int)((freeBlocks * blockSize)/1024/1024);
    }
    /**
     * 上傳檔案
     * @param sourceFileUri
     * @return
     */
    public int uploadFile(String sourceFileUri, char seq, String id){
        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        Log.d("HFFILENAME", siteTypeName + id + seq+ ".jpg");

        if (!sourceFile.isFile()) {
            dialog.dismiss();
            //Log.e("uploadFile", "Source File not exist :"+imagepath);
            Log.e("uploadFile", "Source File not exist :");

            context.runOnUiThread(new Runnable() {
                public void run() {
                    //messageText.setText("Source File not exist :"+ imagepath);
                }
            });
            return 0;
        }
        else
        {
            try {
                // open a URL connection to the Servlet
                //宣告權限 android 6 以上的要這個
                //不知道6之下的版本需不需要這個 不需要可能要判斷版本再進來
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(PinkCon.URL + "uploadPicture.php");

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                //更改圖片在sever的檔名

                fileName =  siteTypeName + id + seq+ ".jpg";


                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);


                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){

                    context.runOnUiThread(new Runnable() {
                        public void run() {
                            String msg = "File Upload Completed.\n\n See uploaded file your server. \n\n";
                            Toast.makeText(context, "File Upload Complete.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                context.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                context.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload Exception", "Exception : "  + e.getMessage(), e);
            }
//            dialog.dismiss();
            return serverResponseCode;

        } // End else block
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            /*       ↓       飛       ↓       */
            case UPLOADPIC_CAMERA:
                if (resultCode == RESULT_OK) {//避免點旁邊會閃退
                    galleryAddPic(uriMyImage);// 將圖片設定給相簿使用
                    Bitmap bmp = BitmapFactory.decodeFile(strImage);
                    //Log.v("path", strImage);
//                    accessPath = strImage;//儲存路徑

                    if (picOutputer!=null)
                        picOutputer.act(bmp, strImage);
//                    gaAdapter.add(bmp, strImage);

                }
                break;

            case UPLOADPIC_ALBUM:
                if (resultCode == RESULT_OK)
                {   //避免點旁邊會閃退
                    //取得照片路徑uri
                    Uri uri = data.getData();
                    ContentResolver cr = context.getContentResolver();
                    try
                    {
                        Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                        String[] pojo = {MediaStore.Images.Media.DATA};
                        Cursor cursor = context.managedQuery(uri, pojo, null, null, null);
                        if (cursor != null) {
                            int colunm_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                            cursor.moveToFirst();
//                            accessPath = cursor.getString(colunm_index);     //儲存路徑
                            if (picOutputer!=null)
                                picOutputer.act(bitmap, cursor.getString(colunm_index));
                            //showPicturePath.setText(path);
                        }

//                        gaAdapter.add(bitmap);

                    }
                    catch (FileNotFoundException e) {}
                }
                break;
            /*       ↑       飛       ↑       */


        }

    }


}

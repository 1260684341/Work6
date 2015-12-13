package comlert.example.administrator.work6;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.File;
import java.util.Vector;

public class AlbumActivity extends AppCompatActivity {

    private ViewFlipper flipper;
    private Bitmap[] mBgList;//图片存储列表
    private long startTime=0;
    private SensorManager sm;//重力感应硬件控制器
    private SensorEventListener sel;//重力感应侦听

    /*
    * 加载相册
    * */
    public String[] loadAlbum(){
        String pathName= android.os.Environment.getExternalStorageDirectory().getPath()+"/com.demo.pr4";
        //创建文件
        File file  = new File(pathName);
        Vector<Bitmap> fileName = new Vector<>();
        if (file.exists()&&file.isDirectory()){
            String[] str=file.list();
            for(String s:str){
                if(new File(pathName+"/"+s).isFile()){
                    fileName.addElement(loadImage(pathName+"/"+s));
                }
            }
            mBgList = fileName.toArray(new Bitmap[]{});
        }
        return null;
    }

    public Bitmap loadImage(String pathName){
        //读取照片，并对照片缩小
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //此时返回的bitmap为空
        Bitmap bitmap = BitmapFactory.decodeFile(pathName, options);
        //获取屏幕宽度
        WindowManager manager=getWindowManager();
        Display display = manager.getDefaultDisplay();
        //假设希望Bitmap的显示宽度为手机屏幕宽度
        int screenWidth=display.getWidth();
        //int screenHeight=display.getHeight();
        //设置Bitmap高度等比变化
        options.inSampleSize = options.outWidth / screenWidth;
        //将inJustDecodeBound设置为false,以便解码为Bitmap文件
        options.inJustDecodeBounds=false;
        //读取相片Bitmap
        bitmap = BitmapFactory.decodeFile(pathName,options);
        return bitmap;
    }
    //添加显示图片
    private View addImage(Bitmap bitmap){
        ImageView img=new ImageView(this);
        img.setImageBitmap(bitmap);
        return img;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        flipper  = (ViewFlipper)this.findViewById(R.id.ViewFlipper01);
        loadAlbum();
        if (mBgList==null){
            Toast.makeText(this,"相册无照片",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }else {
            for(int i=0;i<=mBgList.length-1;i++){
                flipper.addView(addImage(mBgList[i]),i,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));
            }
        }
        //获取重力感应器硬件控制器
        sm  = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //添加重力感应侦听
        sel =new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent se) {
                float x = se.values[SensorManager.DATA_X];
//                float y= se.values[SensorManager.DATA_Y];
//                float z= se.values[SensorManager.DATA_Z];
//                System.currentTimeMillis()>startTime+1000 //控制甩动的必须在1秒内只有一个甩动
                if (x>10&&System.currentTimeMillis()>startTime+1000){//右甩动
                    //记录甩动的开始时间
                    startTime = System.currentTimeMillis();
                    flipper.setInAnimation(AnimationUtils.loadAnimation(AlbumActivity.this,R.anim.push_right_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(AlbumActivity.this,R.anim.push_right_out));
                    flipper.showPrevious();
                }else if (x<-10&&System.currentTimeMillis()>startTime+1000){//左甩动
                    startTime=System.currentTimeMillis();
                    flipper.setInAnimation(AnimationUtils.loadAnimation(AlbumActivity.this,R.anim.push_left_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(AlbumActivity.this, R.anim.push_left_out));
                    flipper.showNext();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
        //注册Listener,SENSOR_DELAY_GAME为检测的精确度
        sm.registerListener(sel, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销重力感应侦听
        sm.unregisterListener(sel);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_album, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

package com.acarstudios.fener;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.acarstudios.fener.R;


public class MainActivity extends AppCompatActivity implements SensorEventListener
{

    // değişkenler oluşturuldu.
    private SensorManager sensorManager; // Yeni sensör verileri olduğunda, SensorManager'dan bildirim almak için kullanılır.
    private CameraManager cameraManager;
    private Float ChangedVale;
    private Sensor lightsensor;
    private String cameraid; // String: Sorgulanacak kamera cihazının kimliği. Bu, tarafından doğrudan açılabilen bağımsız bir kamera kimliği olmalıdır
    private Switch switch1btn;
    private Switch switch2btn;
    private Switch switch3btn;
    private LinearLayout linearrenk;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //değişkenler idlerle baglandı.
        sensorManager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        cameraManager=(CameraManager) getSystemService(Context.CAMERA_SERVICE);
        switch1btn = (Switch) findViewById(R.id.switch1);
        switch2btn = (Switch) findViewById(R.id.switch2);
        switch3btn = (Switch) findViewById(R.id.switch3);
        linearrenk= findViewById(R.id.linear1);
        switch1btn();
        switch2btn();
        switch3btn();

        try {
            cameraid=cameraManager.getCameraIdList()[0];
            /* getCameraIdList() : Diğer kamera API istemcileri tarafından kullanımda olabilecek kameralar
             da dahil olmak üzere tanımlayıcıya göre şu anda bağlı olan kamera cihazlarının listesini döndürün.*/

        }catch (CameraAccessException e){ // CameraAccessException : kamera cihazının bağlantısı kesildiyse.
            e.printStackTrace();
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)!= null){
            lightsensor= sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            // Sensor.TYPE_LIGHT : değerler[0]: SI lux birimlerinde ortam ışığı seviyesi
        }else {
            Toast.makeText(this,"Üzgünüm telefonunuzun ışık sensörü yok",Toast.LENGTH_SHORT).show();
        }


    }


    public void switch1btn(){

        switch1btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b==true){switch2btn.setChecked(false);

                    if (ChangedVale < 50) { //sensor degeri 50 den kucukse
                        flashon();
                    } else { // degilse

                        flashoff();
                    }

                }
                else{flashoff();}
            }
        });
    }

    public void switch2btn(){

        switch2btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    switch1btn.setChecked(false);
                    flashon();}
                else{ // degilse

                    flashoff();}
            }
        });
    }
    //

    public void switch3btn(){
        switch3btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean d) {
                if (d){
                    switch3btn.setChecked(true);
                    linearrenk.setBackgroundColor(Color.WHITE);
                    switch1btn.setTextColor(Color.BLACK);
                    switch2btn.setTextColor(Color.BLACK);
                    switch3btn.setTextColor(Color.BLACK);
                }
                else {
                    switch1btn.setTextColor(Color.WHITE);
                    switch2btn.setTextColor(Color.WHITE);
                    switch3btn.setTextColor(Color.WHITE);
                    linearrenk.setBackgroundColor(434343);
                }
            }
        });

    }



    //

    public void flashon(){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraid,true);
                //setTorchMode(String cameraId, boolean enabled)
                //Kamera cihazını açmadan verilen kimliğin kamerasının flaş ünitesinin el feneri modunu true ayarlayın.
            }
        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }

    public void flashoff(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                cameraManager.setTorchMode(cameraid,false);
                //Kamera cihazını açmadan verilen kimliğin kamerasının flaş ünitesinin el feneri modunu false ayarlayın.

            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override// Yeni bir sensör olayı olduğunda çağrılır.
    public void onSensorChanged(SensorEvent sensorEvent) {
        ChangedVale = sensorEvent.values[0];


        if (switch1btn.isChecked()) {
            switch2btn.setChecked(false);


            if (ChangedVale < 50) { //sensor degeri 50 den kucukse
                flashon();
            } else { // degilse

                flashoff();
            }
        }
    }

    @Override// Kayıtlı sensörün doğruluğu değiştiğinde çağrılır.
    // onSensorChanged()'den farklı olarak, bu yalnızca bu doğruluk değeri değiştiğinde çağrılır.
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,lightsensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override // uygulama durdugunda sensorleri durdur. yoksa sensor calısmaya devam eder ve pili bitirir...
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
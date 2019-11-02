package com.example.recognition;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;


//import com.google.android.gms.vision.CameraSource;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SurfaceView cameraView;
    private Button btn;
    private MyCameraSource cameraSource;
    private CCtrlValue mCtrlValue;
    private  TextView textBlockContent;
    private ProcessorText processorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraView = (SurfaceView) findViewById(R.id.surface_view);
        // cameraView = new CameraPreview(this, )
        textBlockContent = (TextView) findViewById(R.id.text_value);
        ViewfinderView focusBox = (ViewfinderView) findViewById(R.id.viewfinder);
        btn = (Button) findViewById(R.id.buttonClr);
        focusBox.setViewParent(cameraView);

        createSpinner();

        btn.setOnClickListener(this);
        final Button btnSetting = (Button) findViewById(R.id.btnSetting);
        btnSetting.setOnClickListener(this);

        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Log.w("MainActivity", "Detector dependencies are not yet available.");
        }

     //   ConvertValue myServise = ConvertValue.getInstance(textBlockContent);

        mCtrlValue = Valuta.getInstance(textBlockContent, this);

        processorText = new ProcessorText(mCtrlValue.getMyServise());

        textRecognizer.setProcessor(processorText);
        //   processorText.setMyServise(myServise);

        cameraSource = new MyCameraSource(getApplicationContext(), focusBox, textRecognizer);

        SurfaceHolder holder = cameraView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    //noinspection MissingPermission
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            @SuppressLint("MissingPermission")
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                cameraSource.stop();
                try {
                    //noinspection MissingPermission
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }

        });

    }


    private void createSpinner() {
        String[] data = {"Валюта", "Ед. изм.", "Текст"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final Spinner spinner = (Spinner) findViewById(R.id.spinner_id);
        spinner.setAdapter(adapter);
        // заголовок
        spinner.setPrompt("Title");
        // выделяем элемент
        spinner.setSelection(0);
        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // показываем позиция нажатого элемента
                //  Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
                final Button btnSetting = (Button) findViewById(R.id.btnSetting);
                switch (position) {
                    case 0:
                        mCtrlValue = Valuta.getInstance(textBlockContent, MainActivity.this);
                        btnSetting.setEnabled(true);
                        break;
                    case 1:
                        mCtrlValue = CEU.getInstance(textBlockContent, MainActivity.this);
                        btnSetting.setEnabled(true);
                        break;
                    case 2:
                        mCtrlValue = CCtrlText.getInstance(textBlockContent, MainActivity.this);
                        btnSetting.setEnabled(false);

                        break;
                    default:
                        return;
                }
                processorText.setMyServise(mCtrlValue.getMyServise());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }





    @Override
    protected void onResume() {
        super.onResume();
        if (cameraSource != null) return;
        try {
            //noinspection MissingPermission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            cameraSource.start(cameraView.getHolder());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
       /* if (cameraSource != null) {
            cameraSource.stop();
            cameraSource = null;
        }*/
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (cameraSource != null) {
            cameraSource.stop();
            cameraSource = null;
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonClr: {
                if ( mCtrlValue.getMyServise().isHandle() ) {
                    btn.setText("Снять");
                    mCtrlValue.getMyServise().printTextBegin();

                } else {
                    cameraSource.takePicture();
                    btn.setText("Очистить");
                }
                break;

            }
            case R.id.btnSetting:

                Intent intent = new Intent(this, ListViewActivity.class);
                intent.putParcelableArrayListExtra("arraylist", ((CCtrlListValue)mCtrlValue).getList());
                startActivityForResult(intent, 1);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(data == null) return;
        int position = data.getIntExtra("position", 0);
        ((CCtrlListValue)mCtrlValue).selectItem(position);

    }


}

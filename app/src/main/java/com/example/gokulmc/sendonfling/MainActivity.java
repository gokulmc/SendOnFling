package com.example.gokulmc.sendonfling;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;



import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    private EditText etContent;
    private TextView tvResult;
    private Button btnUpdate;
    private String url;

    SwipeGestureListener gestureListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etContent = (EditText) findViewById(R.id.etContent);
        tvResult = (TextView) findViewById(R.id.tvResult);
        gestureListener = new SwipeGestureListener(MainActivity.this);
        tvResult.setOnTouchListener(gestureListener);



        View.OnClickListener btnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = "http://" + etContent.getText().toString();
                Toast.makeText(MainActivity.this, "URL updated", Toast.LENGTH_SHORT).show();
            }
        };
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(btnListener);

    }

    class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener implements
            View.OnTouchListener {
        Context context;
        GestureDetector gDetector;
        static final int SWIPE_MIN_DISTANCE = 120;
        static final int SWIPE_MAX_OFF_PATH = 250;
        static final int SWIPE_THRESHOLD_VELOCITY = 200;

        public SwipeGestureListener(Context context) {
            this(context, null);
        }

        public SwipeGestureListener(Context context, GestureDetector gDetector) {

            if (gDetector == null)
                gDetector = new GestureDetector(context, this);

            this.context = context;
            this.gDetector = gDetector;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
                if (Math.abs(e1.getX() - e2.getX()) > SWIPE_MAX_OFF_PATH
                        || Math.abs(velocityY) < SWIPE_THRESHOLD_VELOCITY) {
                    return false;
                }
                if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE) {
                    tvResult.setText("ON1");
                    new PostTask().execute(url, "ON1");
                } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE) {
                    tvResult.setText("OFF1");
                    new PostTask().execute(url, "OFF1");
                }
            } else {
                if (Math.abs(velocityX) < SWIPE_THRESHOLD_VELOCITY) {
                    return false;
                }
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE) {
                    tvResult.setText("ON2");
                    new PostTask().execute(url, "ON2");

                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE) {
                    tvResult.setText("OFF2");
                    new PostTask().execute(url, "OFF2");
                }
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            gDetector.onTouchEvent(event);
            return true;
        }

        public GestureDetector getDetector() {
            return gDetector;
        }

    }

    private class PostTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... data) {
            try {
                final String a = data[0];

                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        etContent.setText(a);;
                    }
                });

                URL url = new URL(data[0]+"/?pin="+data[1]);
                URLConnection connection = url.openConnection();
//                if(connection instanceof HttpURLConnection) {
//                    MainActivity.this.runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            etContent.setText("True");
//                        }
//                    });
//                }
                HttpURLConnection conn = (HttpURLConnection) connection;
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
//                OutputStream os = conn.getOutputStream();
//
//                //BufferedWriter writer = new BufferedWriter(
//                //      new OutputStreamWriter(os, "UTF-8"));
//
//                final String param =   URLEncoder.encode("pin="+data[1], "UTF-8");
//
//                os.write(param.getBytes("UTF-8"));
//                os.flush();
//                //writer.close();
//                os.close();

                final String sa = conn.getResponseMessage();
//                final String sa = data[1];
                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Http Response : " + sa, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                //Log.e("Goku", Log.getStackTraceString(e));
                final String message = e.getMessage();
                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
//                        tvResult.setText(message);;

                    }
                });
            }
            return "DONE";
        }

    }
}
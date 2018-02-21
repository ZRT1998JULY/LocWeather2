package com.example.locweather2;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

public class MainActivity extends AppCompatActivity{
    private Button button;
    private TextView textView;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String re = (String) msg.obj;
                    textView.setText(re);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TAG", "点击了Button");
                sendRequestWithHttpClient();
            }
        });
    }

    private void sendRequestWithHttpClient() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL("http://api.map.baidu.com/telematics/v3/weather?location=南昌&output=json&ak=8ixCCFzlBB617YX7tONI2P5B&mcode=1C:6B:42:33:E8:A6:DC:A2:11:6E:26:EC:84:BD:42:E3:8E:6B:57:9A;com.example.administrator.jsontest");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Log.i("TAG", response.toString());                    parseJSONObjectOrJSONArray(response.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //解析JSON数据
    private void parseJSONObjectOrJSONArray(String jsonData) {
        try {
            String count = "";
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            if (jsonArray.length() > 0) {
                JSONObject object = jsonArray.getJSONObject(0);
                String city = object.optString("currentCity");
                JSONArray array = object.getJSONArray("weather_data");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject1 = array.getJSONObject(i);
                    String dateDay = jsonObject1.optString("date");
                    String weather = jsonObject1.optString("weather");
                    String wind = jsonObject1.optString("wind");
                    String temperature = jsonObject1.optString("temperature");
                    count =count +"\n"+ dateDay + " " + weather + " " + wind + " " + temperature;
                    Log.i("AAA",count);
                }

                Message message = new Message();
                message.what = 0;
                message.obj = count;
                handler.sendMessage(message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
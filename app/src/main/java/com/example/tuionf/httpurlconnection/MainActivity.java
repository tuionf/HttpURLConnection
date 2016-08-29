package com.example.tuionf.httpurlconnection;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button send;
    private TextView response;
    private static final int SHOW = 0;
    private static final String TAG = "MainActivity";

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SHOW:
                    String responseStr = (String)  msg.obj;
                    //更新UI操作
                    Log.d(TAG, "handleMessage: 更新UI");
                    response.setText(responseStr);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        send = (Button) findViewById(R.id.send_request);
        response = (TextView) findViewById(R.id.response);

        send.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.send_request){
            sendRequestWithHttpURLConnection();
            Log.d(TAG, "onClick: ");
        }
    }

    private void sendRequestWithHttpURLConnection(){
        //开启新线程发送网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;

                try {

                    URL url = new URL("http://www.baidu.com");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(30000);
                    connection.setReadTimeout(30000);

                    InputStream is = connection.getInputStream();
                    //读取输入流
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
                    StringBuffer stringBuffer = new StringBuffer();

                    String line;
                    while ((line = bufferedReader.readLine()) != null){
                        Log.d("Main",line);
                        Log.d(TAG, "run: + readline");
                        Log.d(TAG, "run: "+line);
                        stringBuffer.append(line);
                    }

                    Message message = new Message();
                    message.what = SHOW;
                    //将服务器返回的结果存放到message
                    message.obj = stringBuffer.toString();
                    mHandler.sendMessage(message);


                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(connection != null){
                        Log.d(TAG, "run: "+"关闭");
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}

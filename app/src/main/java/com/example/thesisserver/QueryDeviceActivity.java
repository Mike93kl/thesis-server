package com.example.thesisserver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import QueryManager.QueryManager;
import QueryManager.response.QueryResult;
import QueryManager.response.WearableResult;
@SuppressWarnings(value = "unchecked")
public class QueryDeviceActivity extends AppCompatActivity {

    QueryManager queryManager = QueryManager.getInstance();
    String deviceName;
    TextView queryResultView;
    TextView queryView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_device);
        Intent intent = getIntent();
        deviceName = intent.getStringExtra("deviceName");
        queryResultView = findViewById(R.id.result);
        queryView = findViewById(R.id.query);
        queryManager.setWearableResult(new WearableResult() {
            @Override
            public void onResult(final QueryResult result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(QueryDeviceActivity.this, "Result arrived", Toast.LENGTH_SHORT).show();
                        try{
                            showResult_f( (HashMap<String, float[]>) result.getResult());
                        }catch (Exception e){
                            showResult_F( (HashMap<String, Float[]>) result.getResult());
                        }
                    }
                });
            }

            @Override
            public void onResult(byte[] rawData) {

            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onDeviceConnected(String deviceName) {

            }

            @Override
            public void onDeviceDisconnected(String deviceName) {

            }
        });
    }

    public void send(View view) throws Exception {
        queryManager.sendQuery(deviceName , queryView.getText().toString());
        Toast.makeText(this, "Query was sent", Toast.LENGTH_SHORT).show();
    }

    private void showResult_f(HashMap<String,float[]> map) {
        String res = "";
        for (Map.Entry<String,float[]> entry: map.entrySet()) {
            res += entry.getKey() + " : " + entry.getValue()[0] + "\n";
        }
        queryResultView.setText(res);
    }

    private void showResult_F(HashMap<String,Float[]> map) {
        String res = "";
        for (Map.Entry<String,Float[]> entry: map.entrySet()) {
            res += entry.getKey() + " : " + entry.getValue()[0] + "\n";
        }
        queryResultView.setText(res);
    }
}

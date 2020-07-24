package com.example.thesisserver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import QueryManager.Connection.BluetoothServer;
import QueryManager.Connection.ConnectionType;
import QueryManager.QueryManager;
import QueryManager.response.QueryResult;
import QueryManager.response.WearableResult;


import static QueryManager.QueryManager.log;

@SuppressWarnings(value = "unchecked")
public class MainActivity extends AppCompatActivity {

    QueryManager queryManager = QueryManager.getInstance();
    RecyclerView deviceListView;
    LinearLayout loadingLayout;

    List<String> deviceArray = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        deviceListView = findViewById(R.id.deviceListView);
        loadingLayout = findViewById(R.id.loadingLayout);
        try{
            queryManager = QueryManager.getInstance();
            queryManager.init(this,ConnectionType.BLUETOOTH);
            queryManager.setWearableResult(new WearableResult() {
                @Override
                public void onResult(QueryResult result) {

                }

                @Override
                public void onResult(byte[] rawData) {

                }

                @Override
                public void onError(Exception e) {

                }

                @Override
                public void onDeviceConnected(final String deviceName) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addDeviceToList(deviceName);
                            return;
                        }
                    });
                }

                @Override
                public void onDeviceDisconnected(String deviceName) {
                }
            });
            queryManager.initiateServer();
        }catch (Exception e){
            e.printStackTrace();

        }
    }

    private void addDeviceToList(String deviceName){
        if(deviceArray == null) {
            deviceArray = new ArrayList<>();
            loadingLayout.setVisibility(View.GONE);
            deviceListView.setVisibility(View.VISIBLE);
        }
        deviceArray.add(deviceName);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this,deviceArray);
        deviceListView.setAdapter(adapter);
        deviceListView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void launchWebServer(View view){
        Intent intent = new Intent(this,WebServerActivity.class);
        startActivity(intent);
    }
}

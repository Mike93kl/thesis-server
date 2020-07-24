package com.example.thesisserver;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import QueryManager.QueryManager;
import QueryManager.response.QueryResult;
import QueryManager.response.WearableResult;

public class WebServerActivity extends AppCompatActivity {

    //private final String SOCKET_SERVER_URL = "https://enigmatic-plains-90686.herokuapp.com/";
    private final String SOCKET_SERVER_URL = "http://192.168.10.1:8080";
    private QueryManager queryManager;
    private Socket socket;
    private String code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_server);

        try {
            queryManager = QueryManager.getInstance();
            socket = IO.socket(SOCKET_SERVER_URL);
            configureEvents();
            socket.connect();
            configureWearableResult();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not initialize socket, connection Failed", Toast.LENGTH_SHORT).show();
            finish();
        }


    }

    private void configureWearableResult() {
        queryManager.setWearableResult(new WearableResult() {
            @Override
            public void onResult(QueryResult result) {
                socket.emit("queryResult", new JSONObject(result.getResult()));
            }

            @Override
            public void onResult(byte[] rawData) {

            }

            @Override
            public void onError(Exception e) {
                socket.emit("queryError", "Error processing query");
            }

            @Override
            public void onDeviceConnected(String deviceName) {

            }

            @Override
            public void onDeviceDisconnected(String deviceName) {

            }
        });
    }

    private void configureEvents() {

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                socket.emit("create_room");
            }
        });



        socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("error","error");
            }
        });

        socket.on("room_created", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setCodeOnTextView((String) args[0]);
                    }
                });
            }
        });

        socket.on("joined_in_room", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addUser((String) args[0]);
                    }
                });

                JSONObject devices = new JSONObject();
                try {
                    devices.put("devices", new JSONArray(queryManager.getConnectedDevicesNameList()));
                    socket.emit("deviceList", devices );
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        socket.on("serverQuery", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    queryManager.sendQuery(data.getString("deviceName"), data.getString("query"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private void setCodeOnTextView(String code){
        this.code = code;
        TextView view = findViewById(R.id.connCodeView);
        view.setText("Enter \n'" + code + "'\n to Join Connection From a Browser");

    }

    private void addUser(String user){
        TextView view = findViewById(R.id.userListHeader);
        view.setText(view.getText().toString() + "\n" + user);
    }
}

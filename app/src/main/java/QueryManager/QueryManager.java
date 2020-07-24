package QueryManager;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import QueryManager.Connection.ConnectionManager;
import QueryManager.Connection.ConnectionType;
import QueryManager.Connection.actions.DataExchange;
import QueryManager.Connection.data.ConvertData;
import QueryManager.response.QueryResult;
import QueryManager.response.ResponseState;
import QueryManager.response.WearableResult;

@SuppressWarnings(value = "unchecked")
public class QueryManager {

    private interface  RESPONSE_INDEX {
        int STATE = 0;
        int DATA  = 1;
    }
    public static final String TAG = "QUERY_MANAGER_SERVER";
    public static void log(String msg) {
        Log.d(TAG, msg);
    }
    private static QueryManager instance = new QueryManager();
    public static QueryManager getInstance(){return instance;}
    private Boolean debug = true;
    private ConnectionManager connection;
    private DataExchange dataExchange;
    private QueryManager(){ initializeDataExchange();}
    private WearableResult wearableResult;
    public void init(Context context, ConnectionType connectionType){
        setConnectionType(context,connectionType);
    }
    private void setConnectionType(Context context, ConnectionType type){
        try{
            switch (type){
                case NSD:
                    this.connection = ConnectionManager.nsd(context);
                    break;
                case BLUETOOTH:
                    this.connection = ConnectionManager.bluetooth();
                    break;
                default:
                    break;
            }
            this.connection.setDataExchange(dataExchange);
        }catch (Exception e){
            throw new RuntimeException("Could not initiate connection type object, system broke with msg: " + e.getMessage());
        }
    }

    private void initializeDataExchange(){
        dataExchange = new DataExchange() {
            @Override
            public void onSendSuccess(String from, byte[] data) {
                if(debug) {
                    log("SUCCESSFULLY SENT DATA TO " + from);
                }
            }

            @Override
            public void onSendError(String from, Exception e) {
                if(debug) {
                    log("ERROR SENDING DATA TO " + from);
                }
            }

            @Override
            public void onReceivedData(String from, byte[] data, int numBytes) {
                if(debug) {
                    log("RECEIVED DATA TO " + from);
                }
                handleReceivedData(from,data, numBytes);
            }

            @Override
            public void onReceivedDataError(String from, Exception e) {
                e.printStackTrace();
                if(debug) {
                    log("ERROR RECEIVING DATA FROM: " + from +". (Printed stack trace)");
                }
            }

            @Override
            public void onSocketError(Exception e) {
                e.printStackTrace();
                if(debug) {
                    log("ERROR FROM SOCKET. (Printed stack trace)" );
                }
            }

            @Override
            public void onDeviceConnected(String deviceName) {
                if(debug) {
                    log("DEVICE '" + deviceName + "' JUST CONNECTED!");
                }
                wearableResult.onDeviceConnected(deviceName);
            }

            @Override
            public void onDeviceDisconnected(String deviceName) {
                if(debug) {
                    log("DEVICE '" + deviceName + "' DISCONNECTED!");
                }
                wearableResult.onDeviceDisconnected(deviceName);
            }
        };
    }


    public WearableResult getWearableResult() {
        return wearableResult;
    }

    public void setWearableResult(WearableResult wearableResult) {
        this.wearableResult = wearableResult;
    }
    public Boolean isInDebugMode() {
        return debug;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    private void handleReceivedData(String from, byte[] data, int numBytes) {
        try{
            ArrayList<byte[]> receivedArray = (ArrayList<byte[]>) ConvertData.toObject(data);
            // get state
            ResponseState state = (ResponseState) ConvertData.toObject(receivedArray.get(RESPONSE_INDEX.STATE));
            switch (state) {
                case QUERY_RESULT:
                    try{
                        QueryResult queryResult = (QueryResult) ConvertData.toObject(receivedArray.get(RESPONSE_INDEX.DATA));
                        wearableResult.onResult(queryResult);
                    }catch (Exception e){
                        wearableResult.onResult(receivedArray.get(RESPONSE_INDEX.DATA));
                    }
                    break;
                case SENSOR_MAP:
                    if(debug){
                        log("RECEIVED SENSOR MAP FROM " + from);
                    }
                    break;
                case ERROR:
                    wearableResult.onError(new Exception("Error Response from wearable"));
                    break;
            }
        }catch (Exception e) {
            e.printStackTrace();
            wearableResult.onError(e);
        }
    }

    public void sendQuery(String to ,String s) throws Exception {
        this.connection.send(to , ConvertData.toByteArray(s));
    }

    public void initiateServer(){
        this.connection.start();
    }

    public List<String> getConnectedDevicesNameList(){
        return this.connection.getConnectedDevicesList();
    }

}

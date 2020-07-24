package QueryManager.Connection;

import android.bluetooth.BluetoothAdapter;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import QueryManager.Connection.actions.ConnectionHandlerImpl;
import QueryManager.Connection.actions.Server;

public class BluetoothServer extends ConnectionHandlerImpl<BluetoothSocket> implements Server {

    private BluetoothAdapter _adapter;
    private UUID uuid = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
    private String APP_NAME = "wearQM-2";
    private Thread _listenThread;
    private Boolean _serverRunning = false;

    public static String getBluetoothName(){
        Boolean isEnabled = BluetoothAdapter.getDefaultAdapter().isEnabled();
        try{
            if(!isEnabled) {
                BluetoothAdapter.getDefaultAdapter().enable();
            }
            String bluetoothName = BluetoothAdapter.getDefaultAdapter().getBondedDevices().iterator()
                    .next().getName();
            return bluetoothName;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            if(!isEnabled) {
                BluetoothAdapter.getDefaultAdapter().disable();
            }
        }
    }

    public BluetoothServer() throws Exception {
        super();
        _adapter = BluetoothAdapter.getDefaultAdapter();
        if( _adapter == null ) throw new Exception("DEVICE DOES NOT SUPPORT BLUETOOTH");
        if( !_adapter.isEnabled() )
            _adapter.enable();
    }


    @Override
    public void start(){
        try{
            BluetoothServerSocket serverSocket = _adapter.listenUsingRfcommWithServiceRecord(APP_NAME,uuid);
            _listenThread = new Thread(new ListenForConnections(serverSocket));
            _listenThread.start();
            _serverRunning = true;
        }catch (IOException e){
            e.printStackTrace();
            Log.d("SERVER", "ERROR CHECK STACK TRACE");
        }
    }

    public Boolean isRunning(){
        return _listenThread.isAlive() && _serverRunning;
    }

    @Override
    public void stop(){
        if(_listenThread != null)
            _listenThread.interrupt();
        _serverRunning = false;
    }

    @Override
    public void send(String deviceName, byte[] data) {
        sendToDevice(deviceName,data);
    }


    private class ListenForConnections implements Runnable {
        private final BluetoothServerSocket serverSocket;
        ListenForConnections(BluetoothServerSocket serverSocket){
            this.serverSocket = serverSocket;
        }
        @Override
        public void run() {
            BluetoothSocket socket;
            while (true){
                try {
                    socket = serverSocket.accept();
                    if(socket != null ){
                        addDeviceAndAccept(socket.getRemoteDevice().getName(), socket);
                        onDeviceConnected(socket.getRemoteDevice().getName());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("SERVER","ERROR ON ACCEPT");
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

            }
        }

    }


}

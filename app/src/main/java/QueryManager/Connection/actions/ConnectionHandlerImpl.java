package QueryManager.Connection.actions;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import QueryManager.Connection.data.AcceptData;
import QueryManager.Connection.data.SendData;

public class ConnectionHandlerImpl<T> implements ConnectionHandler<T> {


    protected Map<String,DeviceThreadHolder<T>> connections;
    protected List<String> connectedDevicesNames = new ArrayList<>();
    protected DataExchange dataExchange;

    protected ConnectionHandlerImpl(){
        connections = new HashMap<>();
    }

    @Override
    public void sendToDevice(String deviceName, byte[] data) {
        final DeviceThreadHolder<T> holder = connections.get(deviceName);
        try{

            new Thread(new SendData<T>(holder.getSocket(),data) {
                @Override
                public void onSuccess(byte[] data) {
                    dataExchange.onSendSuccess(holder.getNameOfSocketDevice(),data);
                }

                @Override
                public void onError(IOException e) {
                    dataExchange.onSendError(holder.getNameOfSocketDevice(),e);
                }
            }).start();
        }catch (Exception e){
            e.printStackTrace();
            dataExchange.onSendError(holder.getNameOfSocketDevice(), e);
        }
    }

    @Override
    public T getSocketOf(String deviceName) {
        return getConnections().get(deviceName).getSocket();
    }

    @Override
    public void onDeviceConnected(String deviceName) {
        dataExchange.onDeviceConnected(deviceName);
    }

    @Override
    public void onDeviceDisconnected(String deviceName) {
        dataExchange.onDeviceDisconnected(deviceName);
    }

    public List<String> getConnectedDevicesNames(){
        return connectedDevicesNames;
    }

    public DataExchange getDataExchange() {
        return dataExchange;
    }

    public void setDataExchange(DataExchange dataExchange) {
        this.dataExchange = dataExchange;
    }

    public Map<String, DeviceThreadHolder<T>> getConnections() {
        return connections;
    }

    public void setConnections(Map<String, DeviceThreadHolder<T>> connections) {
        this.connections = connections;
    }

    public void addDeviceAndAccept(String deviceName, T socket) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        connectedDevicesNames.add(deviceName);
        DeviceThreadHolder<T> holder = new DeviceThreadHolder<>();
        holder.setSocket(socket);
        holder.accept();
        connections.put(deviceName,holder);
    }

    /** class that holds socket and accepting thread */

    protected class DeviceThreadHolder<T> {
        private T socket;
        private Thread thread;



        public String getNameOfSocketDevice() {
            if(socket instanceof BluetoothSocket) {
                return ((BluetoothSocket) socket).getRemoteDevice().getName();
            }
            if(socket instanceof Socket) {
                return ((Socket) socket).getInetAddress().getHostName();
            }
            return null;
        }

        public T getSocket() {
            return socket;
        }

        public void setSocket(T socket) {
            this.socket = socket;
        }

        public Thread getThread() {
            return thread;
        }

        public void setThread(Thread thread) {
            this.thread = thread;
        }

        public void accept() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
            this.thread = new Thread(new AcceptData<T>(this.socket) {
                @Override
                public void onDataReceived(byte[] data, int numBytes) {
                    dataExchange.onReceivedData(getNameOfSocketDevice(), data, numBytes);
                }

                @Override
                public void onErrorReadingData(IOException e) {
                    e.printStackTrace();
                    dataExchange.onReceivedDataError(getNameOfSocketDevice(), e);
                }
            });
            this.thread.start();
        }
    }


}

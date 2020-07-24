package QueryManager.Connection;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import QueryManager.Connection.actions.ConnectionHandlerImpl;
import QueryManager.Connection.actions.DataExchange;
import QueryManager.Connection.actions.Server;

public class ConnectionManager<T extends ConnectionHandlerImpl & Server> implements Server{

    private T connection;

    public static ConnectionManager<BluetoothServer> bluetooth() throws Exception {
        return new ConnectionManager<>();
    }

    public static ConnectionManager<NsdServer> nsd(Context context) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, IOException {
        return new ConnectionManager<>(context);
    }

    private ConnectionManager() throws Exception {
        this.connection = (T) new BluetoothServer();
    }

    private ConnectionManager(Context context) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        this.connection = (T) new NsdServer(context);
    }

    @Override
    public void start() {
        this.connection.start();
    }

    @Override
    public void stop() {
        this.connection.stop();
    }

    @Override
    public void send(String deviceName, byte[] data) {
        this.connection.send(deviceName,data);
    }

    public void setDataExchange(DataExchange dataExchange){
        this.connection.setDataExchange(dataExchange);
    }

    public DataExchange getConnectionDataExchange(){
        return this.connection.getDataExchange();
    }

    public List<String> getConnectedDevicesList(){
        return this.connection.getConnectedDevicesNames();
    }

}

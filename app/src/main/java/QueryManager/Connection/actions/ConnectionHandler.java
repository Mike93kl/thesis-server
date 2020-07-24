package QueryManager.Connection.actions;

public interface ConnectionHandler<T> {
    void sendToDevice(String from , byte[] data);
    T getSocketOf(String deviceName);
    void onDeviceConnected(String deviceName);
    void onDeviceDisconnected(String deviceName);


}

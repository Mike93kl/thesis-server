package QueryManager.Connection.actions;

import java.io.IOException;

public interface DataExchange {
    void onSendSuccess(String from, byte[] data);
    void onSendError(String from, Exception e);
    void onReceivedData(String from, byte[] data, int numBytes);
    void onReceivedDataError(String from,Exception e);
    void onSocketError(Exception e);
    void onDeviceConnected(String deviceName);
    void onDeviceDisconnected(String deviceName);
}

package QueryManager.response;

public interface WearableResult {
    void onResult(QueryResult result);
    void onResult(byte [] rawData);
    void onError(Exception e);
    void onDeviceConnected(String deviceName);
    void onDeviceDisconnected(String deviceName);
}

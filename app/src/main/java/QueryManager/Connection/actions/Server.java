package QueryManager.Connection.actions;

public interface Server {
    void start();
    void stop();
    void send(String deviceName, byte[] data);
}

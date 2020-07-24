package QueryManager.Connection.data;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class AcceptData<T> implements Runnable{
    private static final int BUFFER_SIZE = 1024;
    protected T socket;
    protected InputStream input;
    protected byte[] buffer;
    protected int numBytes;
    protected Boolean threadIsRunning;
    public AcceptData(T socket) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        this.socket = socket;
        Method getInputStream =
                socket.getClass().getDeclaredMethod("getInputStream");
        this.input = (InputStream) getInputStream.invoke(socket);
        this.buffer = new byte[BUFFER_SIZE];
        this.threadIsRunning = true;
    }
    public void readInput() throws IOException{
        numBytes = input.read(buffer);
    }

    public void stop(){
        threadIsRunning = false;
    }

    @Override
    public void run(){
        while (threadIsRunning){
            try{
                readInput();
                onDataReceived(buffer,numBytes);
            }
            catch (IOException e){
                e.printStackTrace();
                onErrorReadingData(e);

            }
        }
    }
    public abstract void onDataReceived(byte[] data, int numBytes);
    public abstract void onErrorReadingData(IOException e);
}

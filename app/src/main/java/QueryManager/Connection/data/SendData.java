package QueryManager.Connection.data;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class SendData<T> implements Runnable {
    protected T socket;
    protected OutputStream output;
    protected byte[] data;
    public SendData(T socket,byte[] data) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        this.socket = socket;
        Method getOutputStream =
                socket.getClass().getDeclaredMethod("getOutputStream");
        this.output = (OutputStream) getOutputStream.invoke(this.socket);
        this.data = data;
    }
    @Override
    public void run(){
        try{
            output.write(data);
            onSuccess(data);
        }catch (IOException e){
            e.printStackTrace();
            onError(e);
        }
    }

    public abstract void onSuccess(byte[] data);
    public abstract void onError(IOException e);
}


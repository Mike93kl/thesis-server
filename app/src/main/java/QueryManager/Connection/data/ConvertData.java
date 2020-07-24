package QueryManager.Connection.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ConvertData {
    public static byte[] toByteArray(Object obj) throws Exception {
        byte[] bytes = null;
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(obj);
        objOut.flush();
        bytes = byteOut.toByteArray();
        return bytes;
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            ObjectOutputStream oos = new ObjectOutputStream(bos);
//            oos.writeObject(obj);
//            oos.flush();
//            oos.close();
//            bos.close();
//            byte[] data = bos.toByteArray();
//            return data;

    }

    public static Object toObject(byte[] data) throws Exception{
        Object obj = null;
        ByteArrayInputStream byteIn = new ByteArrayInputStream(data);
        ObjectInputStream objIn = new ObjectInputStream(byteIn);
        obj = objIn.readObject();
        if(byteIn != null) byteIn.close();
        if(objIn != null ) objIn.close();
        return obj;
    }
}

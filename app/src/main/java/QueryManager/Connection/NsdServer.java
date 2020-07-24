package QueryManager.Connection;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;

import QueryManager.Connection.actions.ConnectionHandlerImpl;
import QueryManager.Connection.actions.Server;
import QueryManager.Connection.data.AcceptData;
import QueryManager.QueryManager;

public  class NsdServer extends ConnectionHandlerImpl<Socket> implements Server {

    private static final String SERVICE_NAME = "DQMServer";
    private static final String SERVICE_TYPE = "_dqm._tcp";

    private ServerSocket serverSocket;
    private NsdManager.RegistrationListener registrationListener;
    private String usedServiceName;
    private NsdManager nsdManager;
    private Context context;
    private Thread acceptThread;

    public NsdServer(Context context) throws IOException {
        this.context = context;
        initializeServerSocket();
        initializeRegistrationListener();
    }

    private void registerService(){
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName(SERVICE_NAME);
        serviceInfo.setServiceType(SERVICE_TYPE);
        serviceInfo.setPort(serverSocket.getLocalPort());
        nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        nsdManager.registerService(serviceInfo,NsdManager.PROTOCOL_DNS_SD, registrationListener);
    }

    private void initializeServerSocket() throws IOException {
        serverSocket = new ServerSocket(0);
    }

    private void initializeRegistrationListener() {
        registrationListener = new NsdManager.RegistrationListener() {
            @Override
            public void onRegistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
                QueryManager.log("SERVICE REGISTERED ERROR");
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
                QueryManager.log("UNREGISTRATION FAILED");
            }

            @Override
            public void onServiceRegistered(NsdServiceInfo nsdServiceInfo) {
                QueryManager.log("SERVICE REGISTERED SUCCESSFULLY");
                usedServiceName = nsdServiceInfo.getServiceName();
                acceptConnections();

            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo nsdServiceInfo) {
                QueryManager.log("SERVICE UNREGISTERED SUCCESSFULLY");
            }
        };
    }

    private void acceptConnections(){
        acceptThread = new Thread(new ListenForConnections(serverSocket));
        acceptThread.start();
    }

    @Override
    public void start() {
        registerService();
    }

    @Override
    public void stop() {
        nsdManager.unregisterService(registrationListener);
        if( acceptThread != null && acceptThread.isAlive() ) {
            acceptThread.interrupt();
        }
    }

    @Override
    public void send(String deviceName, byte[] data) {
        sendToDevice(deviceName,data);
    }


    private class ListenForConnections implements Runnable {
        private final ServerSocket serverSocket;
        ListenForConnections(ServerSocket serverSocket){
            this.serverSocket = serverSocket;
        }
        @Override
        public void run() {
            Socket socket;
            while (true){
                try {
                    socket = serverSocket.accept();
                    if(socket != null ){
                        addDeviceAndAccept(socket.getInetAddress().getHostName(), socket);
                        onDeviceConnected(socket.getInetAddress().getHostName());
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

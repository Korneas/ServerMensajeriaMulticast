package com.example.camilomontoya.servermensajeriamulticast;

import java.io.IOException;
import java.net.*;
import java.util.Observable;
import java.util.concurrent.RunnableFuture;

/**
 * Created by CamiloMontoya on 8/02/17.
 */

public class CommunicationManager extends Observable implements Runnable {

    public static CommunicationManager ref;
    private final String HOST_ADDRESS = "10.0.2.2";
    private final String GROUP_ADDRESS = "224.2.2.1";
    public static int PUERTO = 5000;

    private MulticastSocket mSocket;
    private DatagramSocket dSocket;
    private boolean life;

    private CommunicationManager() {
        life = true;
        try {
            dSocket = new DatagramSocket(PUERTO);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static CommunicationManager getInstance() {
        if (ref == null) {
            ref = new CommunicationManager();
            Thread hilo = new Thread(ref);
            hilo.start();
        }
        return ref;
    }

    @Override
    public void run() {
        while (life) {
            if (dSocket != null) {
                DatagramPacket dPacket = recibirMensaje();

                if (dPacket != null) {
                    String msg = new String(dPacket.getData(), 0, dPacket.getLength());

                    setChanged();
                    notifyObservers(msg);
                    clearChanged();
                }
            }
        }
        dSocket.close();
    }

    public void enviarMensaje(final String msg, final String direccionIP, final int pt) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (dSocket != null) {
                    try {
                        InetAddress hosting = InetAddress.getByName(direccionIP);
                        byte[] buffer = msg.getBytes();
                        DatagramPacket dPacket = new DatagramPacket(buffer, buffer.length, hosting, pt);
                        System.out.println("Paquete enviado a: " + direccionIP);

                        dSocket.send(dPacket);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    setChanged();
                    notifyObservers("Sin conexion");
                    clearChanged();
                }
            }
        }).start();
    }

    public DatagramPacket recibirMensaje() {
        byte[] buffer = new byte[1024];
        DatagramPacket dPacket = new DatagramPacket(buffer, buffer.length);

        try {
            dSocket.receive(dPacket);
            System.out.println("Paquete recibido de " + dPacket.getAddress());
            return dPacket;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}

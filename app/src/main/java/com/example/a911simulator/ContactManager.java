package com.example.a911simulator;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.lang.Object;

import static android.content.Context.WIFI_SERVICE;

public class ContactManager {
    private static final String LOG_TAG = "ContactManager";
    private static final int BROADCAST_INTERVAL = 10000; // Milliseconds
    private static final int BROADCAST_BUF_SIZE = 1024;
    private static final int MULTICAST_PORT = 6789;
    private static final String MULTICAST_GROUP = "228.5.6.7";
    private boolean BROADCAST = true;
    private boolean LISTEN = true;
    private HashMap<String, InetAddress> contacts;
    private InetAddress parentIP;
    private Context context;

    public ContactManager(String name, InetAddress parentIP, Context context) {

        contacts = new HashMap<String, InetAddress>();
        this.parentIP = parentIP;
        this.context = context;
        listen();
        broadcastName(name);
    }

    public HashMap<String, InetAddress> getContacts() {

        return contacts;
    }

    public void addContact(String name, InetAddress address) {
        // If the contact is not already known to us, add it
        if(!contacts.containsKey(name) && !address.equals(parentIP)) {

            Log.i(LOG_TAG, "Adding contact: " + name);
            contacts.put(name, address);
            Log.i(LOG_TAG, "#Contacts: " + contacts.size());
            return;
        }
        Log.i(LOG_TAG, "Contact already exists: " + name);
    }

    public void removeContact(String name) {
        // If the contact is known to us, remove it
        if(contacts.containsKey(name)) {

            Log.i(LOG_TAG, "Removing contact: " + name);
            contacts.remove(name);
            Log.i(LOG_TAG, "#Contacts: " + contacts.size());
            return;
        }
        Log.i(LOG_TAG, "Cannot remove contact. " + name + " does not exist.");
    }

    public void bye(final String name) {
        // Sends a Bye notification to other devices
        Thread byeThread = new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    MulticastSocket socket = new MulticastSocket(MULTICAST_PORT);
                    InetAddress group = InetAddress.getByName(MULTICAST_GROUP); // multicast group
                    socket.joinGroup(group);

                    Log.i(LOG_TAG, "Attempting to broadcast BYE notification!");
                    String notification = "BYE:"+name;
                    byte[] message = notification.getBytes();

                    DatagramPacket packet = new DatagramPacket(message, message.length, group, MULTICAST_PORT);
                    socket.send(packet);
                    Log.i(LOG_TAG, "Broadcast BYE notification!");

                    socket.leaveGroup(group);
                    socket.disconnect();
                    socket.close();
                }
                catch(SocketException e) {

                    Log.e(LOG_TAG, "SocketException during BYE notification: " + e);
                }
                catch(IOException e) {

                    Log.e(LOG_TAG, "IOException during BYE notification: " + e);
                }
            }
        });
        byeThread.start();
    }

    public void broadcastName(final String name) {
        // Broadcasts the name of the device at a regular interval
        Log.i(LOG_TAG, "Broadcasting started!");
        Thread broadcastThread = new Thread(new Runnable() {

            @Override
            public void run() {

                WifiManager manager = (WifiManager) context.getSystemService(WIFI_SERVICE);
                WifiInfo wifiInfo = manager.getConnectionInfo();

                WifiManager.MulticastLock multicastLock = manager.createMulticastLock(String.valueOf(System.currentTimeMillis()));
                multicastLock.setReferenceCounted(true);
                multicastLock.acquire();

                try {

                    InetAddress group = InetAddress.getByName(MULTICAST_GROUP); // multicast group
                    String request = "ADD:"+name;
                    byte[] message = request.getBytes();

                    // join multicast group
                    MulticastSocket s = new MulticastSocket(MULTICAST_PORT);
                    s.joinGroup(group);
                    DatagramPacket packet = new DatagramPacket(message, message.length, group, MULTICAST_PORT);
                    while(BROADCAST) {
                        s.send(packet);
                        Log.i(LOG_TAG, "Broadcast packet sent: " + packet.getAddress().toString());
                        Thread.sleep(BROADCAST_INTERVAL);
                    }
                    Log.i(LOG_TAG, "Broadcaster ending!");
                    s.leaveGroup(group);
                    s.disconnect();
                    s.close();
                }
                catch(SocketException e) {

                    Log.e(LOG_TAG, "SocketException in broadcast: " + e);
                    Log.i(LOG_TAG, "Broadcaster ending!");
                }
                catch(IOException e) {

                    Log.e(LOG_TAG, "IOException in broadcast: " + e);
                    Log.i(LOG_TAG, "Broadcaster ending!");
                }
                catch(InterruptedException e) {

                    Log.e(LOG_TAG, "InterruptedException in broadcast: " + e);
                    Log.i(LOG_TAG, "Broadcaster ending!");
                }

                if (multicastLock != null) {
                    multicastLock.release();
                    multicastLock = null;
                }
            }
        });
        broadcastThread.start();

    }

    public void stopBroadcasting() {
        // Ends the broadcasting thread
        BROADCAST = false;
    }

    public void listen() {
        // Create the listener thread
        Log.i(LOG_TAG, "Listening started!");
        Thread listenThread = new Thread(new Runnable() {

            @Override
            public void run() {
                WifiManager manager = (WifiManager) context.getSystemService(WIFI_SERVICE);
                WifiInfo wifiInfo = manager.getConnectionInfo();

                WifiManager.MulticastLock multicastLock = manager.createMulticastLock(String.valueOf(System.currentTimeMillis()));
                multicastLock.setReferenceCounted(true);
                multicastLock.acquire();

                MulticastSocket socket;

                try {

                    socket = new MulticastSocket(MULTICAST_PORT);
                    socket.setReuseAddress(true);
                    // Join multicast group
                    InetAddress group = InetAddress.getByName(MULTICAST_GROUP); // multicast group
                    socket.joinGroup(group);
                }
                catch (SocketException e) {

                    Log.e(LOG_TAG, "SocketException in listener: " + e);
                    return;
                }
                catch(IOException e) {

                    Log.e(LOG_TAG, "IOException in listener: " + e);
                    return;
                }
                byte[] buffer = new byte[BROADCAST_BUF_SIZE];

                while(LISTEN) {

                    listen(socket, buffer);
                }
                Log.i(LOG_TAG, "Listener ending!");

                if (multicastLock != null) {
                    multicastLock.release();
                    multicastLock = null;
                }

                socket.disconnect();
                socket.close();
            }

            public void listen(MulticastSocket socket, byte[] buffer) {

                try {


                    //Listen in for new notifications
                    Log.i(LOG_TAG, "Listening for a packet!");
                    DatagramPacket packet = new DatagramPacket(buffer, BROADCAST_BUF_SIZE);
                    socket.setSoTimeout(15000);
                    socket.receive(packet);
                    String data = new String(buffer, 0, packet.getLength());
                    Log.i(LOG_TAG, "Packet received: " + data);
                    String action = data.substring(0, 4);

                    if(action.equals("ADD:")) {
                        // Add notification received. Attempt to add contact
                        Log.i(LOG_TAG, "Listener received ADD request");
                        addContact(data.substring(4), packet.getAddress());
                    }
                    else if(action.equals("BYE:")) {
                        // Bye notification received. Attempt to remove contact
                        Log.i(LOG_TAG, "Listener received BYE request");
                        removeContact(data.substring(4));
                    }
                    else {
                        // Invalid notification received
                        Log.w(LOG_TAG, "Listener received invalid request: " + action);
                    }

                }
                catch(SocketTimeoutException e) {

                    Log.i(LOG_TAG, "No packet received!");
                    if(LISTEN) {

                        listen(socket, buffer);
                    }
                }
                catch(SocketException e) {

                    Log.e(LOG_TAG, "SocketException in listen: " + e);
                    Log.i(LOG_TAG, "Listener ending!");
                }
                catch(IOException e) {

                    Log.e(LOG_TAG, "IOException in listen: " + e);
                    Log.i(LOG_TAG, "Listener ending!");
                }
            }
        });
        listenThread.start();
    }

    public void stopListening() {
        // Stops the listener thread
        LISTEN = false;
    }
}

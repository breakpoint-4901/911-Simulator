package com.example.a911simulator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConnectActivity extends AppCompatActivity {

    // BEGIN SECTION OF VARIABLES FOR BUTTON LISTENERS
    Button btnDiscover; //three objects for button
    Button btnDisconnect;
    TextView connectionStatus;
    TextView connectionStatus2;
    ListView listView;
    Button btnSend;
    // END SECTION OF VARIABLES FOR BUTTON LISTENERS



    // BEGIN SECTION OF VARIABLES FOR DEVICE CONNECTION
    WifiManager wifiManager;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;

    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>(); // holds the found devices nearby. (DISCOVER BUTTON FUNCTIONALITY)
    String[] deviceNameArray; //personal names for the devices nearby
    WifiP2pDevice[] deviceArray; //the actual devices we find in our nearby enviornment.
    // END SECTION OF VARIABLES FOR DEVICE CONNECTION

    static final int MESSAGE_READ = 1;

    ServerClass serverClass;
    ClientClass clientClass;
    SendReceive sendReceive;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        int testing = 0; // CHANGE THIS IF YOU HAVE A PHYSICAL DEVICE TO TEST ON.


        // BEGIN SECTION OF FUNCTIONS FOR DEVICE CONNECTION
        if(testing == 0) {
            initialWork();
            exqListener();
        }
        // END SECTION OF FUNCTIONS FOR DEVICE CONNECTION
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch(msg.what) //this is where the buffer actually sets to the screen.
            {
                case MESSAGE_READ:
                    byte [] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff, 0, msg.arg1);
                    //ggread_msg_box.setText(tempMsg);
                    Toast.makeText(getApplicationContext(), tempMsg, Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }
    });

    private void exqListener() {
//        Functionality for the "Discover" button.
        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        connectionStatus.setText("Discovery Started");
                    }

                    @Override //wifi turned off ( i did not get this to trigger with wifi turned on )
                    public void onFailure(int i) {
                        connectionStatus.setText("Discovery Starting Failed");
                        wifiManager.setWifiEnabled(false);
                        wifiManager.setWifiEnabled(true);
                    }
                });
            }
        });

        //this unpairs the phones group. I believe either phone can break the connection but typically the host is expected to perform this.
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Log.d("ERR", "ATTEMPTING TO REMOVE");
               if (mManager != null && mChannel != null) { //prevents any errors
                   mManager.requestGroupInfo(mChannel, new WifiP2pManager.GroupInfoListener() {
                       @Override
                       public void onGroupInfoAvailable(WifiP2pGroup group) {
                           if (group != null && mManager != null && mChannel != null&& group.isGroupOwner()) {
                               mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() { //the process of actually removing the group from our phones.
                                   @Override //this just does logging, doesn't actually launch anythng. THE TEXT TODO TEXT CHANGE WOULD GO HERE.
                                   public void onSuccess() {
                                       Log.d("", "removeGroup onSuccess -");
                                   }
                                   @Override
                                   public void onFailure(int reason) {
                                       Log.d("", "removeGroup onFailure -" + reason);
                                   }
                               });
                           }
                       }
                   });
               }
           }
        });

        // video 5 of the tutorial
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final WifiP2pDevice device = deviceArray[i];

                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;

                mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(), "Connected to" + device.deviceName, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i) {
                        Toast.makeText(getApplicationContext(), "Not Connected" , Toast.LENGTH_SHORT).show();
                    }

                });
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg="test";
                // sendReceive.write(msg.getBytes());
                try {
                    sendtask t1 = new sendtask(msg);
                    t1.execute();
                }catch (Exception  e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public class sendtask extends AsyncTask<Void, Void, Void> {

        String message;


        sendtask(String msg) {
            message=msg;

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                sendReceive.write(message.getBytes());
            } catch (Exception e) { //if the button is pressed without a socket being created
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);

        }

    }

    private void initialWork() { //initialization function called upon start up for the application

        btnDiscover = (Button) findViewById(R.id.discover);
        btnDisconnect = (Button) findViewById(R.id.disconnect);
        listView = (ListView) findViewById(R.id.peerListView);
        connectionStatus = (TextView) findViewById(R.id.connectionStatus);
        connectionStatus2 = (TextView) findViewById(R.id.connectionStatus2);
        btnSend = (Button) findViewById(R.id.sendButton);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE); //does what?


        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new wifiDirectBroadcastReceiver(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

    }

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            if(!peerList.getDeviceList().equals(peers)) {
                peers.clear(); //empty out whatever contents are inside of our list
                peers.addAll(peerList.getDeviceList()); //merges the list into peers

                deviceNameArray = new String[peerList.getDeviceList().size()];
                deviceArray = new WifiP2pDevice[peerList.getDeviceList().size()];

                int index = 0;
                for(WifiP2pDevice device : peerList.getDeviceList()) {
                    deviceNameArray[index] = device.deviceName;  //store the NAMES of our device into an array to look at possible options nearby
                    deviceArray[index] = device; //store the actual objects into an array
                    index++; //increment out index variable
                }

//                Log.d("ADebugTag", "Value: " + peerList.getDeviceList());


                ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceNameArray);
                listView.setAdapter( listAdapter );

            }

            if(peers.size() == 0){
                Toast.makeText(getApplicationContext(), "No Device Found", Toast.LENGTH_SHORT).show();
                return;
            }

        }
    };

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;

            if(wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                connectionStatus.setText("Teacher");
                serverClass = new ServerClass();
                serverClass.start();
                //move to a different activity?
            }
            else {
                connectionStatus.setText("Student");
                clientClass = new ClientClass(groupOwnerAddress);
                clientClass.start();
                //move to a different activity?
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    //video #6 server code
    public class ServerClass extends Thread {
        Socket socket;
        ServerSocket serverSocket;

        @Override
        public void run() {
            try{ //creates the socket where we can send messages back and forth.
                serverSocket = new ServerSocket(8888);
                socket = serverSocket.accept();
                sendReceive = new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //video #8 code
    private class SendReceive extends Thread{
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public SendReceive(Socket skt) {
            socket = skt;

            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while(socket != null) {
                try {
                    bytes = inputStream.read(buffer);
                    if(bytes > 0 ) {//we have something to read
                        handler.obtainMessage(MESSAGE_READ, bytes,-1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes) {

            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //video #7 client code
    public class ClientClass extends Thread {
        Socket socket;
        String hostAdd;
        public ClientClass(InetAddress hostAddress) {
            hostAdd = hostAddress.getHostAddress();
            socket = new Socket();
        }
        @Override
        public void run() {
            try {
                socket.connect(new InetSocketAddress(hostAdd, 8888), 500);
                sendReceive = new SendReceive(socket);
                sendReceive.start();
                //future code for s ending and receiving
            }catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}


package com.imaginarymakings.chess.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.imaginarymakings.chess.Logic.GameInfo;
import com.imaginarymakings.chess.Logic.Player;
import com.imaginarymakings.chess.Logic.SpaceAdapter;
import com.imaginarymakings.chess.Activities.MainActivity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by rafaelfrancisco on 02/01/18.
 */

public class NetworkManager {
    private static final int SERVER_PORT = 6000;
    private boolean end = false;

    private Context c;
    private SpaceAdapter adapter;

    public void endGame(){
        end = true;
    }

    public NetworkManager(Context c, SpaceAdapter adapter) {
        this.c = c;
        this.adapter = adapter;
    }

    public String getCurrentIP(){
        WifiManager wifiMan = (WifiManager) c.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        return String.format("%d.%d.%d.%d", (ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));
    }

    public void startServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket ss = new ServerSocket(SERVER_PORT);
                    Socket s = ss.accept();

                    ObjectInputStream is = new ObjectInputStream(s.getInputStream());
                    ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());

                    while (!end){
                        GameInfo gm = (GameInfo) is.readObject();
                        adapter.setGameInfo(gm);

                        os.writeObject(adapter.getGameInfo());
                    }

                    s.close();
                    ss.close();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();

                    Intent intent = new Intent(c, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    c.startActivity(intent);
                }
            }
        }).start();
    }

    public void startClient(final String ip) {
        adapter.whoAmI = Player.BLACK;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket s = new Socket(ip, SERVER_PORT);
                    s.setSoTimeout(200);

                    ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
                    ObjectInputStream is = new ObjectInputStream(s.getInputStream());

                    while (!end){
                        try {
                            os.writeObject(adapter.getGameInfo());

                            GameInfo gm = (GameInfo) is.readObject();
                            adapter.setGameInfo(gm);
                        } catch (IOException e){
                            //Do nothing here
                        }
                    }

                    os.close();
                    s.close();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();

                    Intent intent = new Intent(c, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    c.startActivity(intent);
                }
            }
        }).start();
    }
}
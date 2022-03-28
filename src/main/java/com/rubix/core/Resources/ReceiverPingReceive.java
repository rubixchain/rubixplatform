package com.rubix.core.Resources;

import com.rubix.Ping.PingReceive;
import org.json.JSONException;

import static com.rubix.Resources.Functions.RECEIVER_PORT;
import static com.rubix.Resources.Functions.pathSet;

public class ReceiverPingReceive implements Runnable {
    @Override
    public void run() {
        while (true) {
            try {
                pathSet();
                PingReceive.receive(RECEIVER_PORT+10);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

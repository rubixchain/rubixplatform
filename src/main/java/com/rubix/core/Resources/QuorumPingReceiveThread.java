package com.rubix.core.Resources;

import com.rubix.Ping.QuorumPingReceive;
import org.json.JSONException;

import static com.rubix.Resources.Functions.*;

public class QuorumPingReceiveThread implements Runnable {
    @Override
    public void run() {
        while (true) {
            try {
                pathSet();
                QuorumPingReceive.receive(QUORUM_PORT+410);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

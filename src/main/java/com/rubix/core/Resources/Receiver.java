package com.rubix.core.Resources;

import com.rubix.TokenTransfer.TokenReceiver;
import org.json.JSONException;
public class Receiver implements Runnable {
    @Override
    public void run() {
        while (true) {
            try {
                TokenReceiver.receive();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

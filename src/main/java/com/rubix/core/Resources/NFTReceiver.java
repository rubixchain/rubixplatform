package com.rubix.core.Resources;

import com.rubix.NFT.Buyer;


public class NFTReceiver implements Runnable {
    public void run() {
        while (true) {
            try {
                while (true)
                {String str = Buyer.receive();
                break;}
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

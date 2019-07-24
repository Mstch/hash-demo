package com.tiddar.hash;

import java.util.Comparator;

public class VirtualServer implements Node {

    public Server belongServer;
    public int hash;

    public Data get(String key) {
        return belongServer.get(key);
    }

    public Data put(String key, Data data) {
        return belongServer.put(key, data);
    }

    @Override
    public int getHash() {
        return hash;
    }

    @Override
    public String toString() {
        return "VirtualServer{" +
                ", hash=" + hash +
                '}';
    }



}

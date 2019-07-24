package com.tiddar.hash;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Server implements Node {

    private ConcurrentHashMap<String, Data> map;
    public int hash;
    private boolean health;
    public List<VirtualServer> vituralServers = new ArrayList<>();
    //负载的环长
    public int load;
    public Data get(String key) {
        return map.get(key);
    }

    public Data put(String key, Data data) {
        return map.put(key, data);
    }

    public int size(){
        return map.size();
    }

    public void startUp(int hash) {
        if (map == null) {
            map = new ConcurrentHashMap<>();
        }
        hash = hash;
        health = true;
    }

    public void startUp() {
        health = true;
    }

    public void shutDown() {
        health = false;
    }

    public int getHash(){
        return hash;
    }

    @Override
    public String toString() {
        return "Server{" +
                "map=" + map +
                ", hash=" + hash +
                ", health=" + health +
                ", vituralServers=" + vituralServers +
                ", load=" + load +
                '}';
    }
}

package com.tiddar.hash;

public interface Node {

    Data get(String key);

    Data put(String key, Data data);

    int getHash();
}

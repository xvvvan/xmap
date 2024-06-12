package com.xvvvan.util;

public class Pair<K, V> {
    private K key;
    private V value;

    public Pair() {

    }

    public Pair(K var1, V var2) {
        this.key = var1;
        this.value = var2;
    }
    public K getKey() {
        return this.key;
    }

    public V getValue() {
        return this.value;
    }
    public void setKey(K key) {
        this.key = key;
    }

    public void setValue(V value) {
        this.value = value;
    }
}

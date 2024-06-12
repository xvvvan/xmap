package com.xvvvan.xhole;

public class Tuple<T1, T2> {
    public T1 first;
    public T2 second;

    public Tuple(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    public T1 getFirst() {
        return first;
    }

    public T2 getSecond() {
        return second;
    }

    public void setFirst(T1 first) {
        this.first = first;
    }

    public void setSecond(T2 second) {
        this.second = second;
    }
    public void setTuple(Tuple<T1,T2>tuple){

        this.first = tuple.getFirst();
        this.second = tuple.getSecond();
    }
}

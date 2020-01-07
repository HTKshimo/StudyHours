package com.example.studyhours;

public class StudySession {
    public long in = System.currentTimeMillis();
    public long out;

    public void setIn(long in) {
        this.in = in;
    }

    public void setOut(){
        this.out = System.currentTimeMillis();
    }

    public long getIn() {
        return in;
    }
}

package edu.neu.madcourse.topdog.DatabaseObjects;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

public class LeaderboardEntry {

    private String username;
    private String doggoName;
    private long numberWalks;


    public LeaderboardEntry(String username, String dogName, long numberWalks){
        this.username = username;
        this.numberWalks = numberWalks;
        this.doggoName = dogName;
    }

    public String getUsername(){
        return this.username;
    }

    public String getDogName() { return this.doggoName;}

    public long getNumberWalks() {
        return this.numberWalks;
    }

}
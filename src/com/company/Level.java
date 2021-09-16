package com.company;

public class Level {
    public static int currentLevel = 0;
    //moves available in the level
    private int movesLimit;
    //score necessary to pass to next level
    private int scoreToReach;

    public Level(){
        Level.currentLevel ++;
        this.movesLimit = 16 - (Level.currentLevel > 6 ? 6 : Level.currentLevel);
        this.scoreToReach = 4000 + (Level.currentLevel * 200);
    }

    public int getMovesLimit() {
        return movesLimit;
    }

    public int getScoreToReach() {
        return scoreToReach;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }
}

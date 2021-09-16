package com.company;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CandyCrush extends JPanel implements ActionListener, MouseListener {

    private Candy[][] candies = new Candy[6][8];
    //selectedCandy is the first candy selected in a pair
    private Coordinate selectedCandy = null;
    private int score;
    private Timer timer;
    //coordinate of two candy to switch horizontal
    private Coordinate[] candyToSwitchHorizontal = new Coordinate[2];
    //coordinate of two candy to switch vertical
    private Coordinate[] candyToSwitchVertical = new Coordinate[2];
    //coordinate of animation offset to slide the two candy to switch
    Coordinate offsetPaint = new Coordinate(0,0);
    //number of remaining instants of the crush animation
    private int countdown = 0;
    private Level level;
    private int movesDone = 0;

    public CandyCrush(){
        super.addMouseListener(this);
        //definition of timer delay to update paint
        this.timer = new Timer(50,this);
        this.timer.start();
        //generate first level
        this.level = new Level();

        //generate a grid of random candy
        fillCandies();

        //verify if there are series of three or more color in the grid before to start to play
        while (checkCandy(this.candies,false)){
            //reset candy until there aren't series crushable
            resetCandies();
        }

        //initialize score value of a new game
        this.score = 0;
    }

    //change all candy in the grid
    public void resetCandies(){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 6; j++){
                candies[j][i] = new Candy();
            }
        }
    }

    //fill the empty space in the grid with random candy
    public void fillCandies(){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 6; j++){
                if (candies[j][i] == null){
                    candies[j][i] = new Candy();
                }
            }
        }
    }

    //refill grid with gravity if there are candies over the crushed candies
    public void refillCandy(){
        for (int i = 0; i < 8; i++) {
            for (int j = 5; j >= 0; j--){
                if (candies[j][i] == null){
                    int row = j-1;
                    while (row >= 0 && candies[j][i] == null){
                        if (candies[row][i] != null){
                            candies[j][i] = candies[row][i];
                            candies[row][i] = null;
                        }
                        row -= 1;
                    }
                }
            }
        }
        fillCandies();
        //check for series of candies to crush
        checkCandy(this.candies,false);

        //if there aren't moves available reset candies in the grid
        if (this.countdown == 0 && !checkMovesAvailable()){
            resetCandies();
            checkCandy(this.candies,false);
            JOptionPane.showMessageDialog(null, "NO MOVES AVAILABLE - REFILL CANDIES");
        }

        //condition to next level
        if (this.level.getScoreToReach()<= this.score){
            this.level = new Level();
            resetCandies();
            while (checkCandy(this.candies,false)){
                resetCandies();
            }
            this.score = 0;
            this.movesDone = 0;
            JOptionPane.showMessageDialog(null, "NEW LEVEL: " + Level.currentLevel);
        }

        //condition to lose and repeat the level
        if (this.level.getMovesLimit()-this.movesDone <= 0){
            resetCandies();
            while (checkCandy(this.candies,false)){
                resetCandies();
            }
            this.score = 0;
            this.movesDone = 0;
            JOptionPane.showMessageDialog(null, "YOU LOSE - MOVES FINISHED, RETRY!");
        }
    }



    public Candy[][] getCandiesCopy(){

        Candy[][] candiesCopy = new Candy[6][8];

        //create a copy of grid to test the possible moves available without change the principle grid
        for (int j = 0; j < 6; j++) {
            for (int i = 0; i < 8; i++){
                candiesCopy[j][i] = new Candy(this.candies[j][i].getColor());
            }
        }
        return candiesCopy;
    }

    public boolean checkCandy(Candy[][] candies, boolean simulation){

        //count series of consecutive candies and call the method to crush the series of three or more candies
        boolean match = false;

        //check columns
        for (int i = 0; i < 8; i++) {
            int series = 1;
            for (int j = 0; j < 5; j++){

                //verify if the next candy is colored at the same of the current candy
                if (candies[j][i] != null && candies[j+1][i] != null && candies[j][i].getColor().equals(candies[j+1][i].getColor())){
                    series ++;
                }else{
                    if (series >=3){
                        match = true;
                        if (!simulation){
                            this.score += series * 100;
                            //delete series of three or more candies
                            crushCandyColumn(j,i,series);
                        }
                    }
                    series = 1;
                }
            }
            if (series >=3){
                match = true;
                if (!simulation) {
                    this.score += series * 100;
                    //delete series of three or more candies
                    crushCandyColumn(5, i, series);
                }
            }
        }

        //check rows
        for (int j = 0; j < 6; j++) {
            int series = 1;
            for (int i = 0; i < 7; i++){

                if (candies[j][i] != null && candies[j][i+1] != null && candies[j][i].getColor().equals(candies[j][i+1].getColor())){
                    series ++;
                }else{
                    if (series >=3){
                        if (!simulation) {
                            match = true;
                            this.score += series * 100;
                            //delete series of three or more candies
                            crushCandyRow(j, i, series);
                        }
                    }
                    series = 1;
                }
            }
            if (series >=3){
                match = true;
                if (!simulation) {
                    this.score += series * 100;
                    //delete series of three or more candies
                    crushCandyRow(j, 7, series);
                }
            }
        }
        return match;
    }

    public void crushCandyColumn(int row, int column, int series){
        //delete series of three or more candies in a column
        if (series >= 5){
            this.candies[row-series+1][column] = new Candy("special");
            series -= 1;
        }else if (series == 4){
            this.candies[row-series+1][column] = new Candy("donut");
            series -= 1;
        }
        while (series > 0){
            this.candies[row-series+1][column] = null;
            series -= 1;
        }
        //set a countdown for crush animation
        this.countdown = 60;
    }

    public void crushCandyRow(int row, int column, int series){
        //delete series of three or more candies in a row
        if (series >= 5){
            this.candies[row][column-series+1] = new Candy("special");
            series -= 1;
        }else if (series == 4){
            this.candies[row][column-series+1] = new Candy("donut");
            series -= 1;
        }
        while (series > 0){
            this.candies[row][column-series+1] = null;
            series -= 1;
        }
        this.countdown = 60;
    }

    public void deleteAround(Coordinate center){
        //delete the candies around a point of grid
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                try{
                    this.candies[center.getY() + j][center.getX() + i] = null;
                }catch (Exception e){
                }
                this.score += 100;
            }
        }
        this.countdown = 60;
    }

    public void deleteAllColour(Color color){
        //delete all candies where color is equal to color given from parameter
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 6; j++){
                if (color.equals(new Color(200,200,200))){
                    this.candies[j][i] = null;
                    this.score += 100;
                }
                if (this.candies[j][i] != null && (this.candies[j][i].getColor().equals(color))){
                    this.candies[j][i] = null;
                    this.score += 100;
                }
            }
        }
        this.countdown = 60;
    }

    public void play(Coordinate candy1, Coordinate candy2){
        //try if the move is valid and put candies to switch in the relative array of coordinate

        this.movesDone ++;

        if (candy1.getX()>0 && candy2.getX() == candy1.getX()-1 && candy1.getY() == candy2.getY()){
            this.candyToSwitchHorizontal[0] = candy1;
            this.candyToSwitchHorizontal[1] = candy2;
        }else if(candy1.getX()<7 && candy2.getX() == candy1.getX()+1 && candy1.getY() == candy2.getY()){
            this.candyToSwitchHorizontal[1] = candy1;
            this.candyToSwitchHorizontal[0] = candy2;
        }else if(candy1.getY()>0 && candy2.getY() == candy1.getY()-1 && candy1.getX() == candy2.getX()){
            this.candyToSwitchVertical[1] = candy1;
            this.candyToSwitchVertical[0] = candy2;
        }else if(candy1.getY()<5 && candy2.getY() == candy1.getY()+1 && candy1.getX() == candy2.getX()){
            this.candyToSwitchVertical[0] = candy1;
            this.candyToSwitchVertical[1] = candy2;
        }
    }

    //switch the color of two candy given through their coordinate
    public void switchCandy(Coordinate candy1, Coordinate candy2){
        Color temp = this.candies[candy1.getY()][candy1.getX()].getColor();
        this.candies[candy1.getY()][candy1.getX()].setColor(this.candies[candy2.getY()][candy2.getX()].getColor());
        this.candies[candy2.getY()][candy2.getX()].setColor(temp);
    }

    public boolean checkMovesAvailable(){

        //get the moves available to know if we are stuffed
        boolean movesAvailable = false;
        Candy[][] candiesCopy;
        Color temp;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 6; j++) {

                //try every possible moves in a copy of candy grid
                candiesCopy = getCandiesCopy();

                if (j+1 <= 5){
                    //do the switch of two candy and verify if there are series of three or more candies in the simulation grid
                    temp = candiesCopy[j][i].getColor();
                    candiesCopy[j][i] = candiesCopy[j+1][i];
                    candiesCopy[j+1][i].setColor(temp);
                    if (checkCandy(candiesCopy, true)){
                        movesAvailable = true;
                    }
                }

                candiesCopy = getCandiesCopy();

                if (i+1 <= 7){
                    temp = candiesCopy[j][i].getColor();
                    candiesCopy[j][i] = candiesCopy[j][i+1];
                    candiesCopy[j][i+1].setColor(temp);
                    if (checkCandy(candiesCopy, true)){
                        movesAvailable = true;
                    }
                }
            }
        }
        return movesAvailable;
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //get the click position and extract the position of the candy

        Coordinate currentCandy = new Coordinate(e.getX() / 70,e.getY() / 70);

        //verify if there was a selected candy to switch or to set the first candy selected
        if (this.selectedCandy == null){
            this.selectedCandy = currentCandy;
        }else{
            play(this.selectedCandy,currentCandy);
            this.selectedCandy = null;
        }
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Coordinate currentPaint = new Coordinate(0,0);
        Image crushImg = null;

        try {
            crushImg = ImageIO.read(new File("img/crush.png"));
        } catch (IOException e){
            e.printStackTrace();
        }

        //draw grid without candies
        for (Candy[] row:candies) {
            for (Candy candy: row) {
                //draw border of single cell in the grid
                g.setColor(new Color(70,100,140));
                g.fillRect(currentPaint.getX(), currentPaint.getY(), 70,70);
                //draw single cell in the grid
                g.setColor(new Color(100,200,250));
                g.fillRect(currentPaint.getX()+1, currentPaint.getY()+1, 68,68);
                currentPaint.setX(currentPaint.getX()+70);
            }
            currentPaint.setX(0);
            currentPaint.setY(currentPaint.getY()+70);
        }

        //reset coordinate of paint
        currentPaint.reset();

        //draw candies
        for (Candy[] row:candies) {
            for (Candy candy: row) {

                //if there is a candy crushed print an explosion
                if(this.countdown > 0 && candy == null) {
                    g.drawImage(crushImg, currentPaint.getX()+(this.countdown/2)-5,currentPaint.getY()+(this.countdown/2)-5,80-this.countdown,80-this.countdown,this);
                    this.countdown -= 1;
                    if (this.countdown <= 0){
                        this.countdown = 0;
                        //refill candy after explosion effect
                        refillCandy();
                    }
                }else if(
                        this.candyToSwitchHorizontal[0] != null &&
                        this.candyToSwitchHorizontal[0].getX() == currentPaint.getX()/70 &&
                        this.candyToSwitchHorizontal[0].getY() == currentPaint.getY()/70
                ){
                    //if there are candy to switch horizontal do the animation to switch
                    g.drawImage(candy.getImg(), currentPaint.getX() - this.offsetPaint.getX(),currentPaint.getY(),70,70,this);
                    this.offsetPaint.setX(this.offsetPaint.getX()+10);
                    if (this.offsetPaint.getX() >= 65){
                        //when animation finished switch candy in the grid and apply the power of special candies
                        switchCandy(this.candyToSwitchHorizontal[0],this.candyToSwitchHorizontal[1]);
                        if (this.candies[this.candyToSwitchHorizontal[0].getY()][this.candyToSwitchHorizontal[0].getX()].getColor().equals(new Color(100,100,100))){
                            this.candies[this.candyToSwitchHorizontal[0].getY()][this.candyToSwitchHorizontal[0].getX()] = null;
                            deleteAround(new Coordinate(this.candyToSwitchHorizontal[0].getX(),this.candyToSwitchHorizontal[0].getY()));
                        }else if(this.candies[this.candyToSwitchHorizontal[1].getY()][this.candyToSwitchHorizontal[1].getX()].getColor().equals(new Color(100,100,100))) {
                            this.candies[this.candyToSwitchHorizontal[1].getY()][this.candyToSwitchHorizontal[1].getX()] = null;
                            deleteAround(new Coordinate(this.candyToSwitchHorizontal[1].getX(),this.candyToSwitchHorizontal[1].getY()));
                        }else if (this.candies[this.candyToSwitchHorizontal[0].getY()][this.candyToSwitchHorizontal[0].getX()].getColor().equals(new Color(200,200,200))){
                            this.candies[this.candyToSwitchHorizontal[0].getY()][this.candyToSwitchHorizontal[0].getX()] = null;
                            deleteAllColour(this.candies[this.candyToSwitchHorizontal[1].getY()][this.candyToSwitchHorizontal[1].getX()].getColor());
                        }else if(this.candies[this.candyToSwitchHorizontal[1].getY()][this.candyToSwitchHorizontal[1].getX()].getColor().equals(new Color(200,200,200))){
                            this.candies[this.candyToSwitchHorizontal[1].getY()][this.candyToSwitchHorizontal[1].getX()] = null;
                            deleteAllColour(this.candies[this.candyToSwitchHorizontal[0].getY()][this.candyToSwitchHorizontal[0].getX()].getColor());
                        }else{
                            if (!checkCandy(this.candies,false)) {
                                //if switch doesn't create series, redo the switch to return at the previous state
                                switchCandy(this.candyToSwitchHorizontal[0],this.candyToSwitchHorizontal[1]);
                            }
                        }
                        //after animation, reset the offset of animation and delete the coordinate of candy switched in the array
                        this.offsetPaint.setX(0);
                        this.candyToSwitchHorizontal[0] = null;
                        this.candyToSwitchHorizontal[1] = null;
                    }
                }else if(
                        this.candyToSwitchHorizontal[1] != null &&
                        this.candyToSwitchHorizontal[1].getX() == currentPaint.getX()/70 &&
                        this.candyToSwitchHorizontal[1].getY() == currentPaint.getY()/70
                ){
                    g.drawImage(candy.getImg(), currentPaint.getX() + this.offsetPaint.getX(),currentPaint.getY(),70,70,this);
                }else if (
                        this.candyToSwitchVertical[0] != null &&
                        this.candyToSwitchVertical[0].getX() == currentPaint.getX()/70 &&
                        this.candyToSwitchVertical[0].getY() == currentPaint.getY()/70
                ){
                    g.drawImage(candy.getImg(), currentPaint.getX(),currentPaint.getY() + this.offsetPaint.getY(),70,70,this);
                    this.offsetPaint.setY(this.offsetPaint.getY()+10);
                    if (this.offsetPaint.getY() >= 65){
                        switchCandy(this.candyToSwitchVertical[0],this.candyToSwitchVertical[1]);
                        if (this.candies[this.candyToSwitchVertical[0].getY()][this.candyToSwitchVertical[0].getX()].getColor().equals(new Color(100,100,100))){
                            this.candies[this.candyToSwitchVertical[0].getY()][this.candyToSwitchVertical[0].getX()] = null;
                            deleteAround(new Coordinate(this.candyToSwitchVertical[0].getX(),this.candyToSwitchVertical[0].getY()));
                        }else if(this.candies[this.candyToSwitchVertical[1].getY()][this.candyToSwitchVertical[1].getX()].getColor().equals(new Color(100,100,100))) {
                            this.candies[this.candyToSwitchVertical[1].getY()][this.candyToSwitchVertical[1].getX()] = null;
                            deleteAround(new Coordinate(this.candyToSwitchVertical[1].getX(),this.candyToSwitchVertical[1].getY()));
                        }else if (this.candies[this.candyToSwitchVertical[0].getY()][this.candyToSwitchVertical[0].getX()].getColor().equals(new Color(200,200,200))){
                            this.candies[this.candyToSwitchVertical[0].getY()][this.candyToSwitchVertical[0].getX()] = null;
                            deleteAllColour(this.candies[this.candyToSwitchVertical[1].getY()][this.candyToSwitchVertical[1].getX()].getColor());
                        }else if(this.candies[this.candyToSwitchVertical[1].getY()][this.candyToSwitchVertical[1].getX()].getColor().equals(new Color(200,200,200))){
                            this.candies[this.candyToSwitchVertical[1].getY()][this.candyToSwitchVertical[1].getX()] = null;
                            deleteAllColour(this.candies[this.candyToSwitchVertical[0].getY()][this.candyToSwitchVertical[0].getX()].getColor());
                        }else{
                            if(!checkCandy(this.candies,false)){
                                switchCandy(this.candyToSwitchVertical[0],this.candyToSwitchVertical[1]);
                            }
                        }
                        this.candyToSwitchVertical[0] = null;
                        this.candyToSwitchVertical[1] = null;
                        this.offsetPaint.setY(0);
                    }
                }else if(
                        this.candyToSwitchVertical[1] != null &&
                        this.candyToSwitchVertical[1].getX() == currentPaint.getX()/70 &&
                        this.candyToSwitchVertical[1].getY() == currentPaint.getY()/70
                ){
                    g.drawImage(candy.getImg(), currentPaint.getX(),currentPaint.getY() - this.offsetPaint.getY(),70,70,this);
                }else if (
                        this.selectedCandy != null &&
                        this.selectedCandy.getX() == (currentPaint.getX()/70) &&
                        this.selectedCandy.getY() == (currentPaint.getY()/70)
                ){
                    //paint selected candy with greater border and smaller candy image
                    g.setColor(new Color(70,100,140));
                    g.fillRect(currentPaint.getX(), currentPaint.getY(), 70,70);
                    g.setColor(new Color(100,200,250));
                    g.fillRect(currentPaint.getX()+5, currentPaint.getY()+5, 60,60);
                    g.drawImage(candy.getImg(), currentPaint.getX() +5, currentPaint.getY() +5,60,60,this);
                }else{
                    g.drawImage(candy.getImg(), currentPaint.getX(), currentPaint.getY(),70,70,this);
                }
                currentPaint.setX(currentPaint.getX()+70);

            }
            currentPaint.setX(0);
            currentPaint.setY(currentPaint.getY()+70);
        }
        //paint game information of the current level
        g.setColor(Color.BLACK);
        g.drawString(
                "LEVEL: " + this.level.getCurrentLevel()
                + "           MOVES LEFT: " + (this.level.getMovesLimit()-this.movesDone)
                + "           POINT FOR THE NEXT LEVEL: " + (this.level.getScoreToReach() - this.score < 0 ? 0 : this.level.getScoreToReach() - this.score)
                + "           SCORE: " + this.score,10,432);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        //update paint in base of timer delay
        repaint();
    }
}

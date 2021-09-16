package com.company;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Candy {
    private Color color;

    public Candy(){

        //set a random color between 3^2 (8 colours) without getting black and white
        int r = (int) (Math.round(Math.random()))*255;
        int g = (int) (Math.round(Math.random()))*255;
        int b = (int) (Math.round(Math.random()))*255;
        while(r == g && g == b){
            r = (int) (Math.round(Math.random()))*255;
            g = (int) (Math.round(Math.random()))*255;
            b = (int) (Math.round(Math.random()))*255;
        }
        this.color = new Color(r,g,b);
    }

    public Candy(Color color){
        this.color = color;
    }

    public Candy(String name){
        //constructor to creare the specials candies
        if (name.equals("special")){
            this.color = new Color(200,200,200);
        }else if (name.equals("donut")) {
            this.color = new Color(100, 100, 100);
        }
    }

    public Image getImg(){

        //get the file name of each candy type basing on its color
        String name;

        if (this.color.equals(new Color(255,0,0))){
            name = "redCandy";
        }else if (this.color.equals(new Color(255,0,255))){
            name = "violetCandy";
        }else if (this.color.equals(new Color(0,255,0))){
            name = "greenCandy";
        }else if (this.color.equals(new Color(0,255,255))){
            name = "orangeCandy";
        }else if (this.color.equals(new Color(0,0,255))) {
            name = "blueCandy";
        }else if (this.color.equals(new Color(255,255,0))){
            name = "yellowCandy";
        }else if (this.color.equals(new Color(200,200,200))) {
            name = "specialCandy";
        }else if (this.color.equals(new Color(100,100,100))) {
            name = "donutSpecial";
        }else{
            name = "";
        }

        try {
            return ImageIO.read(new File("img/" + name + ".png"));
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}

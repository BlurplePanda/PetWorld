// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP102/112 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP-102-112 - 2023T1, Assignment 7
 * Name:
 * Username:
 * ID:
 */

import ecs100.*;
import java.awt.Color;

/**
 * An Animal object is an animal character, displayed on the screen
 * that can 
 *   turn (change the direction it is facing),
 *   speak
 * An animal object can be selected or not
 */

public class Animal {
    public static final Color HIGHLIGHT_COL = new Color(97, 255, 89);

    /* Fields representing the state of a Animal */
    private String animal;
    private double imageX = -100;   // top left corner of image
    private double imageY = -100;
    private String direction;
    private boolean selected = false;

    /* Fields containing dimensions of Animals */
    private int imageHeight = 62;
    private int imageWidth = 70;

    private int wordsWidth = 160;
    private int wordsHeight = 45;
    private int wordSize = 12;

    /** Constructor requires
     *  - the type of animal,
     *  - and the coordinates (left, top) of where it should be placed.
     *    For example
     *    new Animal("dog", 100, 50);
     */
    public Animal(String typeOfAnimal, double x, double y ){
        this(typeOfAnimal, "left", x, y);
    }

    /** Constructor requires
     *  - the type of animal,
     *  - the direction of the animal (can be just "l" or "r"),
     *  - and the coordinates (left, top) of where it should be placed.
     *    For example
     *    new Animal("dog", "left", 100, 50);
     */
    public Animal(String typeOfAnimal, String direction, double x, double y ){
        this.animal=typeOfAnimal;
        if (direction.startsWith("r")) this.direction = "right";
	else                           this.direction="left";
        this.imageX = x;
        this.imageY = y;
        UI.setFontSize(wordSize);
        this.draw();
    }

    /** Selects the animal so it can be highlighted */
    public void select(){
	selected = true;
    }

    /** Unselects the animal */
    public void unselect(){
	selected = false;
    }
    
    /** Returns true if the point (u, v) is on top of the animal */
    public boolean on(double u, double v) {
        if((u>=imageX && u<=imageX+imageWidth)&&(v>=imageY && v<=imageY+imageHeight)){
            return true;
        } else {
            return false;
        }
	
    }

    /** Makes the Animal change the direction it is facing */
    public void turn() {
        if (direction == "right"){
            direction = "left";
        } else if (direction == "left") {
            direction = "right";
        }
	
    }

    /** Changes the position of the animal to x and y */
    public void moveTo(double x, double y) {
        imageX = x;
        imageY = y;
	
    }

    /** Makes the Animal say something in a speech box */
    public void speak(String words) {
        double boxX = this.imageX;
        double boxY = this.imageY - this.wordsHeight - 20;

        if (this.direction.equals("right"))
            boxX += 15 ;
        else
            boxX +=  this.imageWidth  - 15 - this.wordsWidth;

        UI.eraseRect(boxX, boxY, this.wordsWidth, this.wordsHeight);
        UI.drawRect(boxX, boxY, this.wordsWidth, this.wordsHeight);
        UI.drawString(words, boxX + 5, boxY + this.wordsHeight/2.0 + 3);

        UI.sleep(1000);

        UI.eraseRect(boxX, boxY, this.wordsWidth+1, this.wordsHeight+1);
    }

    /** Draws the Animal
     * If the animal is selected draw a box around it.
     */
    public void draw(){
        String fname = null;
        fname = "animals/" + this.animal +"-"+this.direction+".gif"; 
        UI.drawImage(fname, this.imageX, this.imageY, this.imageWidth, this.imageHeight);
	if (selected){
	    UI.setColor(HIGHLIGHT_COL);
	    UI.drawRect(this.imageX-2, this.imageY-2, this.imageWidth+4, this.imageHeight+4);
	    UI.setColor(Color.black);
	}
    }

    /** Returns a string description of the animal in a form suitable for
     *  writing to a file in order to recreate the animal
     */
    /*public String toString() {
        *//*# YOUR CODE HERE *//*

    }*/
}


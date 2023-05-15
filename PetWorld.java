// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP102/112 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP-102-112 - 2023T1, Assignment 7
 * Name: Amy Booth
 * Username: boothamy
 * ID: 300653766
 */

import ecs100.*;
import java.util.*;
import java.nio.file.*;
import java.io.*;

/** The PetWorld program allows the user to create, save, and reload files
 *    specifying a world consisting of a list of animal objects.
 *    The program allows the user to
 *      - add a new animal to the world
 *      - remove an animal from the world
 *      - move a animal to a different position
 *      - make the animal turn. speak
 *      - save the current world to a file
 *      - load a previous world from a file.
 *        
 *    Classes
 *      The PetWorld class handles all the user interaction:
 *        buttons, textfields, mouse actions, file opening and closing.
 *        It stores the current world in an ArrayList of Animal .
 *
 *      The Animal class
 *
 *    Files:
 *      A world is stored in a file containing one line for each animal,
 *        
 *    User Interface:
 *        There are buttons for dealing with the whole world (New, Open, Save),
 *         a button and a textfiled for specifying the next animal to add, and
 *         buttons for removing and moving animals, as well at making them 
 *         do something.
 */

public class PetWorld {

    // Fields
    private static final String[] types = new String[] {"bird", "dinosaur", "dog", "grasshopper",
                                           "snake", "tiger", "turtle"};

    private ArrayList<Animal> world = new ArrayList<>();
    private String animalType;
    private String direction;
    private String speech;
    private boolean addSelected;
    private boolean moveSelected;
    private Animal selected;

    /**
     * User interface has buttons for the actions and text field
     */
    public void setupGUI(){
        UI.initialise();
        UI.addButton("New", this::startWorld);
        UI.addButton("Set Animal Type", this::setAnimalType);
        UI.addTextField("Direction(r/l)", this::setDirection);
        UI.addButton("Add", this::setToAdd);
        UI.addButton("Delete", this::deleteAnimal);
        UI.addButton("Move", this::setToMove);
        UI.addButton("Turn", this::turn);
        UI.addTextField("Speech", this::setSpeech);
        UI.addButton("Speak", this::speak);
        UI.addButton("Quit", UI::quit);
        UI.setMouseListener(this::doMouse);

    }

    // Methods to add an animal
    /**
     * Ask the user for a type of animal and assign it to the type of animal field
     * Must pass an array of the types of animal to getOptionFromList
     */
    public void setAnimalType(){
        String type = getOptionFromList("Choose animal type", types);
        if (type==null ) {return;}
        UI.printMessage("Setting animal type to "+type);
        animalType = type;

    }

    /**
     * Set the direction the animal is facing when created
     */
    public void setDirection (String dir){
        direction=dir;
    }

    /**
     * Set a boolean indicating whether releasing the mouse results in adding or selecting 
     * an animal
     */
    public void setToAdd(){ 
        if (addSelected) {
            addSelected = false;
            UI.printMessage("Not adding an animal");
        } else {
            addSelected = true;
            UI.printMessage("Adding an animal");
        }

    }

    /**
     * Set a boolean indicating whether to move the animal to the mouse
     */
    public void setToMove(){
        if (moveSelected){
            moveSelected = false;
            UI.printMessage("Not moving an animal");
        } else {
            moveSelected = true;
            UI.printMessage("Moving an animal");
        }
    }

    /** Construct a new Animal object of the appropriate type and direction
     *    Do not create an animal if it does not have a type
     *    Adds the animal to the end of the collection.
     */
    public void addAnimal(double x, double y){
        if(animalType!=null){
            if(direction!=null) {
                world.add(new Animal(animalType, direction, x, y));
            }
            else {
                world.add(new Animal(animalType, x, y));
            }
            UI.printMessage("Animal added!");
            addSelected = false;
        }

    }

    // Respond to mouse events 
    /**
     *  When the Mouse is released, depending on the current action,
     *  - perform the action (add, move or select)
     *  Redraw the drawing.
     *  It is easiest to call other methods to actually do the work,
     */
    public void doMouse(String mouseAction, double x, double y) {
        if (mouseAction.equals("released")){
            if(addSelected) {
                addAnimal(x, y);
            } else if (moveSelected){
                move(x, y);
            } else {
                if (selected != null){selected.unselect();}
                selected = findAnimal(x, y);
                if (selected != null){selected.select();}
            }
        }

        this.drawWorld();
    }

    /** Draws all the animals in the list on the graphics pane
     *  First clears the graphics pane, then draws each animal,
     *  Finally repaints the graphics pane
     */
    public void drawWorld(){
        UI.clearGraphics();
        for(Animal animal : world){
            animal.draw();
        }

    }   

    /** Checks each animal in the list to see if the point (x,y) is on the animal.
     *  It returns the topmost animal for which this is true.
     *     Returns null if there is no such animal.
     */
    public Animal findAnimal(double x, double y){
        for(int i = world.size()-1; i >= 0; i--){
            if (world.get(i).on(x,y)){
                return world.get(i);
            }
        }

        // failed to find any animal that the point was over 
        return null;  
    }

    /** Start a new world -
     *  initialise the world ArrayList and clear the graphics pane. 
     */
    public void startWorld(){
        world = new ArrayList<>();
        UI.clearGraphics();

    }

    // Add here methods to delete, turn, speak, move, load and save
    /**
     * Method to delete an animal from the world
     */
    public void deleteAnimal(){
        if (selected != null){
            world.remove(selected);
        }
        this.drawWorld();
    }

    /**
     * Method to change the direction an animal is facing
     */
    public void turn(){
        if(selected != null){
            selected.turn();
        }
        this.drawWorld();
    }

    /**
     * Sets text for animal to speak
     * @param input from gui textfield
     */
    public void setSpeech(String input){
        speech = input;
    }

    /**
     * Makes selected animal speak text from textfield
     */
    public void speak(){
        if(selected != null){
            selected.speak(speech);
        }
    }

    public void move(double x, double y){
        if (selected != null){
            selected.moveTo(x,y);
            moveSelected = false;
        }

    }

    /**
     * Method to get a string from a dialog box with an array options
     */
    public String getOptionFromList(String question, String[] options){
        //        Object[] possibilities = options.toArray();
        //Arrays.sort(possibilities);
        return (String)javax.swing.JOptionPane.showInputDialog
        (UI.getFrame(),
            question, "",
            javax.swing.JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            options[0].toString());
    }

    /**
     * main method: set up the user interface
     */
    public static void main(String[] args){
        PetWorld petWorld = new PetWorld();
        petWorld.setupGUI();   // set up the interface
    }

}


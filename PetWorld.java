// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP102/112 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP-102-112 - 2023T1, Assignment 7
 * Name: Amy Booth
 * Username: boothamy
 * ID: 300653766
 */

import ecs100.*;

import javax.swing.*;
import java.util.*;
import java.nio.file.*;
import java.io.*;
import java.awt.Color;

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
    //private Animal selected;
    private double startX; // x pos of mouse when previously clicked
    private double startY; // y pos "
    private JButton addBtn;
    private JButton moveBtn;
    private JButton typeBtn;
    private JButton groupBtn;
    private boolean groupSelected;
    private ArrayList<Animal> selected = new ArrayList<>();

    /**
     * User interface has buttons for the actions and text field
     */
    public void setupGUI(){
        UI.initialise();
        UI.addButton("New", this::startWorld);
        UI.addButton("Save", this::saveWorld);
        UI.addButton("Open", this::loadWorld);
        typeBtn = UI.addButton("Set Animal Type", this::setAnimalType);
        UI.addTextField("Direction(r/l)", this::setDirection);
        addBtn = UI.addButton("Add", this::setToAdd);
        UI.addButton("Delete", this::deleteAnimal);
        moveBtn = UI.addButton("Move", this::setToMove);
        UI.addButton("Turn", this::turn);
        UI.addTextField("Speech", this::setSpeech);
        groupBtn = UI.addButton("Group", this::setToGroup);
        UI.addButton("Speak", this::speak);
        UI.addButton("Quit", UI::quit);
        UI.setMouseMotionListener(this::doMouse);

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
        typeBtn.setText("Current type: "+animalType);

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
            addBtn.setBackground(null);
            UI.printMessage("Not adding an animal");
        } else {
            addSelected = true;
            moveSelected = false;
            moveBtn.setBackground(null);
            addBtn.setBackground(new Color(139, 72, 246));
            UI.printMessage("Adding an animal");
        }

    }

    /**
     * Set a boolean indicating whether to move the animal to the mouse
     */
    public void setToMove(){
        if (moveSelected){
            moveSelected = false;
            moveBtn.setBackground(null);
            UI.printMessage("Not moving an animal");
        } else {
            moveSelected = true;
            addSelected = false;
            addBtn.setBackground(null);
            moveBtn.setBackground(new Color(139, 72, 246));
            UI.printMessage("Moving an animal");
        }
    }

    /**
     * Set a boolean indicating whether the selection/everything is singular or group mode
     */
    public void setToGroup(){
        if (groupSelected) {
            groupSelected = false;
            groupBtn.setBackground(null);
            UI.printMessage("Selecting singular animals");
            for (Animal a : selected){
                a.unselect();
            }
            selected.clear();
            this.drawWorld();
        } else {
            groupSelected = true;
            groupBtn.setBackground(new Color(139, 72, 246));
            UI.printMessage("Selecting multiple animals (changing to singular will clear selections)");
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
            addBtn.setBackground(null);
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
        if (mouseAction.equals("pressed")){
                startX = x;
                startY = y;
        }
        if (mouseAction.equals("released")){
            if(addSelected) {
                addAnimal(x, y);
            } else if (moveSelected){
                move(startX, startY, x, y);
            } else {
                Animal new_selection = findAnimal(x,y);
                if (!selected.isEmpty()){
                    if (new_selection != null){
                        if (groupSelected){
                            if (selected.contains(new_selection)) {
                                selected.remove(new_selection);
                            } else {
                                new_selection.select();
                                selected.add(new_selection);
                            }
                        } else {
                            selected.get(0).unselect();
                            if (new_selection == selected.get(0)) { //not specified in instructions
                                selected.remove(0);
                            } else {
                                selected.remove(0);
                                selected.add(0, new_selection);
                                selected.get(0).select();
                            }
                        }
                    } else { //not specified in instructions
                        // if clicking off an animal does anything put it here
                    }
                } else {
                    if (new_selection != null){
                        selected.add(new_selection);
                        selected.get(0).select();
                    }
                }
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
        boolean confirmed = false;
        if (world.size()>0) {
            confirmed = UI.askBoolean("Are you sure you want to lose your animals?");
        } else {
            confirmed = true;
        }
        if (confirmed){
            world = new ArrayList<>();
            UI.clearGraphics();
        }


    }

    // Add here methods to delete, turn, speak, move, load and save
    /**
     * Method to delete an animal from the world
     */
    public void deleteAnimal(){
        for (Animal a : selected) {
            world.remove(a);
        }
        this.drawWorld();
    }

    /**
     * Method to change the direction an animal is facing
     */
    public void turn(){
        for (Animal a : selected){
            a.turn();
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
        for (Animal a : selected){
            a.speak(speech);
        }
    }

    /**
     * Method to move selected animal
     */
    public void move(double x1, double y1, double x2, double y2){
        /*if (selected != null){
            //selected.moveTo(x, y);
            if (selected.on(x1, y1)){
                selected.moveBy(x2-x1, y2-y1);
                moveSelected = false;
                moveBtn.setBackground(null);
            }
        }*/
    }

    /**
     * Method to save world info to file
     */
    public void saveWorld(){
        try {
            String name = UI.askString("What do you want to call your file? (eg \"myWorld\", \"tuesday animals\", etc)");
            PrintStream out = new PrintStream(name+".txt");
            for (Animal animal : this.world){
                out.println(animal.toString());
            }
            out.close();
        }
        catch (IOException e){ UI.println("File saving failed: "+e); }
    }

    /**
     * Method to load world info from file
     */
    public void loadWorld(){
        try {
            this.world.clear();
            List<String> lines = Files.readAllLines(Path.of(UIFileChooser.open()));
            for (String line : lines){
                Scanner sc = new Scanner(line);
                String type = sc.next();
                String direction = sc.next();
                double x = sc.nextDouble();
                double y = sc.nextDouble();
                this.world.add(new Animal(type, direction, x, y));
            }
        }
        catch (IOException e){ UI.println("File loading failed: "+e); }
        catch (NullPointerException e){ UI.println("You did not choose a file!"); }
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


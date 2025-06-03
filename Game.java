/* Game Class Starter File
 * Authors: Sayeedus Salihin, Joel A. Bianchi
 * Last Edit: 6/3/25
 * using new Screen show method
 */

//import processing.sound.*;

import processing.core.PApplet;
import processing.core.PImage;

public class Game extends PApplet{

  //------------------ GAME VARIABLES --------------------//

  // VARIABLES: Processing variable to do Processing things
  PApplet p;

  // VARIABLES: Title Bar
  String titleText = "SuperCoolJumpManAdventure";
  String extraText = "CurrentLevel?";
  String name = "";

  // VARIABLES: Whole Game
  AnimatedSprite runningHorse;
  boolean doAnimation;
  AnimatedSprite jumpMan;

  // VARIABLES: Splash Screen
  Screen splashScreen;
  PImage splashBg;
  String splashBgFile = "images/apcsa.png";
  //SoundFile song;

  // VARIABLES: Original Grid version of Game
  Grid grid0;
  String grid1BgFile = "images/grasslands.png";
  PImage grid1Bg;
  int player2Row = 0;
  int player2Col = 2;
  int health = 3;

  Button b1;
  boolean isJumping = false;
  int jumpTimer = 0;
  final int maxJumpFrames = 8;


  // VARIABLES: world1World Pixel-based Screen
  World world1;
  String world1BgFile = "images/grasslands.png";
  PImage world1Bg;
  // String player3File = "images/zapdos.png";
  // Sprite player3; //Use Sprite for a pixel-based Location
  float velocityY = 0;
  float gravity = 0.8f;
  float jumpStrength = -15.0f;
  boolean onGround = false;
  float groundY = 500;
  int player3startX = 50;
  int player3startY = 300;

  //VARIABLES: world2World Pixel-based Platformer
  World world2;
  String world2BgFile = "images/sky.png";
  PImage world2Bg;
  Platform plat;
  // String player4

  // VARIABLES: EndScreen
  World endScreen;
  String endBgFile = "images/youwin.png";
  PImage endBg;


  // VARIABLES: Tracking the current Screen being displayed
  Screen currentScreen;
  CycleTimer slowCycleTimer;
  long gravityTimer = 0;               // tracks last gravity update
  int gravityInterval = 400;          // milliseconds between falls (lower = faster)


  boolean start = true;

  PImage enemyImg;  // enemy image for level 1


  //------------------ REQUIRED PROCESSING METHODS --------------------//

  // Processing method that runs once for screen resolution settings
  public void settings() {
    //SETUP: Match the screen size to the background image size
    size(800,600);  //these will automatically be saved as width & height

    // Allows p variable to be used by other classes to access PApplet methods
    p = this;
    
  }

  //Required Processing method that gets run once
  public void setup() {

    p.imageMode(p.CORNER);    //Set Images to read coordinates at corners
    //fullScreen();   //only use if not using a specfic bg image
    
    //SETUP: Set the title on the title bar
    surface.setTitle(titleText);

    //SETUP: Load BG images used in all screens
    splashBg = p.loadImage(splashBgFile);
    splashBg.resize(p.width, p.height);
    grid1Bg = p.loadImage(grid1BgFile);
    grid1Bg.resize(p.width, p.height);
    world1Bg = p.loadImage(world1BgFile);
    //world1Bg.resize(p.width, p.height);
    endBg = p.loadImage(endBgFile);
    endBg.resize(p.width, p.height);

    //SETUP: Screens, Worlds, Grids
    splashScreen = new Screen(this, "splash", splashBg);
    grid0 = new Grid(this, "grasslands", grid1Bg, 6, 8);
    //grid1Grid.startPrintingGridMarks();
    world1 = new World(p, "sky", world1BgFile, 4.0f, 0.0f, -800.0f); //moveable World constructor --> defines center & scale (x, scale, y)???
    System.out.println( "World constructed: " + Util.toStringPImage(world1.getBgImage()));
       
    // world1World = new World("sky", world1Bg);   //non-moving World construtor
    endScreen = new World(p, "end", endBg);
    currentScreen = splashScreen;

    //SETUP: All Game objects
    runningHorse = new AnimatedSprite(p, "sprites/horse_run.png", "sprites/horse_run.json", 50.0f, 75.0f, 1.0f);

    //SETUP: Level 1
    jumpMan = new AnimatedSprite(p, "sprites/chick_walk.png", "sprites/chick_walk.json", 0.0f, 0.0f, 0.5f);
    grid0.setTileSprite(new GridLocation (player2Row, player2Col), jumpMan);

    b1 = new Button(p, "rect", 625, 525, 150, 50, "GoTo Level 2");
    // b1.setFontStyle("fonts/spidermanFont.ttf");
    b1.setFontStyle("Calibri");
    b1.setTextColor(PColor.WHITE);
    b1.setButtonColor(PColor.BLACK);
    b1.setHoverColor(PColor.get(100,50,200));
    b1.setOutlineColor(PColor.WHITE);

    System.out.println("Done loading Level 1 ...");
    
    //SETUP: Level 2
    player3 = new Sprite(p, player3File, 0.25f);
    player3.moveTo(player3startX, player3startY);
    world1.addSpriteCopyTo(runningHorse, 100, 200);  //example Sprite added to a World at a location, with a speed
    world1.printWorldSprites();
    System.out.println("Done loading Level 2 ...");

    // SETUP: Level 3
    world2Bg = loadImage(world2BgFile);
    world2Bg.resize(p.width, p.height);
    world2 = new World(p,"platformer", world2Bg);
    plat = new Platform(p, PColor.MAGENTA, 500.0f, 100.0f, 200.0f, 20.0f);
    plat.setOutlineColor(PColor.BLACK);
    plat.startGravity(5.0f); //sets gravity to a rate of 5.0
    world1.addSprite(plat);    
    world1.addSprite(new Platform(p, PColor.GREEN, 100, 400, 150, 20));
    world1.addSprite(new Platform(p, PColor.BLUE, 300, 300, 200, 20));
    world1.addSprite(new Platform(p, PColor.RED, 600, 350, 180, 20));

    System.out.println("Done loading Level 3 ...");


    //SETUP: Sound
    // Load a soundfile from the sounds folder of the sketch and play it back
     //song = new SoundFile(p, "sounds/Lenny_Kravitz_Fly_Away.mp3");
     //song.play();
    
    System.out.println("Game started...");

    enemyImg = p.loadImage("images/enemy.png");
    enemyImg.resize(grid0.getTileWidth(), grid0.getTileHeight());


  } //end setup()


  //Required Processing method that automatically loops
  //(Anything drawn on the screen should be called from here)
  public void draw() {

    // Update Screen Visuals
    updateTitleBar();
    updateScreen();

    // Gravity for Level 1 (time-based)
    if (currentScreen == grid0) {
        int maxRow = grid0.getNumRows() - 1;
        GridLocation belowLoc = new GridLocation(player2Row + 1, player2Col);

        // Only fall if player is above bottom row and the tile below is empty
        if (player2Row < maxRow && !grid0.hasTileSprite(belowLoc)) {
            long currentTime = p.millis();
            if (currentTime - gravityTimer >= gravityInterval) {
                GridLocation oldLoc = new GridLocation(player2Row, player2Col);
                player2Row++;
                grid0.clearTileSprite(oldLoc);
                grid0.setTileSprite(belowLoc, jumpMan);
                gravityTimer = currentTime;
            }
        }
    }
    
    // Apply gravity to player3 if in world1World
   /*  if(currentScreen == world1) {
        velocityY += gravity;
        player3.move(0, velocityY);

        // Check if landed
        if(player3.getBottom() >= groundY) {
            player3.setBottom(groundY);
            velocityY = 0;
            onGround = true;
        } else {
            onGround = false;
        }
    }
      */

if(currentScreen == world1) {
    velocityY += gravity;
    if (isJumping) {
    jumpTimer++;
    if (jumpTimer > maxJumpFrames) {
        isJumping = false;
    }
}

    player3.move(0, velocityY);
    onGround = false;

    for (Sprite s : world1.getSprites()) {
        if (s instanceof Platform) {
            if (player3.getBottom() >= s.getTop() &&
                player3.getBottom() <= s.getTop() + 20 &&
                player3.getCenterX() >= s.getLeft() &&
                player3.getCenterX() <= s.getRight()) {
                
                player3.setBottom(s.getTop());
                velocityY = 0;
                onGround = true;
                isJumping = false;
                jumpTimer = 0;

            }
        }
    }

    if(player3.getBottom() >= groundY) {
        player3.setBottom(groundY);
        velocityY = 0;
        onGround = true;
    }
}



    // Set Timers
    int cycleTime = 1;  //milliseconds
    int slowCycleTime = 300;  //milliseconds
    if(slowCycleTimer == null){
      slowCycleTimer = new CycleTimer(p, slowCycleTime);
    }

    // Move Sprites
    if(slowCycleTimer.isDone()){
      populateSprites();
      moveSprites();
    }

    // Pause Cycle
    currentScreen.pause(cycleTime);   // slows down the game cycles

    // Check for end of game
    if(isGameOver()){
      endGame();
    }

  } //end draw()



  public void populateSprites() {
    if (currentScreen == grid0) {
      int lastCol = grid0.getNumCols() - 1;

      for (int row = 0; row < grid0.getNumRows(); row++) {
        GridLocation loc = new GridLocation(row, lastCol);

        double spawnChance = Math.random() * 0.2;
            if (Math.random() < spawnChance && !grid0.hasTileImage(loc)) {
          grid0.setTileImage(loc, enemyImg);
        }
      }
    }
  }

  public void moveSprites() {

    if (currentScreen == grid0) {

      for (int row = 0; row < grid0.getNumRows(); row++) {
        for (int col = 0; col < grid0.getNumCols(); col++) {
          GridLocation loc = new GridLocation(row, col);
          if (grid0.hasTileImage(loc)) {

            // Skip player tile
            if (loc.getRow() == player2Row && loc.getCol() == player2Col) continue;

            int newCol = col - 1;   //moves enemies to the left
            if (newCol >= 0) {
              GridLocation newLoc = new GridLocation(row, newCol);

              if (loc.getRow() == player2Row && newCol == player2Col) {
                System.out.println("Enemy collided with player!");
                grid0.clearTileImage(loc);
                health--; // optional: track player health
              } else if (!grid0.hasTileImage(newLoc)) {
                grid0.setTileImage(newLoc, grid0.getTileImage(loc));
                grid0.clearTileImage(loc);
              }
            } else {
              // enemy left the screen
              grid0.clearTileImage(loc);
            }
          }
        }
      }
    }   // close grid1 moving




  }


  //------------------ USER INPUT METHODS --------------------//


  //Known Processing method that automatically will run whenever a key is pressed
  public void keyPressed(){

    //check what key was pressed
    System.out.println("\nKey pressed: " + p.keyCode); //key gives you a character for the key pressed
    
    //KEYS FOR grid0
    if(currentScreen == grid0){

      GridLocation oldLoc = new GridLocation(player2Row, player2Col);

      // Move Up
      if(p.key == 'w' || p.keyCode == UP) {
          if(player2Row > 0) player2Row--;
          jumpMan.move(0,-5);
      }

      // Move Down
      if(p.key == 's' || p.keyCode == DOWN) {
          if(player2Row < grid0.getNumRows() - 1) player2Row++;
      }

      // Move Left
      if(p.key == 'a' || p.keyCode == LEFT) {
          if(player2Col > 0) player2Col--;
      }

      // Move Right
      if(p.key == 'd' || p.keyCode == RIGHT) {
          if(player2Col < grid0.getNumCols() - 1) player2Col++;
      }

      // Update Sprite Position
      GridLocation newLoc = new GridLocation(player2Row, player2Col);
      grid0.clearTileSprite(oldLoc);
      grid0.setTileSprite(newLoc, jumpMan);
    }

    //KEYS FOR world1
    if(currentScreen == world1){
    if ((p.key == 'w' || p.keyCode == UP) && onGround) {
    velocityY = jumpStrength;
    isJumping = true;
    jumpTimer = 0;
    player3.move(5, 0); // forward boost on jump
}
}



    // if the 'n' key is pressed, ask for their name
    if(p.key == 'n'){
      name = Input.getString("What is your name?");
    }

    //CHANGING SCREENS BASED ON KEYS
    //change to grid1 if 1 key pressed, world1 if 2 key is pressed
    if(p.key == '0'){
      currentScreen = grid0;
    } else if(p.key == '1'){
      currentScreen = world1;
    } else if(p.key == '2'){
      currentScreen = world2;
      plat.moveTo(500.0f, 100.0f);
      plat.setSpeed(0,0);
    }


  }

  // Known Processing method that automatically will run when a mouse click triggers it
  public void mouseClicked(){
    
    // Print coordinates of mouse click
    System.out.println("\nMouse was clicked at (" + p.mouseX + "," + p.mouseY + ")");

    // Display color of pixel clicked
    int color = p.get(p.mouseX, p.mouseY);
    PColor.printPColor(p, color);

    // Print grid coordinate clicked
    if(currentScreen instanceof Grid){
      System.out.println("Grid location --> " + ((Grid) currentScreen).getGridLocation());
    }

    // "Mark" the grid coordinate to track the state of the Grid
    if(currentScreen instanceof Grid){
      ((Grid) currentScreen).setMark("X",((Grid) currentScreen).getGridLocation());
    }

  }


  //------------------ CUSTOM  GAME METHODS --------------------//

  // Updates the title bar of the Game
  public void updateTitleBar(){

    if(!isGameOver()) {
      //set the title each loop
      surface.setTitle(titleText + "    " + extraText + " " + name + ": " + health);

      //adjust the extra text as desired
    
    }
  }

  // Updates what is drawn on the screen each frame
  public void updateScreen(){

    // UPDATE: first lay down the Background
    currentScreen.showBg();

    // UPDATE: splashScreen
    if(currentScreen == splashScreen){

      // Print an s in console when splashscreen is up
      System.out.print("s");

      // Change the screen to level 1 between 3 and 5 seconds
      if(splashScreen.getScreenTime() > 3000 && splashScreen.getScreenTime() < 5000){
        currentScreen = grid0;
      }
    }

    // UPDATE: grid1Grid Screen
    if(currentScreen == grid0){

      // Print a '1' in console when grid1
      System.out.print("0");

      // Displays the player2 image
      GridLocation player2Loc = new GridLocation(player2Row, player2Col);
      grid0.setTileSprite(player2Loc, jumpMan);

      // Moves to next level based on a button click
      b1.show();
      if(b1.isClicked()){
        System.out.println("\nButton Clicked");
        currentScreen = world1;
      }
    
    }
    
    // UPDATE: world1World Screen
    if(currentScreen == world1){

      // Print a '2' in console when world1
      System.out.print("1");

      world1.moveBgXY(-0.3f, 0f);  //adjust speeds of moving backgrounds, -3.0f for 100 ms delays
      player3.show();

    }

    // UPDATE: world2World Screen
    if(currentScreen == world2){

      if((p.key == 'w' || p.keyCode == UP) && onGround){
    velocityY = jumpStrength;
    onGround = false;
}


      // Print a '3 in console when world2
      System.out.print("2");


    }

    // UPDATE: End Screen
    // if(currentScreen == endScreen){

    // }


    // UPDATE: Other built-in to current World/Grid/HexGrid
    currentScreen.show();

  }




  // Checks if there is a collision between Sprites on the Screen
  public boolean checkCollision(GridLocation loc, GridLocation nextLoc){

    //Check what image/sprite is stored in the CURRENT location
    // PImage image = grid.getTileImage(loc);
    // AnimatedSprite sprite = grid.getTileSprite(loc);

    //if empty --> no collision

    //Check what image/sprite is stored in the NEXT location

    //if empty --> no collision

    //check if enemy runs into player

      //clear out the enemy if it hits the player (using cleartTileImage() or clearTileSprite() from Grid class)

      //Update status variable

    //check if a player collides into enemy

    return false; //<--default return
  }

  // Indicates when the main game is over
  public boolean isGameOver(){
    
    return false; //by default, the game is never over
  }

  // Describes what happens after the game is over
  public void endGame(){
      System.out.println("Game Over!");

      // Update the title bar

      // Show any end imagery
      currentScreen = endScreen;

  }


} //close class

/* Game Class Starter File
 * Authors: Sayeedus Salihin, Joel A. Bianchi
 * Last Edit: 6/12/25
 * using new Screen show method
 * * MODIFIED TO INCLUDE:
 * - Player respawn on falling
 * - Jetpack item for teleporting to a new world
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
  String name = "Undefined";
  // VARIABLES: Whole Game
  AnimatedSprite runningHorse;
  boolean doAnimation;
  AnimatedSprite jumpMan;
  float gravity = 0.8f;
  int health = 3;
  // VARIABLES for jumpMan's physics state
  float jumpManVelocityX = 0; // Horizontal velocity for jumpMan
  float jumpManVelocityY = 0;
  boolean jumpManOnGround = false;
  float jumpStrength = -15.0f;
  long gravityTimer = 0;
  int gravityInterval = 400;


  // VARIABLES: splashScreen
  Screen splashScreen;
  PImage splashBg;
  String splashBgFile = "images/apcsa.png";
  //SoundFile song;


  // VARIABLES: world1 Pixel-based Screen
  World world1;
  String world1BgFile = "images/grasslands.png";
  PImage world1Bg;
  String enemyFile = "images/zapdos.png";
  Sprite enemy; //Use Sprite for a pixel-based Location
  float velocityY = 0; // Velocity for the original enemy sprite
  boolean onGround = false; // onGround state for the original enemy sprite
  float groundY = 500;
  int enemystartX = 50;
  int enemystartY = 300;
  Coordinate[] spawnPoints = {
    new Coordinate(100,100),
    new Coordinate(200,200)
  };
  //VARIABLES: world2World Pixel-based Platformer
  World world2;
  String world2BgFile = "images/sky.png";
  PImage world2Bg;
  Platform plat;

  // --- NEW FEATURE: JETPACK AND STARTING PLATFORM VARIABLES ---
  Sprite jetpack; // Jetpack sprite that teleports the player
  String jetpackFile = "images/jetpack.png"; // <-- NEW IMAGE FILE NEEDED
  Platform startPlatform; // A reference to the starting platform for respawning

  int testUpdate = 0;
  // VARIABLES: endScreen
  World endScreen;
  PImage endBg;
  String endBgFile = "images/youwin.png";
  // VARIABLES: Tracking the current Screen being displayed
  Screen currentScreen;
  CycleTimer slowCycleTimer;
  boolean start = true;
  //------------------ REQUIRED PROCESSING METHODS --------------------//

  // Processing method that runs once for screen resolution settings
  public void settings() {
    //SETUP: Match the screen size to the background image size
    size(800,600);
    //these will automatically be saved as width & height

    // Allows p variable to be used by other classes to access PApplet methods
    p = this;
  }

  //Required Processing method that gets run once
  public void setup() {

    p.imageMode(p.CORNER);
    //Set Images to read coordinates at corners
    //fullScreen();
    //SETUP: Set the title on the title bar
    surface.setTitle(titleText);
    //SETUP: Load BG images used in all screens
    splashBg = p.loadImage(splashBgFile);
    world1Bg = p.loadImage(world1BgFile);
    endBg = p.loadImage(endBgFile);
    //SETUP: If non-moving, Resize all BG images to exactly match the screen size
    splashBg.resize(p.width, p.height);
    world1Bg.resize(p.width, p.height);
    endBg.resize(p.width, p.height);

    //SETUP: Screens, Worlds, Grids
    splashScreen = new Screen(this, "splash", splashBg);
    world1 = new World(p, "sky", world1BgFile, 1.0f, 0.0f, 0.0f); //moveable World constructor --> defines center & scale (x, scale, y)???
    endScreen = new World(p, "end", endBg);
    currentScreen = splashScreen;

    //SETUP: Construct Game objects used in All Screens
    runningHorse = new AnimatedSprite(p, "sprites/horse_run.png", "sprites/horse_run.json", 50.0f, 75.0f, 1.0f);
    //SETUP: World 1
    // Initialize jumpMan with animation speed 0, so it doesn't animate when idle.
    jumpMan = new AnimatedSprite(p, "sprites/chick_walk.png", "sprites/chick_walk.json", 0.0f, 0.0f, 0.0f);
    jumpMan.moveTo(100, groundY - jumpMan.getH());
    // Set initial position
    
    enemy = new Sprite(p, enemyFile, 0.25f);
    enemy.moveTo(enemystartX, enemystartY);
    world1.addSprite(enemy);
    plat = new Platform(p, PColor.MAGENTA, 500.0f, 100.0f, 200.0f, 20.0f);
    plat.setOutlineColor(PColor.BLACK);
    plat.startGravity(5.0f);
    world1.addSprite(plat);

    // --- MODIFIED PLATFORM SETUP ---
    // Store the green platform in a variable for easy access to its position for respawning
    startPlatform = new Platform(p, PColor.GREEN, 100, 400, 150, 20);
    world1.addSprite(startPlatform);
    world1.addSprite(new Platform(p, PColor.BLUE, 300, 300, 200, 20));
    // Store red platform to place jetpack on it
    Platform redPlatform = new Platform(p, PColor.RED, 600, 350, 180, 20);
    world1.addSprite(redPlatform);
    
    // --- NEW FEATURE: SETUP THE JETPACK ---
    jetpack = new Sprite(p, jetpackFile, 0.2f); // Load the jetpack image
    // Position the jetpack on top of the center of the red platform
    jetpack.moveTo(redPlatform.getCenterX(), redPlatform.getTop() - jetpack.getH());
    world1.addSprite(jetpack); // Add the jetpack to the world
    
    world1.addSprite(jumpMan);
    world1.addSpriteCopyTo(runningHorse, 100, 200);
    world1.printWorldSprites();
    System.out.println("Done loading World 1 ...");

    // --- MODIFIED: SETUP WORLD 2 ---
    world2Bg = loadImage(world2BgFile);
    world2Bg.resize(p.width, p.height);
    world2 = new World(p,"platformer", world2Bg);
    // Add jumpMan to world2 so the player exists and is visible after teleporting
    world2.addSprite(jumpMan); 
    // Add some platforms to the new world to make it different
    world2.addSprite(new Platform(p, PColor.YELLOW, 100, 500, 150, 20));
    world2.addSprite(new Platform(p, PColor.CYAN, 350, 400, 150, 20));
    world2.addSprite(new Platform(p, PColor.MAGENTA, 600, 300, 150, 20));


    System.out.println("Done loading World 2 ...");
    //SETUP: Sound
    // Load a soundfile from the sounds folder of the sketch and play it back
     //song = new SoundFile(p, "sounds/Lenny_Kravitz_Fly_Away.mp3");
    //song.play();
    
    System.out.println("Game started...");



  } //end setup()


  //Required Processing method that automatically loops
  //(Anything drawn on the screen should be called from here)
  public void draw() {

    // DRAW LOOP: Update Screen Visuals
    updateTitleBar();
    updateScreen();

    // Physics and Collision Detection for World 1
    if (currentScreen == world1) {
        
        // --- JUMPMAN Physics ---
        jumpMan.move(jumpManVelocityX, 0); // Apply horizontal velocity
        jumpManVelocityY += gravity;
        jumpMan.move(0, jumpManVelocityY);
        jumpManOnGround = false; // Assume in the air unless a collision is found

        // Check for jumpMan collision with the ground
        if (jumpMan.getBottom() >= groundY) {
            jumpMan.setBottom(groundY);
            jumpManVelocityY = 0;
            jumpManOnGround = true;
        }

        // Check for jumpMan collision with platforms
        for (Sprite s : world1.getSprites()) {
            if (s instanceof Platform) {
                // Check if jumpMan is landing on a platform from above
                if (jumpManVelocityY > 0 &&
     
                     jumpMan.getBottom() >= s.getTop() &&
                    jumpMan.getBottom() <= s.getTop() + jumpManVelocityY &&
                    jumpMan.getRight() > s.getLeft() &&
                    jumpMan.getLeft() < s.getRight()) {
           
         
                    jumpMan.setBottom(s.getTop());
                    jumpManVelocityY = 0;
                    jumpManOnGround = true;
                }
            }
        }
        
        // --- NEW FEATURE: TELEPORT ON FALL ---
        // Check if player has fallen off the bottom of the screen
        if (jumpMan.getTop() > height) { // `height` is a processing variable for screen height
            // Teleport jumpMan back to the top of the starting green platform
            jumpMan.moveTo(startPlatform.getCenterX(), startPlatform.getTop() - jumpMan.getH());
            jumpManVelocityY = 0; // Reset vertical velocity to prevent immediate falling again
            System.out.println("Player fell! Respawning...");
        }

        // --- NEW FEATURE: JETPACK TELEPORT ---
        // Check for collision with the jetpack
        if (jumpMan.isOverlapping(jetpack) && jetpack.isSolid()) {
            System.out.println("Jetpack acquired! Teleporting...");
            // Change to the new world
            currentScreen = world2;
            // Reset jumpMan's position and velocity for the new world
            jumpMan.moveTo(100, 400); // Set a starting position in world2
            jumpManVelocityX = 0;
            jumpManVelocityY = 0;
            // Optional: make the jetpack disappear or become non-interactive
            jetpack.setSolid(false); // Make jetpack non-solid so it doesn't re-trigger
            jetpack.moveTo(-200, -200); // Move the jetpack off-screen so it's not visible
        }

        // --- ENEMY Physics (for the single 'enemy' instance) ---
        velocityY += gravity;
        enemy.move(0, velocityY);
        onGround = false; // Reset enemy ground status

        // Check enemy collision with the ground
        if (enemy.getBottom() >= groundY) {
            enemy.setBottom(groundY);
            velocityY = 0;
            onGround = true;
        }
        // Check enemy collision with platforms
        for (Sprite s : world1.getSprites()) {
            if (s instanceof Platform) {
                if (velocityY > 0 &&
                    enemy.getBottom() >= s.getTop() &&
         
                     enemy.getBottom() <= s.getTop() + 20 &&
                    enemy.getCenterX() >= s.getLeft() &&
                    enemy.getCenterX() <= s.getRight()) {
                    
                  
                     enemy.setBottom(s.getTop());
                    velocityY = 0;
                    onGround = true;
                }
            }
        }
    }
    
    // Check for end of game
    if(isGameOver()){
      endGame();
    }

  } //end draw()



  public void populateSprites() {
      // This method is no longer called from draw to prevent infinite spawning.
      // Spawning should be handled in setup() or based on specific game events.
  }

  public void moveSprites() {
      // This method is empty and no longer called.
  }


  //------------------ USER INPUT METHODS --------------------//

  //Known Processing method that automatically will run whenever a key is pressed
  public void keyPressed(){

    //check what key was pressed
    System.out.println("\nKey pressed: " + key + " " + p.keyCode);
    //KEYS FOR World1
    if(currentScreen == world1 || currentScreen == world2){ // Allow movement in both worlds

      // Move Left
      if(key == 'a' || p.keyCode == LEFT) {
          jumpManVelocityX = -5;
          jumpMan.setAnimationSpeed(0.5f); // Play animation
      }

      // Move Right
      if(key == 'd' || p.keyCode == RIGHT) {
          jumpManVelocityX = 5;
          jumpMan.setAnimationSpeed(0.5f); // Play animation
      }
      
      // Jump
      if((key == 'w' || p.keyCode == UP) && jumpManOnGround) {
          jumpManVelocityY = jumpStrength;
          jumpManOnGround = false;
      }
    }

    // if the 'n' key is pressed, ask for their name
    if(key == 'n'){
      name = Input.getString("What is your name?");
    }

    //CHANGING SCREENS BASED ON KEYS
    if(key == '1'){
      currentScreen = world1;
    } else if(key == '2'){
      currentScreen = world2;
      plat.moveTo(500.0f, 100.0f);
      plat.setSpeed(0,0);
    }
  }
  
  // Added to handle stopping the player's animation
  public void keyReleased() {
    if ((key == 'a' || p.keyCode == LEFT) && jumpManVelocityX < 0) {
        jumpManVelocityX = 0;
        jumpMan.setAnimationSpeed(0.0f); // Stop animation
    }
    if ((key == 'd' || p.keyCode == RIGHT) && jumpManVelocityX > 0) {
        jumpManVelocityX = 0;
        jumpMan.setAnimationSpeed(0.0f); // Stop animation
    }
  }


  // Known Processing method that automatically will run when a mouse click triggers it
  public void mouseClicked(){
    
    // Print coordinates of mouse click
    System.out.println("\nMouse was clicked at (" + p.mouseX + "," + p.mouseY + ")");
    // Display color of pixel clicked
    int color = p.get(p.mouseX, p.mouseY);
    PColor.printPColor(p, color);
    // if the Screen is a Grid, print grid coordinate clicked
    if(currentScreen instanceof Grid){
      System.out.println("Grid location --> " + ((Grid) currentScreen).getGridLocation());
    }

    // if the Screen is a Grid, "mark" the grid coordinate to track the state of the Grid
    if(currentScreen instanceof Grid){
      ((Grid) currentScreen).setMark("X",((Grid) currentScreen).getGridLocation());
    }

  }


  //------------------ CUSTOM  GAME METHODS --------------------//

  // Updates the title bar of the Game
  public void updateTitleBar(){

    if(!isGameOver()) {

      extraText = currentScreen.getName();
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
        currentScreen = world1;
      }
    }

   
    // UPDATE: world1World Screen
    if(currentScreen == world1){

      // Print a '1' in console when world1
      System.out.print("1");
    }

    // UPDATE: world2World Screen
    if(currentScreen == world2){
      // Print a '2' in console when world2
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

    return false;
  }

  // Indicates when the main game is over
  public boolean isGameOver(){
    
    return false;
    //by default, the game is never over
  }

  // Describes what happens after the game is over
  public void endGame(){
      System.out.println("Game Over!");
      // Update the title bar

      // Show any end imagery
      currentScreen = endScreen;
  }


} // end of Game class

/* Game Class Starter File
 * Authors: Sayeedus Salihin, Joel A. Bianchi
 * Last Edit: 6/18/25
 * MODIFIED TO INCLUDE:
 * - Player respawn on falling
 * - Jetpack item for teleporting to a new world & respawning jetpack
 * - Player starts on and respawns to a specific platform
 * - Level 2 movement is fixed
 * - Enemies on Level 2 with random spawning, movement, and collision
 * - More platforms on Level 2
 * - End point flag and win screen
 */

//import processing.sound.*;
import processing.core.PApplet;
import processing.core.PImage;
import java.util.ArrayList;
import java.util.Iterator;


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


  // VARIABLES: splashScreen
  Screen splashScreen;
  PImage splashBg;
  String splashBgFile = "images/apcsa.png";
  //SoundFile song;


  // VARIABLES: world1 Pixel-based Screen
  World world1;
  String world1BgFile = "images/grasslands.png";
  PImage world1Bg;
  String enemyFile = "images/enemy.png"; // Using the correct enemy image
  Sprite enemy; // Base sprite for creating enemy copies


  //VARIABLES: world2 World Pixel-based Platformer
  World world2;
  String world2BgFile = "images/sky.png";

  // --- JETPACK AND STARTING PLATFORM VARIABLES ---
  Sprite jetpack;
  String jetpackFile = "images/jetpack.png";
  Platform startPlatform; // A reference to the starting platform for respawning

  // --- NEW: VARIABLES FOR LEVEL 2 ENEMIES ---
  private ArrayList<Sprite> enemiesLevel2 = new ArrayList<>();
  private long lastEnemySpawnTime = 0;
  private final long ENEMY_SPAWN_INTERVAL = 3000; // Spawn an enemy every 3 seconds

  // --- NEW: END POINT & END SCREEN ---
  World winScreen;
  PImage winBg;
  String winBgFile = "images/youwin.png";
  Sprite endFlag;
  String flagFile = "images/flag.png";

  // VARIABLES: Tracking the current Screen being displayed
  Screen currentScreen;


  //------------------ REQUIRED PROCESSING METHODS --------------------//

  // Processing method that runs once for screen resolution settings
  public void settings() {
    size(800,600);
    p = this;
  }

  //Required Processing method that gets run once
  public void setup() {

    p.imageMode(p.CORNER);
    surface.setTitle(titleText);

    //SETUP: Load BG images used in all screens
    splashBg = p.loadImage(splashBgFile);
    world1Bg = p.loadImage(world1BgFile);
    winBg = p.loadImage(winBgFile);

    //SETUP: If non-moving, Resize all BG images to exactly match the screen size
    splashBg.resize(p.width, p.height);
    world1Bg.resize(p.width, p.height);
    winBg.resize(p.width, p.height);

    //SETUP: Screens, Worlds, Grids
    splashScreen = new Screen(this, "splash", splashBgFile); 
    world1 = new World(p, "Level 1", world1BgFile);
    world2 = new World(p, "Level 2", world2BgFile);
    winScreen = new World(p, "You Win!", winBgFile);

    currentScreen = splashScreen;

    //SETUP: Construct Game objects used in All Screens
    jumpMan = new AnimatedSprite(p, "sprites/chick_walk.png", "sprites/chick_walk.json", 0.0f, 0.0f, 0.0f);
    enemy = new Sprite(p, enemyFile, 0.25f); // Used as a template for level 2 enemies

    // ------------------ SETUP WORLD 1 ------------------
    // Player starts on this platform
    startPlatform = new Platform(p, PColor.GREEN, 100, 400, 150, 20);
    world1.addSprite(startPlatform);
    jumpMan.moveTo(startPlatform.getCenterX(), startPlatform.getTop() - jumpMan.getH());

    world1.addSprite(new Platform(p, PColor.BLUE, 300, 300, 200, 20));
    // Red platform for the jetpack
    Platform redPlatform = new Platform(p, PColor.RED, 600, 350, 180, 20);
    world1.addSprite(redPlatform);

    // Setup the jetpack
    jetpack = new Sprite(p, jetpackFile, 0.2f);
    jetpack.moveTo(redPlatform.getCenterX(), redPlatform.getTop() - jetpack.getH());
    world1.addSprite(jetpack);
    world1.addSprite(jumpMan);


    // ------------------ SETUP WORLD 2 ------------------
    world2.addSprite(jumpMan); // Add player to world 2

    // Add more platforms to Level 2
    world2.addSprite(new Platform(p, PColor.YELLOW, 100, 500, 150, 20));
    world2.addSprite(new Platform(p, PColor.CYAN, 350, 400, 150, 20));
    world2.addSprite(new Platform(p, PColor.MAGENTA, 600, 300, 150, 20));
    world2.addSprite(new Platform(p, PColor.get("#FFA500"), 200, 200, 120, 20)); // Orange platform
    world2.addSprite(new Platform(p, PColor.get("#800080"), 450, 150, 120, 20)); // Purple platform

    // Add the end flag to world 2
    endFlag = new Sprite(p, flagFile, 0.3f);
    endFlag.moveTo(700, 100); // Position the flag near the top right
    world2.addSprite(endFlag);


    System.out.println("Game started...");

  } //end setup()


  //Required Processing method that automatically loops
  public void draw() {

    // DRAW LOOP: Update Screen Visuals
    updateTitleBar();
    updateScreen();

    // Only apply physics and game logic if not on splash or win screen
    if (currentScreen != splashScreen && currentScreen != winScreen) {
        handlePlayerPhysics();
        handlePlayerFall();
    }

    // Logic specific to world 1
    if (currentScreen == world1) {
        handleJetpackCollision();
    }

    // Logic specific to world 2
    if (currentScreen == world2) {
        spawnAndMoveEnemies();
        handleEnemyCollisions();
        handleFlagCollision();
    }

  } //end draw()


  //------------------ USER INPUT METHODS --------------------//
  public void keyPressed(){
    System.out.println("\nKey pressed: " + key + " " + p.keyCode);
    if(currentScreen == world1 || currentScreen == world2){
      if(key == 'a' || p.keyCode == LEFT) {
          jumpManVelocityX = -5;
          jumpMan.setAnimationSpeed(0.5f);
      }
      if(key == 'd' || p.keyCode == RIGHT) {
          jumpManVelocityX = 5;
          jumpMan.setAnimationSpeed(0.5f);
      }
      if((key == 'w' || p.keyCode == UP) && jumpManOnGround) {
          jumpManVelocityY = jumpStrength;
          jumpManOnGround = false;
      }
    }
    // Screen switching for debugging
    if(key == '1'){ currentScreen = world1; }
    else if(key == '2'){ currentScreen = world2; }
  }

  public void keyReleased() {
    if ((key == 'a' || p.keyCode == LEFT) && jumpManVelocityX < 0) {
        jumpManVelocityX = 0;
        jumpMan.setAnimationSpeed(0.0f);
    }
    if ((key == 'd' || p.keyCode == RIGHT) && jumpManVelocityX > 0) {
        jumpManVelocityX = 0;
        jumpMan.setAnimationSpeed(0.0f);
    }
  }

  public void mouseClicked(){
    System.out.println("\nMouse was clicked at (" + p.mouseX + "," + p.mouseY + ")");
  }


  //------------------ CUSTOM GAME METHODS --------------------//

  public void updateTitleBar(){
      extraText = currentScreen.getName();
      surface.setTitle(titleText + "    " + extraText + "    Health: " + health);
  }

  public void updateScreen(){
    currentScreen.showBg();
    if(currentScreen == splashScreen && splashScreen.getScreenTime() > 3000){
        currentScreen = world1;
    }
    currentScreen.show(); // This shows all sprites in the current world
  }

  public void handlePlayerPhysics() {
      // Apply horizontal velocity
      jumpMan.move(jumpManVelocityX, 0);

      // Apply gravity
      jumpManVelocityY += gravity;
      jumpMan.move(0, jumpManVelocityY);
      jumpManOnGround = false;

      // Platform collision
      World currentWorld = (World) currentScreen;
      for (Sprite s : currentWorld.getSprites()) {
          if (s instanceof Platform) {
              if (jumpManVelocityY > 0 &&
                  jumpMan.getBottom() >= s.getTop() &&
                  jumpMan.getBottom() <= s.getTop() + jumpManVelocityY + 1 &&
                  jumpMan.getRight() > s.getLeft() &&
                  jumpMan.getLeft() < s.getRight()) {

                  jumpMan.setBottom(s.getTop());
                  jumpManVelocityY = 0;
                  jumpManOnGround = true;
              }
          }
      }
  }

  public void handlePlayerFall() {
      if (jumpMan.getTop() > height) {
          System.out.println("Player fell! Respawning...");
          if (currentScreen == world2) {
              returnToLevel1();
          } else {
             respawnPlayer();
          }
      }
  }

  public void respawnPlayer() {
      jumpMan.moveTo(startPlatform.getCenterX(), startPlatform.getTop() - jumpMan.getH());
      jumpManVelocityY = 0;
      jumpManVelocityX = 0;
  }

  public void handleJetpackCollision() {
      // Check if player touches the jetpack and if it's "solid" (available)
      if (jetpack.isSolid() && jumpMan.isOverlapping(jetpack)) {
          System.out.println("Jetpack acquired! Teleporting to Level 2...");
          currentScreen = world2;
          jumpMan.moveTo(100, 400); // Start position in world 2
          jumpManVelocityX = 0;
          jumpManVelocityY = 0;
          // Make the jetpack "disappear" by moving it off-screen and making it non-solid
          jetpack.moveTo(-200, -200);
          jetpack.setSolid(false);
      }
  }

  public void spawnAndMoveEnemies() {
      // Spawn new enemies periodically
      if (p.millis() - lastEnemySpawnTime > ENEMY_SPAWN_INTERVAL) {
          float spawnY = p.random(50, p.height - 150); // Spawn at a random height
          Sprite newEnemy = enemy.copyTo(p.width + 50, spawnY); // Spawn off-screen to the right
          newEnemy.setSpeedX(-p.random(2, 5)); // Give it a random horizontal speed to the left
          world2.addSprite(newEnemy);
          enemiesLevel2.add(newEnemy);
          lastEnemySpawnTime = p.millis();
      }

      // Move existing enemies and remove them if they go off-screen
      Iterator<Sprite> iterator = enemiesLevel2.iterator();
      while (iterator.hasNext()) {
          Sprite currentEnemy = iterator.next();
          currentEnemy.update(); // Moves the enemy based on its speed
          if (currentEnemy.getRight() < 0) {
              world2.removeSprite(currentEnemy);
              iterator.remove();
          }
      }
  }

  public void handleEnemyCollisions() {
      for (Sprite currentEnemy : enemiesLevel2) {
          if (jumpMan.isOverlapping(currentEnemy)) {
              System.out.println("Player hit an enemy! Returning to Level 1.");
              health--; // Lose health
              if (health <= 0) {
                  endGame();
              } else {
                  returnToLevel1();
              }
              break; // Exit loop after one collision
          }
      }
  }

  public void returnToLevel1() {
        currentScreen = world1;
        respawnPlayer(); // Respawn player at the start of level 1

        // Clear all enemies from world 2
        for (Sprite e : enemiesLevel2) {
            world2.removeSprite(e);
        }
        enemiesLevel2.clear();

        // ** FEATURE: Respawn the jetpack **
        // Find the red platform again to place the jetpack
        for (Sprite s : world1.getSprites()) {
            if (s instanceof Platform && ((Platform)s).getColor() == PColor.RED) {
                jetpack.moveTo(s.getCenterX(), s.getTop() - jetpack.getH());
                jetpack.setSolid(true); // Make it available again
                break;
            }
        }
  }

  public void handleFlagCollision() {
      if (jumpMan.isOverlapping(endFlag)) {
          System.out.println("You reached the flag! You win!");
          currentScreen = winScreen;
      }
  }


  public void endGame(){
      // In a real game, you might go to a "Game Over" screen
      // For now, we'll just print to console and show the win screen
      System.out.println("Game Over!");
      currentScreen = winScreen;
  }


} // end of Game class

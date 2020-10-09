package integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.Gson;
import controllers.PlayGame;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import models.GameBoard;
import models.Player;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class) 
public class GameTest {
  /**
   * Runs only once before the testing starts.
   */
  @BeforeAll
  public static void init() {
    PlayGame.main(new String[0]);
    System.out.println("Before All");
  }
  
  /**
   * This method starts a new game before every test run. It will run every time before a test.
   */
  @BeforeEach
  public void startNewGame() {
    System.out.println("Before Each");
  }
  
  /**
   * This is a test cases to evaluate the newgame endpoint.
   */
  @Test
  @Order(1)
  public void newGameTest() {
    HttpResponse<String> response = Unirest.get("http://localhost:8080/newgame").asString();
    int restStatus = response.getStatus();
    assertEquals(restStatus, 200);
    System.out.println("Test New Game");
  }
  
  /**
   * This is a test case to evaluate the case that Player 1 tries to make a move
   * when no one joins the game.
   */
  @Test
  @Order(2)
  public void moveBeforeStartTest() {
    Unirest.get("http://localhost:8080/newgame").asString();
    HttpResponse<String> response = Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asString();
    
    String responseBody = response.getBody();
    JSONObject jsonObject = new JSONObject(responseBody);
    assertEquals(false, jsonObject.get("moveValidity"));
    assertEquals("Please start the game first.", jsonObject.get("message"));
    
    System.out.println("Test Player 1 cannot make a move if no one has joined the game");
  }
  
  /**
   * This is a test case to evaluate startgame endpoint.
   */
  @Test
  @Order(3)
  public void startGameTest() {
    HttpResponse<String> response = Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    String responseBody = response.getBody();
    System.out.println("Start Game Response: " + responseBody);
    
    JSONObject jsonObject = new JSONObject(responseBody);
    assertEquals(false, jsonObject.get("gameStarted"));
    
    Gson gson = new Gson();
    GameBoard gameBoard = gson.fromJson(jsonObject.toString(), GameBoard.class);
    Player player1 = gameBoard.getP1();
    
    assertEquals('X', player1.getType());
    System.out.println("Test Start Game");
  }
  
  /**
   * This is a test case to evaluate the case that Player 1
   * cannot make a move until both players have joined the game.
   */
  @Test
  @Order(4)
  public void moveBeforePlayer2JoinTest() {
    Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    HttpResponse<String> response = Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asString();
    String responseBody = response.getBody();
    JSONObject jsonObject = new JSONObject(responseBody);
    
    assertEquals(false, jsonObject.get("moveValidity"));
    assertEquals("Please wait for Player 2 to join!", jsonObject.get("message"));
    System.out.println("Test Player 1 cannot make a move before Player 2 joins the game");
  }
  
  /**
   * This is a test case to evaluate if Player 2 can make the first move after game has started.
   */
  @Test
  @Order(5)
  public void firstMovePlayer2Test() {
    Unirest.post("http://localhost:8080/startgame").body("type=O").asString();
    Unirest.get("http://localhost:8080/joingame").asString();

    HttpResponse<String> response = Unirest.post("http://localhost:8080/move/2").body("x=0&y=0").asString();
    String responseBody = response.getBody();
    JSONObject jsonObject = new JSONObject(responseBody);
    assertEquals(false, jsonObject.get("moveValidity"));
    System.out.println("Test Player 2 cannot make the first move");
  }
  
  /**
   * This is a test case to evaluate if Player 1 can make the first move after game has started.
   */
  @Test
  @Order(6)
  public void firstMovePlayer1Test() {
    Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    Unirest.get("http://localhost:8080/joingame").asString();

    HttpResponse<String> response = Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asString();
    String responseBody = response.getBody();
    JSONObject jsonObject = new JSONObject(responseBody);
    assertEquals(true, jsonObject.get("moveValidity"));
    System.out.println("Test Player 1 can make the first move");
  }
  
  /**
   * This is a test case to evaluate if Player 1 can make two moves in its turn.
   */
  @Test
  @Order(7)
  public void player1ConsecutiveMoveTest() {
    Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    Unirest.get("http://localhost:8080/joingame").asString();
    
    HttpResponse<String> response1 = Unirest.post("http://localhost:8080/move/1").body("x=1&y=1").asString();
    String responseBody1 = response1.getBody();
    JSONObject jsonObject1 = new JSONObject(responseBody1);
    assertEquals(true, jsonObject1.get("moveValidity"));
    
    HttpResponse<String> response2 = Unirest.post("http://localhost:8080/move/1").body("x=2&y=2").asString();
    String responseBody2 = response2.getBody();
    JSONObject jsonObject2 = new JSONObject(responseBody2);
    assertEquals(false, jsonObject2.get("moveValidity"));
    
    System.out.println("Test if Player 1 can make 2 moves in its turn");
  }
  
  /**
   * This is a test case to evaluate if Player 2 can make two moves in its turn.
   */
  @Test
  @Order(8)
  public void player2ConsecutiveMoveTest() {
    Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    Unirest.get("http://localhost:8080/joingame").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asString();
    
    HttpResponse<String> response1 = Unirest.post("http://localhost:8080/move/2").body("x=1&y=1").asString();
    String responseBody1 = response1.getBody();
    JSONObject jsonObject1 = new JSONObject(responseBody1);
    assertEquals(true, jsonObject1.get("moveValidity"));
    
    HttpResponse<String> response2 = Unirest.post("http://localhost:8080/move/2").body("x=2&y=2").asString();
    String responseBody2 = response2.getBody();
    JSONObject jsonObject2 = new JSONObject(responseBody2);
    assertEquals(false, jsonObject2.get("moveValidity"));
    
    System.out.println("Test if Player 2 can make 2 moves in its turn");
  }
  
  /**
   * This is test case to evaluate if Player 1 is able to win a game.
   */
  @Test
  @Order(9)
  public void player1WinGameTest() {
    Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    Unirest.get("http://localhost:8080/joingame").asString();
    
    Unirest.post("http://localhost:8080/move/1").body("x=1&y=1").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=0&y=0").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=0&y=1").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=1&y=0").asString();
    HttpResponse<String> response = Unirest.post("http://localhost:8080/move/1").body("x=2&y=1").asString();
    
    String responseBody = response.getBody();
    JSONObject jsonObject = new JSONObject(responseBody);
    assertEquals(true, jsonObject.get("moveValidity"));
    assertEquals("Player 1 wins!", jsonObject.get("message"));
    
    System.out.println("Test if Player 1 can win the game");
  }
  
  /**
   * This is test case to evaluate if Player 1 is able to win a game.
   */
  @Test
  @Order(10)
  public void player2WinGameTest() {
    Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    Unirest.get("http://localhost:8080/joingame").asString();
    
    Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=1&y=1").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=1&y=0").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=0&y=1").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=0&y=2").asString();
    HttpResponse<String> response = Unirest.post("http://localhost:8080/move/2").body("x=2&y=1").asString();
    
    String responseBody = response.getBody();
    JSONObject jsonObject = new JSONObject(responseBody);
    assertEquals(true, jsonObject.get("moveValidity"));
    assertEquals("Player 2 wins!", jsonObject.get("message"));
    
    System.out.println("Test if Player 2 can win the game");
  }
  
  /**
   * This is a test case to evaluate if a game can be a draw
   * when all the positions are exhausted and no one has won.
   */
  @Test
  @Order(11)
  public void drawGameTest() {
    Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    Unirest.get("http://localhost:8080/joingame").asString();
    
    Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=0&y=1").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=1&y=0").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=1&y=2").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=1&y=1").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=2&y=0").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=2&y=1").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=2&y=2").asString();
    HttpResponse<String> response = Unirest.post("http://localhost:8080/move/1").body("x=0&y=2").asString();
    
    String responseBody = response.getBody();
    JSONObject jsonObject = new JSONObject(responseBody);
    assertEquals(true, jsonObject.get("moveValidity"));
    assertEquals("Game over, ended in a draw!", jsonObject.get("message"));
    
    System.out.println("Test if a game can be a draw");
  }
  
  /**
   * This is a test to evaluate if an occupied spot can be reclaimed.
   */
  @Test
  @Order(12)
  public void positionOccupiedTest() {
    Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    Unirest.get("http://localhost:8080/joingame").asString();
    
    Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=1&y=1").asString();
    HttpResponse<String> response = Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asString();
    String responseBody = response.getBody();
    JSONObject jsonObject = new JSONObject(responseBody);
    assertEquals(false, jsonObject.get("moveValidity"));
    assertEquals("This position has been occupied.", jsonObject.get("message"));
    
    System.out.println("Test if a game can be a draw");
  }
  
  /**
   * This is test case to evaluate if user can make a move after
   * the game is ended as a win/lose.
   */
  @Test
  @Order(13)
  public void moveAfterGameWinTest() {
    Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    Unirest.get("http://localhost:8080/joingame").asString();
    
    Unirest.post("http://localhost:8080/move/1").body("x=1&y=1").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=0&y=0").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=0&y=1").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=1&y=0").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=2&y=1").asString();
    HttpResponse<String> response = Unirest.post("http://localhost:8080/move/1").body("x=2&y=2").asString();
    
    String responseBody = response.getBody();
    JSONObject jsonObject = new JSONObject(responseBody);
    assertEquals(false, jsonObject.get("moveValidity"));
    assertEquals("Game was ended. No further move can be made.", jsonObject.get("message"));
    
    System.out.println("Test move after game won");
  }
  
  /**
   * This is a test case to evaluate if user can make a move
   * after a game was ended with as draw.
   */
  @Test
  @Order(11)
  public void moveAfterGameDrawTest() {
    Unirest.post("http://localhost:8080/startgame").body("type=X").asString();
    Unirest.get("http://localhost:8080/joingame").asString();
    
    Unirest.post("http://localhost:8080/move/1").body("x=0&y=0").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=0&y=1").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=1&y=0").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=1&y=2").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=1&y=1").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=2&y=0").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=2&y=1").asString();
    Unirest.post("http://localhost:8080/move/2").body("x=2&y=2").asString();
    Unirest.post("http://localhost:8080/move/1").body("x=0&y=2").asString();
    HttpResponse<String> response = Unirest.post("http://localhost:8080/move/2").body("x=0&y=2").asString();
    
    String responseBody = response.getBody();
    JSONObject jsonObject = new JSONObject(responseBody);
    assertEquals(false, jsonObject.get("moveValidity"));
    assertEquals("Game was ended. No further move can be made.", jsonObject.get("message"));
    
    System.out.println("Test move after game draw");
  }
  
  /**
   * This will run every time after a test has finished.
   */
  @AfterEach
  public void finishGame() {
    System.out.println("After Each");
  }
  
  @AfterAll()
  public static void close() {
    PlayGame.stop();
    System.out.println("After All");
  }
}

package controllers;

import com.google.gson.Gson;
import io.javalin.Javalin;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Queue;
import models.GameBoard;
import models.Message;
import models.Move;
import models.Player;
import org.eclipse.jetty.websocket.api.Session;

public class PlayGame {

  private static final int PORT_NUMBER = 8080;

  private static Javalin app;

  private static GameBoard gameboard;
  
  private static Gson gsonLib;
  
  private static Connection c;

  /**
   * Main method of the application.
   * 
   * @param args Command line arguments
   */
  public static void main(final String[] args) {
    
    createConnection();
    
    gameboard = new GameBoard(c);
    
    gsonLib = new Gson();
    
    app = Javalin.create(config -> {
      config.addStaticFiles("/public");
    }).start(PORT_NUMBER);

    // Test Echo Server
    app.post("/echo", ctx -> {
      ctx.result(ctx.body());
    });

    // When a new game starts, redirect to new game page.
    app.get("/newgame", ctx -> {
      ctx.redirect("tictactoe.html");
    });

    // When player1 choose a type and click Start Game, set p1 and set p1's type.
    app.post("/startgame", ctx -> {
      gameboard.cleanGameBoard(c);
      
      String requestBody = ctx.body();
      String[] tokens = requestBody.split("=");

      Player player1 = new Player(tokens[1].charAt(0), 1);
      
      gameboard.setP1(c, player1);

      String jsonGameBoard = gsonLib.toJson(gameboard);

      ctx.result(jsonGameBoard);
    });

    // When player1 joins the game, set p2 and set p2's type.
    // Then, update view for both players.
    app.get("/joingame", ctx -> {
      Player player2 = new Player(gameboard.getP1().getType() == 'X' ? 'O' : 'X', 2);
      
      gameboard.setP2(c, player2);
      gameboard.setGameStarted(true);
      
      String jsonGameBoard = gsonLib.toJson(gameboard);
      ctx.result(jsonGameBoard);
      sendGameBoardToAllPlayers(jsonGameBoard);
      ctx.redirect("/tictactoe.html?p=2");
    });

    // A player makes a move:
    // check if this move is valid;
    // if valid, update game board status;
    // check whether this move ends the game (win or draw);
    // update view for both players.
    app.post("/move/:playerId", ctx -> {
      
      Message message = new Message();
      message.setMoveValidity(false);
      
      if (gameboard.getWinner() != 0 || gameboard.isDraw()) {
        message.setMessage("Game was ended. No further move can be made.");
        String jsonMessage = gsonLib.toJson(message);
        ctx.result(jsonMessage);
        String jsonGameBoard = gsonLib.toJson(gameboard);
        sendGameBoardToAllPlayers(jsonGameBoard);
        return;
      }

      if (gameboard.getP1() == null) {
        message.setMessage("Please start the game first.");
        String jsonMessage = gsonLib.toJson(message);
        ctx.result(jsonMessage);
        return;
      }
      
      if (!gameboard.isGameStarted()) {
        message.setMessage("Please wait for Player 2 to join!");
        String jsonMessage = gsonLib.toJson(message);
        ctx.result(jsonMessage);
        return;
      }

      String playerId = ctx.pathParam("playerId");
      String requestBody = ctx.body();
      String[] tokens = requestBody.split("&");
      int row = tokens[0].charAt(2) - '0';
      int col = tokens[1].charAt(2) - '0';
      
      Move thisMove = new Move();
      if (playerId.equals("1")) {
        thisMove.setPlayer(gameboard.getP1());
      } else {
        thisMove.setPlayer(gameboard.getP2());
      }
      thisMove.setMoveX(row);
      thisMove.setMoveY(col);

      // Check if the same player wants to make consecutive moves.
      if (!gameboard.isValidTurn(thisMove)) {
        message.setMessage("Please wait for your turn!");
        String jsonMessage = gsonLib.toJson(message);
        ctx.result(jsonMessage);
        return;
      }
      
      // Check whether the position clicked is still available for this move.
      if (!gameboard.spaceAvaliable(thisMove)) {
        message.setMessage("This position has been occupied.");
        String jsonMessage = gsonLib.toJson(message);
        ctx.result(jsonMessage);
        return;
      }
      
      // Set game board status.
      gameboard.setBoardState(c, thisMove);
      message.setMoveValidity(true);
      
      // Check for win and draw.
      if (gameboard.gameWin(thisMove)) {
        gameboard.setWinner(thisMove);
        message.setMessage("Player " + playerId + " wins!");
      } else if (gameboard.gameDraw()) {
        gameboard.setDraw(true);
        message.setMessage("Game over, ended in a draw!");
      } else {
        // If current move does not end the game, set turn and update message object.
        gameboard.setTurn(thisMove);
        message.setCode(100);
        message.setMessage("");
      }
      
      String jsonGameBoard = gsonLib.toJson(gameboard);
      sendGameBoardToAllPlayers(jsonGameBoard);
      String jsonMessage = gsonLib.toJson(message);
      ctx.result(jsonMessage);
    });

    // Web sockets - DO NOT DELETE or CHANGE
    app.ws("/gameboard", new UiWebSocket());
  }
 
  
  /**
   * Create new connection.
   * @return Connection object
   */
  private static void createConnection() {
    try {
      Class.forName("org.sqlite.JDBC");
      c = DriverManager.getConnection("jdbc:sqlite:ase:db");
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
    System.out.println("Opened database successfully");
  }

  /**
   * Send message to all players.
   * 
   * @param gameBoardJson Gameboard JSON
   * @throws IOException Websocket message send IO Exception
   */
  private static void sendGameBoardToAllPlayers(final String gameBoardJson) {
    Queue<Session> sessions = UiWebSocket.getSessions();
    for (Session sessionPlayer : sessions) {
      try {
        sessionPlayer.getRemote().sendString(gameBoardJson);
      } catch (IOException e) {
        System.out.println(e);
      }
    }
  }

  /**
   * Stop the app and close the connection to database.
   */
  public static void stop() {
    app.stop();
    try {
      c.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
  }
}

package unit;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import models.GameBoard;
import models.Move;
import models.Player;
import org.junit.Test;

public class TicTacToeTest {
  
  @Test
  public void testGameWinRowTrue() {
    char[][] state = new char[3][3];
    state[0][0] = 'O';
    state[0][1] = 'O';
    state[1][0] = 'X';
    state[1][2] = 'X';
    state[1][1] = 'X';
    GameBoard gameboard = new GameBoard();
    gameboard.setBoardState(state);
    Move thisMove = new Move();
    Player player = new Player('X', 1);
    thisMove.setPlayer(player);
    thisMove.setMoveX(1);
    thisMove.setMoveY(1);
    boolean rowWin = gameboard.gameWin(thisMove);
    assertEquals(true, rowWin);
  }
  
  @Test
  public void testGameWinColTrue() {
    char[][] state = new char[3][3];
    state[0][0] = 'O';
    state[0][1] = 'X';
    state[0][2] = 'O';
    state[2][1] = 'X';
    state[1][1] = 'X';
    GameBoard gameboard = new GameBoard();
    gameboard.setBoardState(state);
    Move thisMove = new Move();
    Player player = new Player('X', 1);
    thisMove.setPlayer(player);
    thisMove.setMoveX(1);
    thisMove.setMoveY(1);
    boolean colWin = gameboard.gameWin(thisMove);
    assertEquals(true, colWin);
  }
  
  @Test
  public void testGameWinDiagTrue() {
    char[][] state = new char[3][3];
    state[0][0] = 'X';
    state[0][1] = 'O';
    state[0][2] = 'O';
    state[2][2] = 'X';
    state[1][1] = 'X';
    GameBoard gameboard = new GameBoard();
    gameboard.setBoardState(state);
    Move thisMove = new Move();
    thisMove.setPlayer(new Player('X', 1));
    thisMove.setMoveX(1);
    thisMove.setMoveY(1);
    boolean diagWin = gameboard.gameWin(thisMove);
    assertEquals(true, diagWin);
  }
  
  @Test
  public void testGameWinAntiDiagTrue() {
    char[][] state = new char[3][3];
    state[0][2] = 'X';
    state[1][0] = 'O';
    state[1][2] = 'O';
    state[2][0] = 'X';
    state[1][1] = 'X';
    GameBoard gameboard = new GameBoard();
    gameboard.setBoardState(state);
    Move thisMove = new Move();
    Player player = new Player('X', 1);
    thisMove.setPlayer(player);
    thisMove.setMoveX(1);
    thisMove.setMoveY(1);
    boolean antiDiagWin = gameboard.gameWin(thisMove);
    assertEquals(true, antiDiagWin);
  }
  
  @Test
  public void testGameWinFalse() {
    char[][] state = new char[3][3];
    state[0][0] = 'X';
    state[0][1] = 'O';
    state[0][2] = 'X';
    state[1][0] = 'X';
    state[1][1] = 'X';
    state[1][2] = 'O';
    state[2][0] = 'O';
    state[2][1] = 'X';
    state[2][2] = 'O';
    GameBoard gameboard = new GameBoard();
    gameboard.setBoardState(state);
    Move thisMove = new Move();
    Player player = new Player('X', 1);
    thisMove.setPlayer(player);
    thisMove.setMoveX(2);
    thisMove.setMoveY(2);
    boolean win = gameboard.gameWin(thisMove);
    assertEquals(false, win);
  }
  
  @Test
  public void testGameDrawTrue() {
    char[][] state = new char[3][3];
    state[0][0] = 'X';
    state[0][1] = 'O';
    state[0][2] = 'X';
    state[1][0] = 'X';
    state[1][1] = 'X';
    state[1][2] = 'O';
    state[2][0] = 'O';
    state[2][1] = 'X';
    state[2][2] = 'O';
    GameBoard gameboard = new GameBoard();
    gameboard.setBoardState(state);
    boolean isDraw = gameboard.gameDraw();
    assertEquals(true, isDraw);
  }
  
  @Test
  public void testGameDrawFalse() {
    char[][] state = new char[3][3];
    state[0][0] = 'X';
    state[0][1] = 'O';
    state[0][2] = 'X';
    state[1][0] = 'X';
    state[1][1] = 'X';
    state[1][2] = 'O';
    state[2][0] = 'O';
    GameBoard gameboard = new GameBoard();
    gameboard.setBoardState(state);
    boolean isDraw = gameboard.isDraw();
    assertEquals(false, isDraw);
  }
  
  @Test
  public void testSpaceAvaliableTrue() {
    GameBoard gameboard = new GameBoard();
    char[][] state = new char[3][3];
    gameboard.setBoardState(state);
    Move thisMove = new Move();
    thisMove.setPlayer(new Player('X', 1));
    thisMove.setMoveX(0);
    thisMove.setMoveY(0);
    boolean isAva = gameboard.spaceAvaliable(thisMove);
    assertEquals(true, isAva);
  }
  
  @Test
  public void testSpaceAvaliableXFalse() {
    GameBoard gameboard = new GameBoard();
    char[][] state = new char[3][3];
    state[0][0] = 'X';
    gameboard.setBoardState(state);
    Move thisMove = new Move();
    thisMove.setPlayer(new Player('O', 2));
    thisMove.setMoveX(0);
    thisMove.setMoveY(0);
    boolean isAva = gameboard.spaceAvaliable(thisMove);
    assertEquals(false, isAva);
  }
  
  @Test
  public void testSpaceAvaliableOFalse() {
    GameBoard gameboard = new GameBoard();
    char[][] state = new char[3][3];
    state[0][0] = 'O';
    gameboard.setBoardState(state);
    Move thisMove = new Move();
    thisMove.setPlayer(new Player('O', 2));
    thisMove.setMoveX(0);
    thisMove.setMoveY(0);
    boolean isAva = gameboard.spaceAvaliable(thisMove);
    assertEquals(false, isAva);
  }
  
  @Test
  public void testCreateTable() {
    GameBoard gameboard = new GameBoard();
    try {
      Class.forName("org.sqlite.JDBC");
      Connection c = DriverManager.getConnection("jdbc:sqlite:ase:db");
      boolean created = gameboard.createTable(c);
      assertEquals(true, created);
      c.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
  }
  
  @Test
  public void testCleanGameBoard() {
    GameBoard gameboard = new GameBoard();
    try {
      Class.forName("org.sqlite.JDBC");
      Connection c = DriverManager.getConnection("jdbc:sqlite:ase:db");
      boolean cleaned = gameboard.cleanGameBoard(c);
      assertEquals(true, cleaned);
      c.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
  }
  
  @Test
  public void testAddMoveData() {
    GameBoard gameboard = new GameBoard();
    try {
      Class.forName("org.sqlite.JDBC");
      Connection c = DriverManager.getConnection("jdbc:sqlite:ase:db");
      Move thisMove = new Move(new Player('X', 1), 0, 0);
      boolean added = gameboard.addMoveData(c, thisMove);
      assertEquals(true, added);
      gameboard.cleanGameBoard(c);
      c.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
  }
  
  @Test
  public void testRebootStartGame() {
    GameBoard gameboard = new GameBoard();
    try {
      Class.forName("org.sqlite.JDBC");
      Connection c = DriverManager.getConnection("jdbc:sqlite:ase:db");
      Player p1 = new Player('X', 1);
      gameboard.setP1(c, p1);
      c.close();
      c = DriverManager.getConnection("jdbc:sqlite:ase:db");
      boolean reboot = gameboard.reboot(c);
      assertEquals(true, reboot);
      gameboard.cleanGameBoard(c);
      c.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
  }
  
  @Test
  public void testRebootJoinGame() {
    GameBoard gameboard = new GameBoard();
    try {
      Class.forName("org.sqlite.JDBC");
      Connection c = DriverManager.getConnection("jdbc:sqlite:ase:db");
      Player p1 = new Player('O', 1);
      gameboard.setP1(c, p1);
      Player p2 = new Player('X', 2);
      gameboard.setP2(c, p2);
      c.close();
      c = DriverManager.getConnection("jdbc:sqlite:ase:db");
      boolean reboot = gameboard.reboot(c);
      assertEquals(true, reboot);
      gameboard.cleanGameBoard(c);
      c.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
  }
  
  @Test
  public void testRebbotMakeMove1() {
    GameBoard gameboard = new GameBoard();
    try {
      Class.forName("org.sqlite.JDBC");
      Connection c = DriverManager.getConnection("jdbc:sqlite:ase:db");
      Player p1 = new Player('X', 1);
      gameboard.setP1(c, p1);
      Player p2 = new Player('O', 2);
      gameboard.setP2(c, p2);
      Move thisMove = new Move(p1, 0, 0);
      gameboard.setBoardState(c, thisMove);
      c.close();
      c = DriverManager.getConnection("jdbc:sqlite:ase:db");
      boolean reboot = gameboard.reboot(c);
      assertEquals(true, reboot);
      gameboard.cleanGameBoard(c);
      c.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
  }
  
  @Test
  public void testRebbotMakeMove2() {
    GameBoard gameboard = new GameBoard();
    try {
      Class.forName("org.sqlite.JDBC");
      Connection c = DriverManager.getConnection("jdbc:sqlite:ase:db");
      Player p1 = new Player('O', 1);
      gameboard.setP1(c, p1);
      Player p2 = new Player('X', 2);
      gameboard.setP2(c, p2);
      Move thisMove = new Move(p1, 0, 0);
      gameboard.setBoardState(c, thisMove);
      thisMove = new Move(p2, 1, 1);
      gameboard.setBoardState(c, thisMove);
      c.close();
      c = DriverManager.getConnection("jdbc:sqlite:ase:db");
      boolean reboot = gameboard.reboot(c);
      assertEquals(true, reboot);
      gameboard.cleanGameBoard(c);
      c.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
  }
  
  @Test
  public void testRebootWin() {
    GameBoard gameboard = new GameBoard();
    try {
      Class.forName("org.sqlite.JDBC");
      Connection c = DriverManager.getConnection("jdbc:sqlite:ase:db");
      Player p1 = new Player('X', 1);
      gameboard.setP1(c, p1);
      Player p2 = new Player('O', 2);
      gameboard.setP2(c, p2);
      Move thisMove = new Move(p1, 0, 0);
      gameboard.setBoardState(c, thisMove);
      thisMove = new Move(p2, 1, 1);
      gameboard.setBoardState(c, thisMove);
      thisMove = new Move(p1, 1, 0);
      gameboard.setBoardState(c, thisMove);
      thisMove = new Move(p2, 2, 2);
      gameboard.setBoardState(c, thisMove);
      thisMove = new Move(p1, 2, 0);
      gameboard.setBoardState(c, thisMove);
      c.close();
      c = DriverManager.getConnection("jdbc:sqlite:ase:db");
      boolean reboot = gameboard.reboot(c);
      assertEquals(true, reboot);
      assertEquals(1, gameboard.getWinner());
      gameboard.cleanGameBoard(c);
      c.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
  }
  
  @Test
  public void testRebootDraw() {
    GameBoard gameboard = new GameBoard();
    try {
      Class.forName("org.sqlite.JDBC");
      Connection c = DriverManager.getConnection("jdbc:sqlite:ase:db");
      Player p1 = new Player('X', 1);
      gameboard.setP1(c, p1);
      Player p2 = new Player('O', 2);
      gameboard.setP2(c, p2);
      Move thisMove = new Move(p1, 0, 0);
      gameboard.setBoardState(c, thisMove);
      thisMove = new Move(p2, 0, 1);
      gameboard.setBoardState(c, thisMove);
      thisMove = new Move(p1, 0, 2);
      gameboard.setBoardState(c, thisMove);
      thisMove = new Move(p2, 1, 2);
      gameboard.setBoardState(c, thisMove);
      thisMove = new Move(p1, 1, 0);
      gameboard.setBoardState(c, thisMove);
      thisMove = new Move(p2, 2, 0);
      gameboard.setBoardState(c, thisMove);
      thisMove = new Move(p1, 1, 1);
      gameboard.setBoardState(c, thisMove);
      thisMove = new Move(p2, 2, 2);
      gameboard.setBoardState(c, thisMove);
      thisMove = new Move(p1, 2, 1);
      gameboard.setBoardState(c, thisMove);
      c.close();
      c = DriverManager.getConnection("jdbc:sqlite:ase:db");
      boolean reboot = gameboard.reboot(c);
      assertEquals(true, reboot);
      assertEquals(true, gameboard.isDraw());
      gameboard.cleanGameBoard(c);
      c.close();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
  }
}

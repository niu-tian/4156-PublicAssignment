package models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class GameBoard {

  private Player p1;

  private Player p2;

  private boolean gameStarted;

  private int turn;

  private char[][] boardState;

  private int winner;

  private boolean isDraw;
  
  /**
   * Construct a new game board.
   */
  public GameBoard() {
    gameStarted = false;
    turn = 1;
    boardState = new char[3][3];
    winner = 0;
    isDraw = false;
  }
  
  /**
   * Construct a new game board with connection to a database.
   */
  public GameBoard(Connection c) {
    this();
    createTable(c);
    reboot(c);
  }
  
  /**
   * Reboot gameboard to status before crash.
   * @param con connection to database
   * @return
   */
  public boolean reboot(Connection con) {
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = con.createStatement();
      rs = stmt.executeQuery("SELECT * FROM ASE_I3_MOVE;");
      int size = 0;
      Move thisMove = null;
      while (rs.next()) {
        int id = rs.getInt("PLAYER_ID");
        int x = rs.getInt("MOVE_X");
        int y = rs.getInt("MOVE_Y");
        if (x == -1 && id == 1) {
          char type = y == 1 ? 'X' : 'O';
          Player player = new Player(type, id);
          addP1ToGameBoard(player);
        }
        if (x == -1 && id == 2) {
          char type = y == 1 ? 'X' : 'O';
          Player player = new Player(type, id);
          addP2ToGameBoard(player);
        } 
        if (x != -1) {
          thisMove = new Move(id == 1 ? p1 : p2, x, y);
          addMoveToBoard(thisMove);
        }
        size++;
      } 
      
      if (size >= 2) {
        gameStarted = true;
        if (size > 2) {
          turn = thisMove.getPlayer().getId() == 1 ? 2 : 1;
          if (gameWin(thisMove)) {
            setWinner(thisMove);
          } else if (gameDraw()) {
            setDraw(true);
          }
        } 
      }
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      return false;
    } finally {
      try {
        rs.close();
      } catch (Exception e) {
        System.err.println(e.getClass().getName() + ": " + e.getMessage());
      }
      try {
        stmt.close();
      } catch (Exception e) {
        System.err.println(e.getClass().getName() + ": " + e.getMessage());
      }
    }
    return true;
  }

  /**
   * Return true if the game is draw, return false otherwise.
   * @return the isDraw
   */
  public boolean isDraw() {
    return isDraw;
  }
    
  /**
   * Set isDraw to true once the game achieves a draw.
   * @param isDraw the isDraw to set
   */
  public void setDraw(boolean isDraw) {
    this.isDraw = isDraw;
  }
    
  /**
   * Return the winner of the game.
   * @return the winner
   */
  public int getWinner() {
    return winner;
  }
    
  /**
   * Set the winner once either player wins the game.
   * @param thisMove a move object with player who made the move, the row and column number.
   */
  public void setWinner(Move thisMove) {
    int playerId = thisMove.getPlayer().getId();
    this.winner = playerId;
  }
    
  /**
    * Set player1 when player1 joins the game.
    * @return the p1
    */
  public Player getP1() {
    return p1;
  }

  /**
   * Get the first player of the game.
   * @param p1 the p1 to set
   */
  public void setP1(Connection c, Player p1) {
    addP1ToDatabase(c, p1);
    addP1ToGameBoard(p1);
  }
  
  public void addP1ToGameBoard(Player p1) {
    this.p1 = p1;
  }
  
  public void addP1ToDatabase(Connection c, Player p1) {
    int type = p1.getType() == 'X' ? 1 : 2;
    addMoveData(c, new Move(p1, -1, type));
  }

  /**
   * Get the second player of the game.
   * @return the p2
   */
  public Player getP2() {
    return p2;
  }
  
  public void addP2ToGameBoard(Player p2) {
    this.p2 = p2;
  }
  
  public void addP2ToDatabase(Connection c, Player p2) {
    int type = p2.getType() == 'X' ? 1 : 2;
    addMoveData(c, new Move(p2, -1, type));
  }

  /**
   * This method sets the state of the game board.
   * @param boardState the boardState to set
   */
  public void setBoardState(char[][] boardState) {
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        this.boardState[i][j] = boardState[i][j];
      }
    }
  }
  
  /**
   * Set the game board state when a player makes a valid move.
   * @param thisMove a move object with player who made the move, the row and column number.
   */
  public void setBoardState(Connection c, Move thisMove) {
    addMoveToBoard(thisMove);
    addMoveData(c, thisMove);
  }
  
  /**
   * Add this move to game board.
   * @param thisMove current move
   */
  public void addMoveToBoard(Move thisMove) {
    int row = thisMove.getMoveX();
    int col = thisMove.getMoveY();
    
    int playerId = thisMove.getPlayer().getId();
    
    char type = playerId == 1 ? p1.getType() : p2.getType();
    this.boardState[row][col] = type;
  }

  /**
   * Set player2 when player2 joins the game.
   * @param p2 the p2 to set
   */
  public void setP2(Connection c, Player p2) {
    addP2ToGameBoard(p2);
    addP2ToDatabase(c, p2);
  }
  
  /**
   * Switch turn to the player who did not make the last move.
   * @param thisMove a move object with player who made the move, the row and column number.
   */
  public void setTurn(Move thisMove) {
    int playerId = thisMove.getPlayer().getId();
    this.turn = playerId == 1 ? 2 : 1;
  }
  
  /**
   * Return whether the game has started or not.
   * @return the gameStarted
   */
  public boolean isGameStarted() {
    return gameStarted;
  }

  /**
   * Set gameStarted to true when game starts and set it to false when game ends.
   * @param gameStarted the gameStarted to set
   */
  public void setGameStarted(boolean gameStarted) {
    this.gameStarted = gameStarted;
  }

  /**
   * After the player makes a move, check whether the player wins the game.
   * @param thisMove a move object with player who made the move, the row and column number.
   * @return true if the space has not been occupied, otherwise false
   */
  public boolean gameWin(Move thisMove) {
    int row = thisMove.getMoveX();
    int col = thisMove.getMoveY();
    char type = thisMove.getPlayer().getType();
    // check row
    boolean rowWin = true;
    for (int i = 0; i < boardState.length; i++) {
      if (boardState[row][i] != type) {
        rowWin = false;
        break;
      }
    }
    if (rowWin) {
      return true;
    }
    // check column
    boolean colWin = true;
    for (int i = 0; i < boardState.length; i++) {
      if (boardState[i][col] != type) {
        colWin = false;
        break;
      }
    }
    if (colWin) {
      return true;
    }
    // if this coordinate belongs to diagonal, check diagonal
    if (row == col) {
      boolean diagWin = true;
      for (int i = 0; i < boardState.length; i++) {
        if (boardState[i][i] != type) {
          diagWin = false;
          break;
        }
      }
      if (diagWin) {
        return true;
      }
    }
    // if this coordinate belongs to anti-diagonal, check anti-diagonal
    if (row + col == boardState.length - 1) {
      boolean antiDiagWin = true;
      for (int i = 0; i < boardState.length; i++) {
        if (boardState[i][boardState.length - 1 - i] != type) {
          antiDiagWin = false;
          break;
        }
      }
      if (antiDiagWin) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Check whether all spaces are occupied.
   * @return true if the no space is left, otherwise false.
   */
  public boolean gameDraw() {
    for (int i = 0; i < boardState.length; i++) {
      for (int j = 0; j < boardState[0].length; j++) {
        if (boardState[i][j] != 'X' && boardState[i][j] != 'O') {
          return false;
        }
      }
    }
    return true;
  }
  
  /**
   * After the player makes a move, check whether the space clicked is available.
   * @param thisMove a move object with player who made the move, the row and column number.
   * @return true if the space has not been occupied, otherwise false.
   */
  public boolean spaceAvaliable(Move thisMove) {
    int row = thisMove.getMoveX();
    int col = thisMove.getMoveY();
    if (boardState[row][col] == 'X' || boardState[row][col] == 'O') {
      return false;
    }
    return true;
  }
  
  /**
   * This methods check whether current move is done by the right player.
   * @param thisMove a move object with player who made the move, the row and column number.
   * @return true if it is current player's turn, false otherwise.
   */
  public boolean isValidTurn(Move thisMove) {
    if (this.turn == thisMove.getPlayer().getId()) {
      return true;
    }
    return false;
  }
  
  /**
   * Create a new table.
   * @return Boolean true if table created, and false if an error occurs.
   */
  public boolean createTable(Connection con) {
    Statement stmt = null;
    
    try {
      con.setAutoCommit(false);
      stmt = con.createStatement();
      String sql = "CREATE TABLE IF NOT EXISTS ASE_I3_MOVE "
          + "(PLAYER_ID INT NOT NULL, "
          + " MOVE_X INT NOT NULL, "
          + " MOVE_Y INT NOT NULL)";
      stmt.executeUpdate(sql);
      con.commit();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      return false;
    } finally {
      try {
        stmt.close();
      } catch (Exception e) {
        System.err.println(e.getClass().getName() + ": " + e.getMessage());
      }
    }
    System.out.println("Table created successfully");
    return true;
  }
  
  /**
   * Adds move date to the database table.
   * @param move Move object containing data
   * @return Boolean true if data added successfully, and false if any error occurs.
   */
  public boolean addMoveData(Connection con, Move move) {
    Statement stmt = null;
    
    try {
      con.setAutoCommit(false);
      System.out.println("Opened database successfully");
      stmt = con.createStatement();
      String sql = "INSERT INTO ASE_I3_MOVE VALUES (" + move.getPlayer().getId()
                    + ", " + move.getMoveX()
                    + ", " + move.getMoveY() + ");";
      stmt.executeUpdate(sql);
      con.commit();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      return false;
    } finally {
      try {
        stmt.close();
      } catch (Exception e) {
        System.err.println(e.getClass().getName() + ": " + e.getMessage());
      }
    }
    System.out.println("Record created successfully");
    return true;
  }
  
  /**
   * Clean gameboard and database when a new game starts.
   * @param c connectiont to database
   */
  public boolean cleanGameBoard(Connection c) {
    gameStarted = false;
    turn = 1;
    boardState = new char[3][3];
    winner = 0;
    isDraw = false;
    return cleanDatabase(c);
  }
  
  /**
   * Clean the database if a new game starts.
   */
  private boolean cleanDatabase(Connection con) {
    Statement stmt = null;
    try {
      con.setAutoCommit(false);
      stmt = con.createStatement();
      String sql = "DELETE from ASE_I3_MOVE;";
      stmt.executeUpdate(sql);
      con.commit();
    } catch (Exception e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
    } finally {
      try {
        stmt.close();
      } catch (Exception e) {
        System.err.println(e.getClass().getName() + ": " + e.getMessage());
      }
    }
    System.out.println("Clean table done successfully");
    return true;
  }
}

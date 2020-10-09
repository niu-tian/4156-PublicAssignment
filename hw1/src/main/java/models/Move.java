package models;

public class Move {

  private Player player;

  private int moveX;

  private int moveY;
  
  public Move() {
    
  }
  
  /**
   * Construct a Move object with given parameters.
   * @param player a player object who made this move
   * @param x the x-coordinate of this move
   * @param y the y-coordinate of this move
   */
  public Move(Player player, int x, int y) {
    this.player = player;
    this.moveX = x;
    this.moveY = y;
  }

  /**
   * Returns the player of this Move.
   * @return the player
   */
  public Player getPlayer() {
    return player;
  }

  /**
   * Set the player of this Move.
   * @param player the player to set
   */
  public void setPlayer(Player player) {
    this.player = player;
  }

  /**
   * Return the x-coordinate of this Move.
   * @return the moveX
   */
  public int getMoveX() {
    return moveX;
  }

  /**
   * Set the x-coordinate of this Move.
   * @param moveX the moveX to set
   */
  public void setMoveX(int moveX) {
    this.moveX = moveX;
  }

  /**
   * Return the y-coordinate of this Move.
   * @return the moveY
   */
  public int getMoveY() {
    return moveY;
  }

  /**
   * Set the y-coordinate of this Move.
   * @param moveY the moveY to set
   */
  public void setMoveY(int moveY) {
    this.moveY = moveY;
  }

}
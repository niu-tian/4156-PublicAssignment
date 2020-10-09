package models;

public class Player {

  private char type;

  private int id;
  
  public Player() {
    
  }
  
  /**
   * A constructor to construct a player with given parameters.
   * @param type player's chosen type
   * @param id player's id
   */
  public Player(char type, int id) {
    this.type = type;
    this.id = id;
  }

  /**
   * Return type of this player.
   * @return the type
   */
  public char getType() {
    return type;
  }

  /**
   * Set type of this player.
   * @param type the type to set
   */
  public void setType(char type) {
    this.type = type;
  }

  /**
   * Return id of this player.
   * @return the id
   */
  public int getId() {
    return id;
  }

  /**
   * Set id of this player.
   * @param id the id to set
   */
  public void setId(int id) {
    this.id = id;
  }

}

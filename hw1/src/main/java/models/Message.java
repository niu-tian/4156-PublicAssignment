package models;

public class Message {

  private boolean moveValidity;

  private int code;

  private String message;

  public Message() {
    this.moveValidity = true;
  }

  /**
   * Return the moveValidity:
   * false if previous move is not valid or the game is ended.
   * @return the moveValidity
   */
  public boolean isMoveValidity() {
    return moveValidity;
  }

  /**
   * This method sets moveValidity.
   * @param moveValidity the moveValidity to set
   */
  public void setMoveValidity(boolean moveValidity) {
    this.moveValidity = moveValidity;
  }

  /**
   * Return the code.
   * @return the code
   */
  public int getCode() {
    return code;
  }

  /**
   * This method sets the code.
   * @param code the code to set
   */
  public void setCode(int code) {
    this.code = code;
  }

  /**
   * Return the message content.
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /**
   * This method sets the message content.
   * @param message the message to set
   */
  public void setMessage(String message) {
    this.message = message;
  }

}

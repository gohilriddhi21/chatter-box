import java.util.Random;

/**
 * The {@code InsultGenerator} class generates random insults from a predefined list.
 * These insults can be used for various purposes, such as sending playful messages.
 * The insults are randomly selected from a list of predefined phrases.
 */
public class InsultGenerator {

  /**
   * Predefined array of insults.
   */
  private static final String[] insults = {
      "You're as bright as a black hole.",
      "You have a face only a mother could love.",
      "You're not the sharpest tool in the shed.",
      "If you were any slower, you'd be going backward.",
      "You're a walking disaster.",
      "I've met smarter sandwiches.",
      "You're so lazy, you probably haven't even read this insult yet."
  };

  /**
   * Returns a string representation of the {@code InsultGenerator} object.
   * This method is automatically generated by the IDE.
   *
   * @return a string representation of the object.
   */
  @Override
  public String toString() {
    return "InsultGenerator{}";
  }

  /**
   * Returns the hash code value for the {@code InsultGenerator} object.
   * This method is automatically generated by the IDE.
   *
   * @return a hash code value for this object.
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   * This method is automatically generated by the IDE.
   *
   * @param obj the reference object with which to compare.
   * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }

  /**
   * Generates a random insult from the predefined list.
   *
   * @return a randomly selected insult.
   */
  public static String generateInsult() {
    Random random = new Random();
    int index = random.nextInt(insults.length);
    return insults[index];
  }
}

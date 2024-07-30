import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class InsultGeneratorTest {

  @Test
  void generateInsult() {
    String insult = InsultGenerator.generateInsult();
    assertNotNull(insult);
    assertTrue(insult.length() > 0);
    System.out.println("Generated Insult: " + insult);
  }

  @Test
  void toStringMethod() {
    InsultGenerator insultGenerator = new InsultGenerator();
    assertEquals("InsultGenerator{}", insultGenerator.toString());
  }

  @Test
  void hashCodeMethod() {
    InsultGenerator insultGenerator1 = new InsultGenerator();
    InsultGenerator insultGenerator2 = new InsultGenerator();
    assertNotEquals(insultGenerator1.hashCode(), insultGenerator2.hashCode());
  }

  @Test
  void equalsMethod() {
    InsultGenerator insultGenerator1 = new InsultGenerator();
    InsultGenerator insultGenerator2 = new InsultGenerator();
    assertNotEquals(insultGenerator1, insultGenerator2);

  }

  @Test
  void equalsMethod2() {
    InsultGenerator insultGenerator1 = new InsultGenerator();
    InsultGenerator insultGenerator2 = new InsultGenerator();

    assertTrue(!insultGenerator1.equals("Not an InsultGenerator"));

  }

  @Test
  void equalsMethod3() {
    InsultGenerator insultGenerator1 = new InsultGenerator();
    InsultGenerator insultGenerator2 = new InsultGenerator();
    assertFalse(insultGenerator1.equals(null));
  }

}
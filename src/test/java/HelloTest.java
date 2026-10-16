
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloTest {
    @Test
    void shouldReturnHello() {
        Hello hello = new Hello();
        assertEquals("Hello", hello.greet());
    }
}
  
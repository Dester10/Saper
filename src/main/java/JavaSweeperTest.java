import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import sweeper.*;

public class JavaSweeperTest {

    private JavaSweeper sweeper;

    @BeforeEach
    public void setUp() {
        sweeper = new JavaSweeper(9, 9, 9, "Player1"); // Создаём новый объект перед каждым текстом
    }

    @Test
    public void testInitialization() {
        assertNotNull(sweeper); // Проверяем, что объект sweeper инциализирован
    }

    @Test
    public void testTimerStarts() {
        sweeper.startTimer(); // Проверяем запуск таймера
        assertTrue(sweeper.getTimer().isRunning()); // Проверяем, что таймер запущен
    }

}

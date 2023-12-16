import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import sweeper.PostgreSQL;

public class PostgreSQLTest {

    private PostgreSQL db;

    @BeforeEach
    public void setUp() {
        db = new PostgreSQL();
    }

    @Test
    public void testPlayerCreation() {
        String playerName = "TestPlayer1";
        int playerTime = 50;
        db.createPlayer(playerName, playerTime);

        assertTrue(db.searchPlayer(playerName)); // Проверяем, что игрок создан
    }

    @Test
    public void testPlayerUpdate() {
        String playerName = "TestPlayer2";
        int initialTime = 100;
        int updatedTime = 80;
        db.createPlayer(playerName, initialTime);
        db.updatePlayer(playerName, updatedTime);

        List<String> topPlayers = db.getTopPlayers();
        assertTrue(topPlayers.contains("Player: " + playerName + " - Time: " + updatedTime + " s.")); // Проверяем обновление времени игрока
    }

    @Test
    public void testTopPlayersRetrieval() {
        List<String> topPlayers = db.getTopPlayers();
        assertNotNull(topPlayers); // Проверяем, что список не пустой
        assertFalse(topPlayers.isEmpty()); // Проверяем, что список пуст изначально
    }

    @Test
    public void testAllPlayersDeletion() {
        String playerName = "TestPlayer3";
        int playerTime = 60;
        db.createPlayer(playerName, playerTime);
        db.deleteAllPlayers();

        assertFalse(db.searchPlayer(playerName)); // Проверяем, что все игроки были удалены
    }
}

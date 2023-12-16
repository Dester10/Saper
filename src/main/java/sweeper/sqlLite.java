package sweeper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class sqlLite {
    private Connection conn;
    private ClassLoader classLoader;
    private String databasePath;

    public sqlLite() {
        classLoader = getClass().getClassLoader();
        databasePath = Objects.requireNonNull(classLoader.getResource("database/players.db")).getPath();
        conn = null;
        connect();
        System.out.println(databasePath);
    }

    private void connect() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException ex) {
            System.out.println("Ошибка при работе с базой данных: " + ex.getMessage());
        }
    }

    public void createTable() {
        try {
            Statement statement = conn.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXIST Players " +
                    "p_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "P_name TEXT NOT NULL" +
                    "p_time INTEGER");
            System.out.println("Table Players was created or already exists!");
            statement.close();
        } catch (SQLException ex) {
            System.out.println("Error in create table: " + ex.getMessage());
        }
    }

    public void createPlayer(String name, int time) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO Players (p_name, p_time) VALUES (?, ?)");
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, time);
            preparedStatement.executeUpdate();
            System.out.println("Player was created in DB!.");
        } catch (SQLException ex) {
            System.out.println("Error in create player: " + ex.getMessage());
        }
    }

    public void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
                System.out.println("Connection to SQLite has been closed.");
            }
        } catch (SQLException ex) {
            System.out.println("Ошибка при закрытии соединения: " + ex.getMessage());
        }
    }

    public List<String> getTopPlayers() {
        List<String> topPlayersList = new ArrayList<>();

        try {
            PreparedStatement query = conn.prepareStatement("SELECT p_name, p_time FROM Players ORDER BY p_time ASC LIMIT 3");
            ResultSet result = query.executeQuery();

            while (result.next()) {
                String playerName = result.getString("p_name");
                int playerTime = result.getInt("p_time");

                String playerInfo = "Player: " + playerName + "- Time: " + playerTime + " s.";
                topPlayersList.add(playerInfo);
            }
            result.close();
        } catch (SQLException ex) {
            System.out.println("Error in retrieving top players: " + ex.getMessage());
        }

        return topPlayersList;
    }

}

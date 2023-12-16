package sweeper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgreSQL {
    private Connection conn;

    public PostgreSQL() {
        conn = null;
        connect();
    }

    private void connect() {
        String url = "jdbc:postgresql://dpg-clpoeg946foc73dcrnug-a.frankfurt-postgres.render.com/JavaSweeper";;
        String user = "javasweeper_user";
        String password = "Hqw3Rcmlxv6l4cWumWeEf5ehEBYBboNN";

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver не найден. Пожалуйста, добавьте драйвер в classpath.");
            e.printStackTrace();
            return;
        }

        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connection to PostgreSQL has been established.");
        } catch (SQLException ex) {
            System.out.println("Error connecting to PostgreSQL database: " + ex.getMessage());
        }
    }
    public void createPlayer(String name, int time) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO Players (p_name, p_time) VALUES (?, ?)");
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, time);
            preparedStatement.executeUpdate();
            System.out.println("Player was created in DB!");
            preparedStatement.close();
        } catch (SQLException ex) {
            System.out.println("Error in create player: " + ex.getMessage());
        }
    }

    public void updatePlayer(String p_name, int time) {
        try {
            PreparedStatement checkStatement = conn.prepareStatement("SELECT p_time FROM Players WHERE p_name = ?");
            checkStatement.setString(1, p_name);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                int currentTime = resultSet.getInt("p_time");
                if (time < currentTime) {
                    PreparedStatement updateStatement = conn.prepareStatement("UPDATE Players SET p_time = ? WHERE p_name = ?");
                    updateStatement.setInt(1, time);
                    updateStatement.setString(2, p_name);
                    updateStatement.executeUpdate();

                    System.out.println("Player time updated successfully.");
                    updateStatement.close();
                } else {
                    System.out.println("New time is not less than the current time for the player.");
                }
            }
            resultSet.close();
            checkStatement.close();
        } catch (SQLException ex) {
            System.out.println("Error updating player time: " + ex.getMessage());
        }
    }
    public void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
                System.out.println("Connection to PostgreSQL has been closed.");
            }
        } catch (SQLException ex) {
            System.out.println("Ошибка при закрытии соединения: " + ex.getMessage());
        }
    }
    public boolean searchPlayer(String p_name) {
        boolean isPlayer = false;

        try {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT EXISTS(SELECT 1 FROM Players WHERE p_name = ?)");
            preparedStatement.setString(1, p_name);
            ResultSet result = preparedStatement.executeQuery();

            if (result.next()) {
                isPlayer = result.getBoolean(1);
            }
            result.close();
            preparedStatement.close();
        } catch (SQLException ex) {
            System.out.println("Error checking player existence: " + ex.getMessage());
        }

        return isPlayer;
    }

    public void deleteAllPlayers() {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM Players");
            preparedStatement.executeUpdate();

            System.out.println("All players have been deleted");
            preparedStatement.close();
        } catch (SQLException ex) {
            System.out.println("Error deleting players: " + ex.getMessage());
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

                String playerInfo = "Player: " + playerName + " - Time: " + playerTime + " s. ";
                topPlayersList.add(playerInfo);
            }
            result.close();
        } catch (SQLException ex) {
            System.out.println("Error in retrieving top players: " + ex.getMessage());
        }

        return topPlayersList;
    }
}

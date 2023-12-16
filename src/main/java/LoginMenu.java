import sweeper.PostgreSQL;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.util.List;

public class LoginMenu extends JFrame {

    private PostgreSQL Connection;
    private JTextField bombsField;
    private JTextField rowsField;
    private JTextField colsField;
    private JTextField nameField;
    private JTextArea topPlayersArea;

    public LoginMenu() {
        setTitle("Sweeper Login");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(7, 4));
        setResizable(false);

        JLabel bombsLabel = new JLabel("Number of Bombs:");
        JLabel rowsLabel = new JLabel("Number of Rows:");
        JLabel colsLabel = new JLabel("Number of Columns:");
        JLabel nameLabel = new JLabel("Name User:");
        JLabel topPlayersLabel = new JLabel("Top Players:");

        bombsField = new JTextField();
        rowsField = new JTextField();
        colsField = new JTextField();
        nameField = new JTextField();
        topPlayersArea = new JTextArea("", 3, 12);
        topPlayersArea.setEditable(false);
        JButton startButton = new JButton("Start");
        JButton exitButton = new JButton("Exit");
        JButton deleteButton = new JButton("DELETE");
        JButton randomParameters = new JButton("Random parameters");

        Connection = new PostgreSQL();
        //Connection.createTable();

        add(topPlayersLabel);
        add(topPlayersArea);
        displayTopPlayers();

        add(nameLabel);
        add(nameField);
        add(bombsLabel);
        add(bombsField);
        add(rowsLabel);
        add(rowsField);
        add(colsLabel);
        add(colsField);
        add(startButton);
        add(deleteButton);
        add(randomParameters);
        add(exitButton);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String bombsText = bombsField.getText();
                String rowsText = rowsField.getText();
                String colsText = colsField.getText();

                if (name.isEmpty() || bombsText.isEmpty() || rowsText.isEmpty() || colsText.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginMenu.this, "Please fill in all the fields.");
                    return;
                }

                try {
                    int bombs = Integer.parseInt(bombsText);
                    int rows = Integer.parseInt(rowsText);
                    int cols = Integer.parseInt(colsText);

                    if (bombs <= 0 || rows < 8 || rows > 19 || cols < 8 || cols > 19) {
                        JOptionPane.showMessageDialog(LoginMenu.this, "Please provide valid inputs:\n" +
                                "- Number of bombs should be greater than zero.\n" +
                                "- Rows and columns should be between 8 and 19.");
                        return;
                    }

                    JavaSweeper sweeper = new JavaSweeper(cols, rows, bombs, name);
                    sweeper.setVisible(true);
                    dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(LoginMenu.this, "Please enter valid numeric values.");
                }
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Connection.closeConnection();
                System.exit(0);
                dispose(); // Закрыть меню после начала игры
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Connection.deleteAllPlayers();
                displayTopPlayers();
                System.out.println("Deleted All Players from DB");
            }
        });

        randomParameters.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fillFieldsWithRandomValues();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                Connection.closeConnection();
                dispose();
            }
        });

    }

    private void fillFieldsWithRandomValues() {

        int randomRows = (int) (Math.random() * 12) + 8; //Генерация случайного количества строк от 8 до 19
        int randomCols = (int) (Math.random() * 12) + 8; //Генерация случайного количества столбцов от 8 до 19
        int randomBombs = (int) (Math.random() * randomRows) + 1; //Генерация случайного количества бомб от 1 до 50

        bombsField.setText(String.valueOf(randomBombs));
        rowsField.setText(String.valueOf(randomRows));
        colsField.setText(String.valueOf(randomCols));

        // Заполнение имени пользователя случайным именем или згачением
        String[] randomNames = {"Bob", "Ivan", "John", "Arnold", "Alex", "Jimmy", "Maxim", "Michael", "Nikita", "Arseny", "Daniel"};
        String randomName = randomNames[(int) (Math.random() * randomNames.length)];
        nameField.setText(randomName);
    }

    private void displayTopPlayers() {
        List<String> topPlayers = Connection.getTopPlayers();
        StringBuilder topPlayersInfo = new StringBuilder();

        for (int i =0; i < topPlayers.size(); i++) {
            String playerInfo = topPlayers.get(i);
            topPlayersInfo.append(i + 1).append(". ").append(playerInfo).append("\n");
        }

        topPlayersArea.setText(topPlayersInfo.toString());
    }
}

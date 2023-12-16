import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import sweeper.*;
import sweeper.Box;
import sweeper.PostgreSQL;

public class JavaSweeper extends JFrame {
    private static LoginMenu loginMenu;
    private Game game;

    private JPanel panel;
    private JPanel bottomPanel;

    private int COLS = 9;
    private int ROWS = 9;
    private int BOMBS = 9;
    private String NAME = "";
    private final int IMAGE_SIZE = 50;

    private Timer timer;

    private int time;

    private PostgreSQL Connection;

    public static void main(String[] args) {
        loginMenu = new LoginMenu();
        loginMenu.setVisible(true);
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    // Геттер для таймера
    public Timer getTimer() {
        return timer;
    }

    // Геттер для времени
    public int getTime() {
        return time;
    }

    // Сеттер для времени
    public void setTime(int time) {
        this.time = time;
    }

    public JavaSweeper(int cols, int rows, int bombs, String name) {
        COLS = cols;
        ROWS = rows;
        BOMBS = bombs;
        NAME = name;
        game = new Game (COLS, ROWS, BOMBS, NAME);
        game.start();
        setImages();
        initBottomPanel();
        initPanel();
        initFrame();
        Connection = new PostgreSQL();
        panel.repaint();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Connection.closeConnection();
                dispose();
            }
        });
    }

    private void initBottomPanel() {
        bottomPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome, " + NAME);
        JLabel timeLabel = new JLabel("Time: 0"); // Здесь будет отображаться время

        bottomPanel.add(welcomeLabel, BorderLayout.WEST);
        bottomPanel.add(timeLabel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
        startTimer();
    }

     void startTimer() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setTimer(time++); // Увеличиваем время каждую секунду
            }
        });
        time = 0;
        timer.start();
    }

    private void initPanel() {
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                for (Coord coord : Ranges.getAllCoords()) {
                    g.drawImage((Image) game.getBox(coord).image, coord.x * IMAGE_SIZE, coord.y * IMAGE_SIZE, this);
                }
            }
        };

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX() / IMAGE_SIZE;
                int y = e.getY() / IMAGE_SIZE;
                Coord coord = new Coord(x,y);
                if (e.getButton() == MouseEvent.BUTTON1) {
                    game.pressLeftButton(coord);
                }
                if (e.getButton() == MouseEvent.BUTTON3) {
                    game.pressRightButton(coord);
                }
                if (e.getButton() == MouseEvent.BUTTON2) {
                    startTimer();
                    game.start();
                }
                panel.repaint();

                if(game.getState() == GameState.BOMBED) {
                    timer.stop();
                    showGameOverDialog();
                }

                if(game.getState() == GameState.WINNER) {
                    timer.stop();
                    showGameWinDialog();
                    Connection.createPlayer(NAME, time);
                }
            }
        });
        panel.setPreferredSize(new Dimension(Ranges.getSize().x * IMAGE_SIZE, Ranges.getSize().y * IMAGE_SIZE));
        add(panel);
    }

    private void showGameOverDialog() {
        JFrame gameOverFrame = new JFrame("Game Over");
        JPanel panel = new JPanel();
        JLabel label = new JLabel("You lost! Do you want to play again?");
        JButton restartButton = new JButton("Restart");
        JButton exitButton = new JButton("Menu");

        restartButton.addActionListener(e -> {
            JavaSweeper.this.game = new Game(COLS, ROWS, BOMBS, NAME); // Создаём новый лбъект Game
            JavaSweeper.this.game.start();
            label.setText("Welcome!");
            remove(panel); // Удаляем старую панель
            initPanel(); // Инциализируем новую панель
            startTimer();
            gameOverFrame.dispose();
        });

        exitButton.addActionListener(e -> {
            loginMenu = new LoginMenu();
            loginMenu.setVisible(true);
            dispose();
            gameOverFrame.dispose();
        });

        panel.add(label);
        panel.add(restartButton);
        panel.add(exitButton);

        gameOverFrame.add(panel);
        gameOverFrame.setSize(300, 150);
        gameOverFrame.setLocationRelativeTo(null);
        gameOverFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gameOverFrame.setVisible(true);
    }

    private void showGameWinDialog() {
        JFrame gameWinFrame = new JFrame("WINNER");
        JPanel panel = new JPanel();
        JLabel label = new JLabel("You WIN! Do you want to play again?");
        JButton playAgainButton = new JButton("Play again");
        JButton exitButton = new JButton("Menu");

        playAgainButton.addActionListener(e -> {
            JavaSweeper.this.game = new Game(COLS, ROWS, BOMBS, NAME);
            JavaSweeper.this.game.start();
            label.setText("Welcome!");
            remove(panel); // Удоляем старую панель
            initPanel(); // Инциализируем новую панель
            startTimer();
            gameWinFrame.dispose();
        });

        exitButton.addActionListener(e -> {
            loginMenu = new LoginMenu();
            loginMenu.setVisible(true);
            dispose();
            gameWinFrame.dispose();
        });

        panel.add(label);
        panel.add(playAgainButton);
        panel.add(exitButton);

        gameWinFrame.add(label);
        panel.add(playAgainButton);
        panel.add(exitButton);

        gameWinFrame.add(panel);
        gameWinFrame.setSize(300, 150);
        gameWinFrame.setLocationRelativeTo(null);
        gameWinFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gameWinFrame.setVisible(true);
    }

//    private String getMessage() {
//        switch (game.getState()) {
//            case PLAYED: return "Think twice!";
//            case BOMBED:
//                showGameOverDialog();
//                return "You lose! BIG BA-DA-BOOM!";
//            case WINNER:
//                return "CONGRATULATIONS!";
//            default: return "Welcome!";
//        }
//    }

    private void initFrame() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Java Sweeper");
        setResizable(false);
        setLocationRelativeTo(null);
        setIconImage(getImage("icon"));

        setLayout(new BorderLayout());

        add(panel, BorderLayout.CENTER); // Игровое поле занимет центральное место
        add(bottomPanel, BorderLayout.SOUTH); // Нижняя панель всегда внизу

        pack();
        setVisible(true);
        setLocationRelativeTo(null);
    }

    private void setImages() {
        for (Box box : Box.values()) {
            box.image = getImage(box.name().toLowerCase());
        }
    }

    private Image getImage(String name) {
        String filename = "img/" + name + ".png";
        ImageIcon icon = new ImageIcon(getClass().getResource(filename));
        return icon.getImage();
    }

    private void setTimer(int time) {
        JLabel timeLabel = (JLabel) bottomPanel.getComponent(1); // Получаем компонент, отображающий время
        timeLabel.setText("Time: " + time); // Обновляем время
    }
}
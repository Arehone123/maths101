import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MathApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login());
    }
}

class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public Login() {
        setTitle("Math App Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(this::login);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(this::register);

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(registerButton);

        add(panel);
        setVisible(true);
    }

    private void login(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (Utility.verifyUser(username, password)) {
            JOptionPane.showMessageDialog(this, "Login successful!");
            new MainApp(username);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.");
        }
    }

    private void register(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (Utility.addUser(new User(username, password))) {
            JOptionPane.showMessageDialog(this, "Registration successful!");
        } else {
            JOptionPane.showMessageDialog(this, "Username already exists.");
        }
    }
}

class MainApp extends JFrame {
    private String username;

    public MainApp(String username) {
        this.username = username;
        setTitle("Math App Main");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        JButton viewStudentsButton = new JButton("View Total Students");
        viewStudentsButton.addActionListener(this::viewStudents);

        JButton takeTestButton = new JButton("Take a Test");
        takeTestButton.addActionListener(this::takeTest);

        JButton viewProgressButton = new JButton("View Progress");
        viewProgressButton.addActionListener(this::viewProgress);

        panel.add(viewStudentsButton);
        panel.add(takeTestButton);
        panel.add(viewProgressButton);

        add(panel);
        setVisible(true);
    }

    private void viewStudents(ActionEvent e) {
        int totalStudents = Utility.getTotalStudents();
        JOptionPane.showMessageDialog(this, "Total number of students: " + totalStudents);
    }

    private void takeTest(ActionEvent e) {
        new TestApp(username);
    }

    private void viewProgress(ActionEvent e) {
        int progress = Utility.getProgress(username);
        JOptionPane.showMessageDialog(this, "Your progress: " + progress + "%");
    }
}

class TestApp extends JFrame {
    private String username;
    private int score;
    private int questionCount;
    private JLabel questionLabel;
    private JTextField answerField;

    public TestApp(String username) {
        this.username = username;
        this.score = 0;
        this.questionCount = 0;

        setTitle("Math Test");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        questionLabel = new JLabel();
        answerField = new JTextField();

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(this::submitAnswer);

        panel.add(questionLabel);
        panel.add(answerField);
        panel.add(submitButton);

        add(panel);
        setVisible(true);

        generateQuestion();
    }

    private void submitAnswer(ActionEvent e) {
        int answer = Integer.parseInt(answerField.getText());
        if (checkAnswer(answer)) {
            score++;
        }
        questionCount++;
        if (questionCount < 5) {
            generateQuestion();
        } else {
            Utility.saveProgress(username, score * 20); // Save progress as percentage
            JOptionPane.showMessageDialog(this, "Test finished! Your score: " + score + "/5");
            dispose();
        }
    }

    private void generateQuestion() {
        Random rand = new Random();
        int num1 = rand.nextInt(10) + 1;
        int num2 = rand.nextInt(10) + 1;
        questionLabel.setText("What is " + num1 + " + " + num2 + "?");
        answerField.setText("");
    }

    private boolean checkAnswer(int answer) {
        String question = questionLabel.getText();
        String[] parts = question.split(" ");
        int num1 = Integer.parseInt(parts[2]);
        int num2 = Integer.parseInt(parts[4]);
        return (num1 + num2) == answer;
    }
}

class Utility {
    private static final String FILE_NAME = "students.json";

    public static boolean verifyUser(String username, String password) {
        List<User> users = readUsers();
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public static boolean addUser(User user) {
        List<User> users = readUsers();
        for (User u : users) {
            if (u.getUsername().equals(user.getUsername())) {
                return false; // User already exists
            }
        }
        users.add(user);
        writeUsers(users);
        return true;
    }

    public static List<User> readUsers() {
        List<User> users = new ArrayList<>();
        JSONParser parser = new JSONParser();

        try (FileReader reader = new FileReader(FILE_NAME)) {
            JSONArray userArray = (JSONArray) parser.parse(reader);

            for (Object obj : userArray) {
                JSONObject userObject = (JSONObject) obj;
                String username = (String) userObject.get("username");
                String password = (String) userObject.get("password");
                int progress = ((Long) userObject.get("progress")).intValue();
                users.add(new User(username, password, progress));
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return users;
    }

    public static void writeUsers(List<User> users) {
        JSONArray userArray = new JSONArray();

        for (User user : users) {
            JSONObject userObject = new JSONObject();
            userObject.put("username", user.getUsername());
            userObject.put("password", user.getPassword());
            userObject.put("progress", user.getProgress());
            userArray.add(userObject);
        }

        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            writer.write(userArray.toJSONString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getTotalStudents() {
        return readUsers().size();
    }

    public static int getProgress(String username) {
        List<User> users = readUsers();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user.getProgress();
            }
        }
        return 0;
    }

    public static void saveProgress(String username, int progress) {
        List<User> users = readUsers();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                user.setProgress(progress);
            }
        }
        writeUsers(users);
    }
}

class User {
    private String username;
    private String password;
    private int progress;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.progress = 0;
    }

    public User(String username, String password, int progress) {
        this.username = username;
        this.password = password;
        this.progress = progress;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

// Main class to run the application
public class MathApp {
    public static void main(String[] args) {
        new Login();
    }
}

// Login class
class Login extends JFrame {
    private JButton studentButton, teacherButton;

    public Login() {
        setTitle("Login");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        studentButton = new JButton("Student");
        teacherButton = new JButton("Teacher");

        studentButton.addActionListener(e -> {
            new Student();
            dispose();
        });

        teacherButton.addActionListener(e -> {
            new Teacher();
            dispose();
        });

        setLayout(new GridLayout(2, 1));
        add(studentButton);
        add(teacherButton);

        setVisible(true);
    }
}

// Student class
class Student extends JFrame {
    private JTextField answerField;
    private JButton submitButton;
    private JLabel questionLabel, feedbackLabel;
    private int currentQuestion = 0;
    private int correctAnswers = 0;
    private String[] questions = {
            "5 + 3 = ?",
            "12 - 4 = ?",
            "6 * 2 = ?"
    };
    private int[] answers = {8, 8, 12};

    public Student() {
        setTitle("Student");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        questionLabel = new JLabel(questions[currentQuestion]);
        answerField = new JTextField();
        submitButton = new JButton("Submit");
        feedbackLabel = new JLabel();

        submitButton.addActionListener(e -> {
            int answer = Integer.parseInt(answerField.getText());
            if (answer == answers[currentQuestion]) {
                feedbackLabel.setText("Correct!");
                correctAnswers++;
            } else {
                feedbackLabel.setText("Incorrect! The correct answer is " + answers[currentQuestion]);
            }

            currentQuestion++;
            if (currentQuestion < questions.length) {
                questionLabel.setText(questions[currentQuestion]);
                answerField.setText("");
            } else {
                feedbackLabel.setText("Quiz finished! Correct answers: " + correctAnswers);
                submitButton.setEnabled(false);
            }
        });

        setLayout(new GridLayout(4, 1));
        add(questionLabel);
        add(answerField);
        add(submitButton);
        add(feedbackLabel);

        setVisible(true);
    }
}

// Teacher class
class Teacher extends JFrame {
    private JTextArea classListTextArea;
    private Map<String, Integer> studentProgress;

    public Teacher() {
        setTitle("Teacher");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        studentProgress = new HashMap<>();
        studentProgress.put("Student A", 2);
        studentProgress.put("Student B", 3);
        studentProgress.put("Student C", 1);

        classListTextArea = new JTextArea();
        for (String student : studentProgress.keySet()) {
            classListTextArea.append(student + ": " + studentProgress.get(student) + " correct answers\n");
        }

        add(new JScrollPane(classListTextArea));

        setVisible(true);
    }
}

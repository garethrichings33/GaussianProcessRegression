import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Scanner;

public class GPR implements ActionListener {
    public static void main(String[] args){
        new GPR();
    }

    private final JFrame frame;
    private final JButton trainingFileButton;
    private final JButton predictionInputFileButton;
    private final JButton predictionOutputFileButton;
    private final JButton fitButton;
    private final JButton predictionsButton;
    private final JButton howToButton;
    private final JLabel label;
    private final JLabel message;

    private final String trainingFileButtonLabel;
    private final String inputDataFileButtonLabel;
    private final String predictionOutputFileButtonLabel;
    private final String fitButtonLabel;
    private final String predictionsButtonLabel;
    private final String howToButtonLabel;
    private String trainingFileName = null;
    private String inputDataFileName = null;
    private String predictionsFileName = null;
    private String helpText;
    private boolean helpNotRead = true;

    private final GPRModelHandler gprModelHandler;

    public GPR() {
        frame = new JFrame("Gaussian Process Regression");
        frame.setSize(400,400);

        FrameWithActionListener fileFrame = new FrameWithActionListener();

        trainingFileButtonLabel = "Open training data file";
        trainingFileButton = new JButton(trainingFileButtonLabel);
        trainingFileButton.addActionListener(fileFrame);
        trainingFileButton.setBounds(30, 20, 200, 30);

        inputDataFileButtonLabel = "Open input data file";
        predictionInputFileButton = new JButton(inputDataFileButtonLabel);
        predictionInputFileButton.addActionListener(fileFrame);
        predictionInputFileButton.setBounds(30, 60, 200, 30);

        predictionOutputFileButtonLabel = "Save prediction data file";
        predictionOutputFileButton = new JButton(predictionOutputFileButtonLabel);
        predictionOutputFileButton.addActionListener(fileFrame);
        predictionOutputFileButton.setBounds(30, 100, 200, 30);

        fitButtonLabel = "Generate GPR Model";
        fitButton = new JButton(fitButtonLabel);
        fitButton.addActionListener(this);
        fitButton.setBounds(30, 140, 200, 30);

        predictionsButtonLabel = "Generate predictions";
        predictionsButton = new JButton(predictionsButtonLabel);
        predictionsButton.addActionListener(this);
        predictionsButton.setBounds(30, 180, 200, 30);

        howToButtonLabel = "How to use";
        howToButton = new JButton(howToButtonLabel);
        howToButton.addActionListener(this);
        howToButton.setBounds(290,330,100,30);

        label = new JLabel();
        label.setBounds(20, 220, 360, 30);

        message = new JLabel();
        message.setBounds(20, 260, 360, 30);

        frame.add(trainingFileButton);
        frame.add(predictionInputFileButton);
        frame.add(predictionOutputFileButton);
        frame.add(label);
        frame.add(message);
        frame.add(fitButton);
        frame.add(predictionsButton);
        frame.add(howToButton);

        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        gprModelHandler = new GPRModelHandler();
    }

    private void displayhelp(){
        if(helpNotRead) {
            getHelpText();
            helpNotRead = false;
        }

        JFrame helpFrame = new JFrame("How to use");
        helpFrame.setSize(400,400);

        JTextArea help = new JTextArea(helpText);
        int textWidth = frame.getWidth() - 50;
        int textHeight = frame.getHeight() - 50;
        help.setPreferredSize(new Dimension(textWidth, textHeight));
        help.setLineWrap(true);
        help.setFont(new Font("Arial", Font.PLAIN, 16));
        help.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(help);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        helpFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        helpFrame.getContentPane().setLayout(new FlowLayout());
        helpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        helpFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if(command.equals(fitButtonLabel)){
            if(!gprModelHandler.gprCalculatorIsNull())
                message.setText(gprModelHandler.createModel());
            else
                message.setText("Add training set before creating model.");
        }
        else if (command.equals(predictionsButtonLabel)) {
            if(gprModelHandler.isModelCreated())
                message.setText(gprModelHandler.getPredictions());
            else
                message.setText("Create model before getting predictions.");
        }
        else if (command.equals(howToButtonLabel)) {
            displayhelp();
        }
    }

    private class FrameWithActionListener extends JFrame implements ActionListener{

        public FrameWithActionListener() throws HeadlessException {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView());
            int dialog = 0;
            message.setText("");

            if(command.equals(trainingFileButtonLabel)){
                dialog = fileChooser.showOpenDialog(null);
                if(dialog == JFileChooser.APPROVE_OPTION) {
                    trainingFileName = fileChooser.getSelectedFile().getAbsolutePath();
                    label.setText("Training file: " + fileChooser.getSelectedFile().getName());
                    gprModelHandler.initialiseGPR(trainingFileName);
                }
                else
                    label.setText("Open training file cancelled.");
            }
            else if(command.equals(inputDataFileButtonLabel)) {
                if (!gprModelHandler.isModelCreated())
                    message.setText("Create model before adding data.");
                else {
                    dialog = fileChooser.showOpenDialog(null);
                    if (dialog == JFileChooser.APPROVE_OPTION) {
                        inputDataFileName = fileChooser.getSelectedFile().getAbsolutePath();
                        label.setText("Data file: " + fileChooser.getSelectedFile().getName());
                        message.setText(gprModelHandler.addData(inputDataFileName));
                    } else
                        label.setText("Open input data file cancelled.");
                }
            }
            else if(command.equals(predictionOutputFileButtonLabel)){
                if(!gprModelHandler.isPredictionsGenerated()) {
                    message.setText("Calculate predictions before saving.");
                }
                else{
                    dialog = fileChooser.showSaveDialog(null);
                    if(dialog == JFileChooser.APPROVE_OPTION) {
                        predictionsFileName = fileChooser.getSelectedFile().getAbsolutePath();
                        label.setText("Predictions file: " + fileChooser.getSelectedFile().getName());
                        message.setText(gprModelHandler.savePredictions(predictionsFileName));
                    }
                    else
                        label.setText("Save predictions file cancelled");
                }
            }
        }
    }

    private void getHelpText(){
        StringBuilder builder = new StringBuilder();
        try {
            Scanner scanner = new Scanner(new File("src/helptext.txt"));
            while(scanner.hasNextLine()){
                builder.append(scanner.nextLine());
            }
            scanner.close();
            helpText = builder.toString();
        }
        catch (FileNotFoundException excp){
            helpText = "Help file not found.";
            message.setText(helpText);
        }
    }
}

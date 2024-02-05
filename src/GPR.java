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
    private final JButton inputDataFileButton;
    private final JButton predictionOutputFileButton;
    private final JButton fitButton;
    private final JButton predictionsButton;
    private final JButton chooseAlphaButton;
    private final JButton chooseGammaButton;
    private final JButton howToButton;
    private final JLabel label;
    private final JLabel message;

    private final String trainingFileButtonLabel;
    private final String inputDataFileButtonLabel;
    private final String predictionOutputFileButtonLabel;
    private final String fitButtonLabel;
    private final String predictionsButtonLabel;
    private final String chooseAlphaButtonLabel;
    private final String chooseGammaButtonLabel;
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
        inputDataFileButton = new JButton(inputDataFileButtonLabel);
        inputDataFileButton.addActionListener(fileFrame);
        inputDataFileButton.setBounds(30, 60, 200, 30);
        inputDataFileButton.setEnabled(false);

        predictionOutputFileButtonLabel = "Save prediction data file";
        predictionOutputFileButton = new JButton(predictionOutputFileButtonLabel);
        predictionOutputFileButton.addActionListener(fileFrame);
        predictionOutputFileButton.setBounds(30, 100, 200, 30);
        predictionOutputFileButton.setEnabled(false);

        fitButtonLabel = "Generate GPR Model";
        fitButton = new JButton(fitButtonLabel);
        fitButton.addActionListener(this);
        fitButton.setBounds(30, 140, 200, 30);
        fitButton.setEnabled(false);

        predictionsButtonLabel = "Generate predictions";
        predictionsButton = new JButton(predictionsButtonLabel);
        predictionsButton.addActionListener(this);
        predictionsButton.setBounds(30, 180, 200, 30);
        predictionsButton.setEnabled(false);

        chooseAlphaButtonLabel = "Choose width parameter (alpha)";
        chooseAlphaButton= new JButton(chooseAlphaButtonLabel);
        chooseAlphaButton.addActionListener(this);
        chooseAlphaButton.setBounds(30, 220, 250, 30);
        chooseAlphaButton.setEnabled(false);

        chooseGammaButtonLabel = "Choose uncertainty parameter (gamma^2)";
        chooseGammaButton= new JButton(chooseGammaButtonLabel);
        chooseGammaButton.addActionListener(this);
        chooseGammaButton.setBounds(30, 260, 300, 30);
        chooseGammaButton.setEnabled(false);

        howToButtonLabel = "How to use";
        howToButton = new JButton(howToButtonLabel);
        howToButton.addActionListener(this);
        howToButton.setBounds(290,330,100,30);

        label = new JLabel();
        label.setBounds(20, 300, 360, 30);

        message = new JLabel();
        message.setBounds(20, 340, 360, 30);

        frame.add(trainingFileButton);
        frame.add(inputDataFileButton);
        frame.add(predictionOutputFileButton);
        frame.add(label);
        frame.add(message);
        frame.add(fitButton);
        frame.add(predictionsButton);
        frame.add(chooseAlphaButton);
        frame.add(chooseGammaButton);
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
        helpFrame.setSize(600,600);

        JTextArea help = new JTextArea(helpText);
        int textWidth = helpFrame.getWidth() - 50;
        int textHeight = helpFrame.getHeight() - 50;
        System.out.println(textHeight);
        help.setPreferredSize(new Dimension(textWidth, textHeight));
        help.setLineWrap(true);
        help.setFont(new Font("Arial", Font.PLAIN, 14));
        help.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(help);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        helpFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        helpFrame.getContentPane().setLayout(new FlowLayout());
        helpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        helpFrame.setVisible(true);
    }

    private void chooseAlpha(){
        JFrame alphaFrame = new JFrame("Choose alpha");
        final int frameHeight = 80;
        final int frameWidth = 400;
        alphaFrame.setSize(frameWidth, frameHeight);

        JLabel label = new JLabel("Input alpha value: ");
        label.setBounds(10, 10, 120, 20);

        JLabel error = new JLabel();
        error.setBounds(10,40, 200, 20);

        JTextField alphaField = new JTextField();
        alphaField.setBounds(130, 10, 200, 20);
        alphaField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double alpha = Double.parseDouble(alphaField.getText());
                    if(alpha <= 0.0)
                        throw new IllegalArgumentException("Alpha must be positive");
                    gprModelHandler.setModelAlpha(alpha);
                    message.setText("Alpha set to: " + gprModelHandler.getModelAlpha());
                    error.setText("");
                    alphaFrame.setSize(frameWidth,frameHeight);
                }
                catch (NumberFormatException excp){
                    alphaFrame.setSize(frameWidth, 100);
                    error.setText("Invalid number format.");
                }
                catch (IllegalArgumentException excp){
                    alphaFrame.setSize(frameWidth, 100);
                    error.setText(excp.getMessage());
                }
            }
        });

        alphaFrame.add(label);
        alphaFrame.add(alphaField);
        alphaFrame.add(error);
        alphaFrame.setLayout(null);
        alphaFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        alphaFrame.setVisible(true);
    }

    private void chooseGamma(){
        JFrame gammaFrame = new JFrame("Choose gamma^2");
        final int frameHeight = 80;
        final int frameWidth = 400;
        gammaFrame.setSize(frameWidth, frameHeight);

        JLabel label = new JLabel("Input gamma^2 value: ");
        label.setBounds(10, 10, 120, 20);

        JLabel error = new JLabel();
        error.setBounds(10,40, 300, 20);

        JTextField gammaField = new JTextField();
        gammaField.setBounds(130, 10, 200, 20);
        gammaField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double gamma = Double.parseDouble(gammaField.getText());
                    if(gamma < 0.0)
                        throw new IllegalArgumentException("Gamma^2 must be non-negative.");

                    gprModelHandler.setModelGammaSquared(gamma);
                    message.setText("Gamma^2 set to: " + gprModelHandler.getModelGammaSquared());
                    error.setText("");
                    gammaFrame.setSize(frameWidth,frameHeight);
                }
                catch (NumberFormatException excp){
                    gammaFrame.setSize(frameWidth, 100);
                    error.setText("Invalid number format.");
                }
                catch (IllegalArgumentException excp)                    {
                    gammaFrame.setSize(frameWidth, 100);
                    error.setText(excp.getMessage());
                }
            }
        });

        gammaFrame.add(label);
        gammaFrame.add(gammaField);
        gammaFrame.add(error);
        gammaFrame.setLayout(null);
        gammaFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gammaFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if(command.equals(fitButtonLabel)){
            if(!gprModelHandler.gprCalculatorIsNull()) {
                message.setText(gprModelHandler.createModel());
                inputDataFileButton.setEnabled(true);
            }
            else
                message.setText("Add training set before creating model.");
        }
        else if (command.equals(predictionsButtonLabel)) {
            if(gprModelHandler.isModelCreated()) {
                message.setText(gprModelHandler.getPredictions());
                predictionOutputFileButton.setEnabled(true);
            }
            else
                message.setText("Create model before getting predictions.");
        }
        else if (command.equals(chooseAlphaButtonLabel)) {
            chooseAlpha();
            predictionOutputFileButton.setEnabled(false);
        }
        else if (command.equals(chooseGammaButtonLabel)) {
            chooseGamma();
            predictionOutputFileButton.setEnabled(false);
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
                    fitButton.setEnabled(true);
                    chooseAlphaButton.setEnabled(true);
                    chooseGammaButton.setEnabled(true);
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
                        predictionsButton.setEnabled(true);
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

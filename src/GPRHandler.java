import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.SimpleAttributeSet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.DecimalFormat;
import java.util.Scanner;

public class GPRHandler implements ActionListener {
    private final JFrame frame;
    private final JButton trainingFileButton;
    private final JButton inputDataFileButton;
    private final JButton predictionOutputFileButton;
    private final JButton fitButton;
    private final JButton predictionsButton;
    private final JButton chooseAlphaButton;
    private final JButton chooseGammaButton;
    private final JButton howToButton;
    private final JLabel fileMessage;
    private final JLabel message;
    private final JLabel modelInfo;
    private final JLabel alphaReport;
    private final JLabel gammaReport;
    private final JLabel logMarginalLikelihoodReport;
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
    private final GPRModel gprModel;
    private final JFileChooser fileChooser;
    private final int labelHeight = 20;
    private final int buttonHeight = 30;
    private final int fieldHeight = 20;

    public GPRHandler() {
        gprModel = new GPRModel();

        frame = new JFrame("Gaussian Process Regression");
        frame.setSize(400,500);

        FrameWithActionListener fileFrame = new FrameWithActionListener();

        trainingFileButtonLabel = "Open training data file";
        trainingFileButton = new JButton(trainingFileButtonLabel);
        trainingFileButton.addActionListener(fileFrame);
        trainingFileButton.setBounds(30, 20, 200, buttonHeight);

        inputDataFileButtonLabel = "Open input data file";
        inputDataFileButton = new JButton(inputDataFileButtonLabel);
        inputDataFileButton.addActionListener(fileFrame);
        inputDataFileButton.setBounds(30, 60, 200, buttonHeight);
        inputDataFileButton.setEnabled(false);

        predictionOutputFileButtonLabel = "Save prediction data file";
        predictionOutputFileButton = new JButton(predictionOutputFileButtonLabel);
        predictionOutputFileButton.addActionListener(fileFrame);
        predictionOutputFileButton.setBounds(30, 100, 200, buttonHeight);
        predictionOutputFileButton.setEnabled(false);

        fitButtonLabel = "Generate GPRHandler Model";
        fitButton = new JButton(fitButtonLabel);
        fitButton.addActionListener(this);
        fitButton.setBounds(30, 140, 200, buttonHeight);
        fitButton.setEnabled(false);

        predictionsButtonLabel = "Calculate predictions";
        predictionsButton = new JButton(predictionsButtonLabel);
        predictionsButton.addActionListener(this);
        predictionsButton.setBounds(30, 180, 200, buttonHeight);
        predictionsButton.setEnabled(false);

        chooseAlphaButtonLabel = "Choose width parameter (alpha)";
        chooseAlphaButton= new JButton(chooseAlphaButtonLabel);
        chooseAlphaButton.addActionListener(this);
        chooseAlphaButton.setBounds(30, 220, 250, buttonHeight);
        chooseAlphaButton.setEnabled(false);

        chooseGammaButtonLabel = "Choose uncertainty parameter (gamma^2)";
        chooseGammaButton= new JButton(chooseGammaButtonLabel);
        chooseGammaButton.addActionListener(this);
        chooseGammaButton.setBounds(30, 260, 300, buttonHeight);
        chooseGammaButton.setEnabled(false);

        howToButtonLabel = "How to use";
        howToButton = new JButton(howToButtonLabel);
        howToButton.addActionListener(this);
        howToButton.setBounds(290,frame.getHeight()-70,100,buttonHeight);

        modelInfo = new JLabel("Model information: ");
        modelInfo.setBounds(20, 310, 150, labelHeight);

        alphaReport = new JLabel();
        alphaReport.setBounds(30, 330, 200, labelHeight);

        gammaReport = new JLabel();
        gammaReport.setBounds(30, 350, 200, labelHeight);

        logMarginalLikelihoodReport = new JLabel();
        logMarginalLikelihoodReport.setBounds(30, 370, 300, labelHeight);

        fileMessage = new JLabel();
        fileMessage.setBounds(20, 410, 360, labelHeight);

        message = new JLabel();
        message.setBounds(20, 430, 360, labelHeight);

        frame.add(trainingFileButton);
        frame.add(inputDataFileButton);
        frame.add(predictionOutputFileButton);
        frame.add(fileMessage);
        frame.add(message);
        frame.add(modelInfo);
        frame.add(alphaReport);
        frame.add(gammaReport);
        frame.add(logMarginalLikelihoodReport);
        frame.add(fitButton);
        frame.add(predictionsButton);
        frame.add(chooseAlphaButton);
        frame.add(chooseGammaButton);
        frame.add(howToButton);

        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        fileChooser = new JFileChooser(FileSystemView.getFileSystemView());
    }

    private void displayhelp(){
        if(helpNotRead) {
            getHelpText();
            helpNotRead = false;
        }

        JFrame helpFrame = new JFrame("How to use");
        helpFrame.setSize(700,700);
        helpFrame.getContentPane().setLayout(new BorderLayout());

        JTextPane help = new JTextPane();
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        help.setCharacterAttributes(attributeSet, true);
        help.setContentType("text/html");
        help.setText(helpText);

        int textWidth = helpFrame.getWidth() - 100;
        int textHeight = helpFrame.getHeight() - 100;
        help.setPreferredSize(new Dimension(textWidth, textHeight));
        help.setFont(new Font("Arial", Font.PLAIN, 14));
        help.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(help);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        helpFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        helpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        helpFrame.setVisible(true);
    }

    private void chooseAlpha(){
        JFrame alphaFrame = new JFrame("Choose alpha");
        final int frameHeight = 80;
        final int frameWidth = 400;
        alphaFrame.setSize(frameWidth, frameHeight);

        JLabel label = new JLabel("Input alpha value: ");
        label.setBounds(10, 10, 120, labelHeight);

        JLabel error = new JLabel();
        error.setBounds(10,40, 200, labelHeight);

        JTextField alphaField = new JTextField();
        alphaField.setBounds(130, 10, 200, fieldHeight);
        alphaField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double alpha = Double.parseDouble(alphaField.getText());
                    if(alpha <= 0.0)
                        throw new IllegalArgumentException("Alpha must be positive");
                    gprModel.setAlpha(alpha);
                    message.setText("Alpha set to: " + gprModel.getAlpha());
                    error.setText("");
                    alphaFrame.setSize(frameWidth,frameHeight);
                    setAlphaReport();
                    unsetLogMarginalLikelihoodReport();
                    predictionsButton.setEnabled(false);
                    predictionOutputFileButton.setEnabled(false);
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
        label.setBounds(10, 10, 120, labelHeight);

        JLabel error = new JLabel();
        error.setBounds(10,40, 300, labelHeight);

        JTextField gammaField = new JTextField();
        gammaField.setBounds(130, 10, 200, fieldHeight);
        gammaField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double gamma = Double.parseDouble(gammaField.getText());
                    if(gamma < 0.0)
                        throw new IllegalArgumentException("Gamma^2 must be non-negative.");

                    gprModel.setGammaSquared(gamma);
                    message.setText("Gamma^2 set to: " + gprModel.getGammaSquared());
                    error.setText("");
                    gammaFrame.setSize(frameWidth,frameHeight);
                    setGammaSquaredReport();
                    unsetLogMarginalLikelihoodReport();
                    predictionsButton.setEnabled(false);
                    predictionOutputFileButton.setEnabled(false);
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

        if(command.equals(fitButtonLabel))
            fitButtonPressed();
        else if (command.equals(predictionsButtonLabel))
            predictionButtonPressed();
        else if (command.equals(chooseAlphaButtonLabel))
            chooseAlpha();
        else if (command.equals(chooseGammaButtonLabel))
            chooseGamma();
        else if (command.equals(howToButtonLabel))
            displayhelp();
    }

    private void predictionButtonPressed() {
        if(gprModel.isModelCreated()) {
            message.setText(gprModel.getPredictions());
            predictionOutputFileButton.setEnabled(true);
        }
        else
            message.setText("Create model before getting predictions.");
    }

    private void fitButtonPressed() {
        if(!gprModel.gprCalculatorIsNull()) {
            message.setText(gprModel.createModel());
            setLogMarginalLikelihoodReport();
            inputDataFileButton.setEnabled(true);
            predictionsButton.setEnabled(gprModel.isInputDataProvided());
        }
        else
            message.setText("Add training set before creating model.");
    }

    private class FrameWithActionListener extends JFrame implements ActionListener{

        public FrameWithActionListener() throws HeadlessException {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            int dialog = 0;
            message.setText("");

            if(command.equals(trainingFileButtonLabel)){
                dialog = fileChooser.showOpenDialog(null);
                if(dialog == JFileChooser.APPROVE_OPTION) {
                    trainingFileName = fileChooser.getSelectedFile().getAbsolutePath();
                    fileMessage.setText("Training file: " + fileChooser.getSelectedFile().getName());
                    gprModel.initialiseGPR(trainingFileName);
                    fitButton.setEnabled(true);
                    chooseAlphaButton.setEnabled(true);
                    setAlphaReport();
                    chooseGammaButton.setEnabled(true);
                    setGammaSquaredReport();
                }
                else
                    fileMessage.setText("Open training file cancelled.");
            }
            else if(command.equals(inputDataFileButtonLabel)) {
                if (!gprModel.isModelCreated())
                    message.setText("Create model before adding data.");
                else {
                    dialog = fileChooser.showOpenDialog(null);
                    if (dialog == JFileChooser.APPROVE_OPTION) {
                        inputDataFileName = fileChooser.getSelectedFile().getAbsolutePath();
                        fileMessage.setText("Data file: " + fileChooser.getSelectedFile().getName());
                        message.setText(gprModel.addData(inputDataFileName));
                        predictionsButton.setEnabled(true);
                    } else
                        fileMessage.setText("Open input data file cancelled.");
                }
            }
            else if(command.equals(predictionOutputFileButtonLabel)){
                if(!gprModel.isPredictionsGenerated()) {
                    message.setText("Calculate predictions before saving.");
                }
                else{
                    dialog = fileChooser.showSaveDialog(null);
                    if(dialog == JFileChooser.APPROVE_OPTION) {
                        predictionsFileName = fileChooser.getSelectedFile().getAbsolutePath();
                        fileMessage.setText("Predictions file: " + fileChooser.getSelectedFile().getName());
                        message.setText(gprModel.savePredictions(predictionsFileName));
                    }
                    else
                        fileMessage.setText("Save predictions file cancelled");
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

    private void setAlphaReport(){
        alphaReport.setText("Alpha = " + gprModel.getAlpha());
    }

    private void setGammaSquaredReport(){
        gammaReport.setText("Gamma^2 = " + gprModel.getGammaSquared());
    }

    private void setLogMarginalLikelihoodReport(){
        var decimalFormat = new DecimalFormat("########.##");
        logMarginalLikelihoodReport.setText("Log marginal likelihood = "
                + decimalFormat.format(gprModel.getLogMarginalLikelihood()));
    }

    private void unsetLogMarginalLikelihoodReport(){
        logMarginalLikelihoodReport.setText("Regenerate model for log marginal likelihood.");
    }
}

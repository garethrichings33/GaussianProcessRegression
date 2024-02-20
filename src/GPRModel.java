public class GPRModel {
    private GPRCalculator gprCalculator = null;
    private DataTensor trainingDataTensor;
    private DataTensor inputDataTensor;
    private DataTensor predictionsTensor;
    private boolean modelCreated = false;
    private boolean predictionsGenerated = false;

    private boolean inputDataProvided = false;

    private double alpha = 0.5;

    private double gammaSquared = 1.0e-8;

    public void initialiseGPR(String trainingFileName){
        CSVHandler dataCSV = new CSVHandler(trainingFileName);
        trainingDataTensor = null;
        try {
            trainingDataTensor = DataReader.csvToTensor(dataCSV, "training");
        }catch (IllegalArgumentException illArgExcp){
            System.out.println("Illegal tensor type.");
        }
        gprCalculator = new GPRCalculator(trainingDataTensor, alpha, gammaSquared);
        setNewTrainingSet();
    }

    public String addData(String inputDataFileName){
        CSVHandler predictionCSV = new CSVHandler(inputDataFileName);
        inputDataTensor = null;
        try {
            inputDataTensor = DataReader.csvToTensor(predictionCSV, "prediction");
        }catch (IllegalArgumentException illArgExcp){
            System.out.println("Illegal tensor type.");
        }

        setPredictionsGenerated(false);

        String message = "";
        if(!dataSetIsValid()) {
            message = "Input data structure does not match training set.";
            inputDataProvided = false;
        }
        else
            inputDataProvided = true;

        return message;
    }

    public String createModel(){
        gprCalculator.createModel();
        setModelCreated(true);
        setPredictionsGenerated(false);
        return "Model created.";
    }

    public String getPredictions(){
        predictionsTensor = new DataTensor(inputDataTensor.getNumberOfData(),
                inputDataTensor.getDimensions(), true);
        for(int i = 0; i < inputDataTensor.getNumberOfData(); i++) {
            double prediction[] = inputDataTensor.getCoordinates(i);
            predictionsTensor.setCoordinates(i, prediction);
            predictionsTensor.setValue(i, gprCalculator.getPrediction(prediction));
            predictionsTensor.setVariance(i, gprCalculator.getVariance(prediction));
        }
        setPredictionsGenerated(true);
        return "Predictions evaluated.";
    }

    public String savePredictions(String predictionsFileName){
        if(!isPredictionsGenerated())
            return "Calculate predictions before saving.";

        CSVHandler outputCSV = new CSVHandler(predictionsFileName,
                predictionsTensor.getNumberOfData(), predictionsTensor.getTotalColumns());
        DataWriter.tensorToCSV(predictionsTensor, outputCSV);
        return "Predictions saved to file.";
    }

    public boolean gprCalculatorIsNull(){
        return gprCalculator == null;
    }

    private boolean dataSetIsValid(){
        return inputDataTensor.getDimensions() == trainingDataTensor.getDimensions();
    }

    public boolean isModelCreated() {
        return modelCreated;
    }

    public boolean isPredictionsGenerated() {
        return predictionsGenerated;
    }

    public boolean isInputDataProvided() {
        return inputDataProvided;
    }

    public void setAlpha(double alpha){
        this.alpha = alpha;
        gprCalculator.setAlpha(alpha);
    }

    public double getAlpha(){
        return alpha;
    }

    public void setGammaSquared(double gammaSquared){
        this.gammaSquared = gammaSquared;
        gprCalculator.setGammaSquared(gammaSquared);
    }

    public double getGammaSquared(){
        return this.gammaSquared;
    }

    public double getLogMarginalLikelihood() {return gprCalculator.calculateLogMarginalLikelihood();}

    private void setModelCreated(boolean modelCreated) {
        this.modelCreated = modelCreated;
    }

    private void setPredictionsGenerated(boolean predictionsGenerated) {
        this.predictionsGenerated = predictionsGenerated;
    }

    private void setNewTrainingSet(){
        setModelCreated(false);
        setPredictionsGenerated(false);
    }
}

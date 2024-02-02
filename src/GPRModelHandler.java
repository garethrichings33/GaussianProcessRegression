public class GPRModelHandler {
    private GPRCalculator gprCalculator = null;
    private DataTensor trainingDataTensor;
    private DataTensor inputDataTensor;
    private DataTensor predictionsTensor;
    private boolean modelCreated = false;
    private boolean predictionsGenerated = false;

    public void initialiseGPR(String trainingFileName){
        CSVHandler dataCSV = new CSVHandler(trainingFileName);
        trainingDataTensor = null;
        try {
            trainingDataTensor = DataReader.csvToTensor(dataCSV, "training");
        }catch (IllegalArgumentException illArgExcp){
            System.out.println("Illegal tensor type.");
        }
        gprCalculator = new GPRCalculator(trainingDataTensor);
        setNewTrainingSet();
    }

    public String addData(String inputDataFileName){
        CSVHandler predictionCSV = new CSVHandler(inputDataFileName);
        inputDataTensor = null;
        try {
            inputDataTensor = DataReader.csvToTensor(predictionCSV, "prediction");
//            predictionTensor.writeDataVector();
        }catch (IllegalArgumentException illArgExcp){
            System.out.println("Illegal tensor type.");
        }

        setPredictionsGenerated(false);

        String message = "";
        if(!dataSetIsValid())
            message ="Input data structure does not match training set.";
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

    public void setModelAlpha(double alpha){
        gprCalculator.setModelAlpha(alpha);
    }
    public double getModelAlpha(){
        return gprCalculator.getModelAlpha();
    }
    public void setModelGammaSquared(double alpha){
        gprCalculator.setModelGammaSquared(alpha);
    }
    public double getModelGammaSquared(){
        return gprCalculator.getModelGammaSquared();
    }

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

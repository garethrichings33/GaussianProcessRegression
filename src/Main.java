public class Main {
    public static void main(String[] args) {
        CSVHandler dataCSV = new CSVHandler("data/x-squared.csv");
        DataTensor dataTensor = null;
        try {
            dataTensor = DataReader.csvToTensor(dataCSV, "training");
//            dataTensor.writeDataVector();
        }catch (IllegalArgumentException illArgExcp){
            System.out.println("Illegal tensor type.");
        }

        CSVHandler predictionCSV = new CSVHandler("data/x-prediction.csv");
        DataTensor predictionTensor = null;
        try {
            predictionTensor = DataReader.csvToTensor(predictionCSV, "prediction");
//            predictionTensor.writeDataVector();
        }catch (IllegalArgumentException illArgExcp){
            System.out.println("Illegal tensor type.");
        }


//        dataVector.writeDataVector();
        GPR gpr = new GPR(dataTensor);
        gpr.createModel();
//        gpr.writeWeights();
//        gpr.optimiseModel();

        DataTensor outputTensor = new DataTensor(predictionTensor.getNumberOfData(),
                predictionTensor.getDimensions(), true);
        for(int i = 0; i < predictionTensor.getNumberOfData(); i++) {
            double prediction[] = predictionTensor.getCoordinates(i);
//            System.out.println("Coordinate: " + prediction[0]);
            outputTensor.setCoordinates(i, prediction);
            outputTensor.setValue(i, gpr.getPrediction(prediction));
            outputTensor.setVariance(i, gpr.getVariance(prediction));
//            System.out.println("Prediction: " + gpr.getPrediction(prediction));
//            System.out.println("Variance: " + gpr.getVariance(prediction));
        }
//        predictionTensor.writeDataVector();
//        System.out.println("Log Marginal Likelihood: " + gpr.getLogMarginalLikelihood());

            CSVHandler outputCSV = new CSVHandler("data/output.csv",
                outputTensor.getNumberOfData(), outputTensor.getTotalColumns());
            DataWriter.tensorToCSV(outputTensor, outputCSV);

    }
}
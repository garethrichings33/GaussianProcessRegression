import java.util.ArrayList;

public class DataReader {
    static private int dimensions;
    static private int numberOfData;

    static public DataTensor csvToTensor(CSVHandler csvHandler, String typeOfDataSet){
        ArrayList<ArrayList<String>> csvList = csvHandler.getRecords();
        int numberOfRecords = csvHandler.getNumberOfRecords();
        int numberOfValues = csvHandler.getNumberOfValues();

        numberOfData = getNumberOfValidRecords(csvList, numberOfRecords, numberOfValues);
        if(typeOfDataSet == "training")
            dimensions = csvHandler.getNumberOfValues() - 1;
        else if (typeOfDataSet == "prediction") {
            dimensions = csvHandler.getNumberOfValues();
        }
        else{
            throw new IllegalArgumentException();
        }

        DataTensor tensor =
                new DataTensor(numberOfData, dimensions);

        double[] coordinates = new double[dimensions];
        double value;
        int index = 0;
        for(int i = 0; i < numberOfRecords; i++) {
            try {
                for (int j = 0; j < dimensions; j++)
                    coordinates[j] = Double.parseDouble(csvList.get(i).get(j));
                tensor.setCoordinates(index, coordinates);
                if(typeOfDataSet == "training") {
                    value = Double.parseDouble(csvList.get(i).get(dimensions));
                    tensor.setValue(index, value);
                }
                index++;
            }
            catch (NumberFormatException nfExcp) {
            }
        }

        return tensor;
    }

    private static int getNumberOfValidRecords(ArrayList<ArrayList<String>> list,
                                               int numberOfRecords, int numberOfValues) {
        boolean allDouble = true;
        int numberOfValidRecords = 0;
        for(int i = 0; i < numberOfRecords; i++) {
            allDouble = true;
            for (int j = 0; j < numberOfValues; j++) {
                try{
                    Double.parseDouble(list.get(i).get(j));
                }
                catch(NumberFormatException nfExcp){
                    allDouble = false;
                    break;
                }
            }
            if(allDouble) numberOfValidRecords++;
        }
        return numberOfValidRecords;
    }

    public static int getDimensions() {
        return dimensions;
    }

    public static int getNumberOfData() {
        return numberOfData;
    }
}

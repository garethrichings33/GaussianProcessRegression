import java.util.ArrayList;

public class DataWriter {
    static public void tensorToCSV(DataTensor dataTensor, CSVHandler csvHandler){
        boolean hasVariance = dataTensor.isHasVariance();
        int numberOfRecords = csvHandler.getNumberOfRecords();
        int numberOfValues = csvHandler.getNumberOfValues();
        int dimensions = dataTensor.getDimensions();

        ArrayList<ArrayList<String>> list = new ArrayList<>();

        double[] coordinates;
        for(int i = 0; i < numberOfRecords; i++){
            ArrayList<String> record = new ArrayList<>();
            for(int j = 0; j < dimensions; j++){
                coordinates = dataTensor.getCoordinates(i);
                record.add(Double.toString(coordinates[j]));
                record.add(Double.toString(dataTensor.getValue(i)));
            }
            if(hasVariance){
                record.add(Double.toString(dataTensor.getVariance(i)));
            }
            list.add(record);
        }

        ArrayList<String> headings = new ArrayList<>();
        for(int i = 0; i < dimensions; i++)
            headings.add("Coordinate " + i);
        headings.add("Prediction");
        if(hasVariance)
            headings.add("Variance");

        csvHandler.setRecords(list, headings);
    }
}

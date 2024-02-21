package main.java;
public class DataTensor {
    private double[][] coordinates;
    private double[] values;
    private double[] variances;
    private boolean hasVariance;
    private int dimensions;
    private int numberOfData;
    private int totalColumns;

    public DataTensor(int numberOfData, int dimensions) {
        this(numberOfData, dimensions, false);
    }

    public DataTensor(int numberOfData, int dimensions, boolean hasVariance) {
        this.numberOfData = numberOfData;
        this.dimensions = dimensions;
        this.hasVariance = hasVariance;
        coordinates = new double[numberOfData][dimensions];
        values = new double[numberOfData];
        totalColumns = dimensions + 1;
        if(hasVariance) {
            variances = new double[numberOfData];
            totalColumns++;
        }
    }

    public void setCoordinates(int index, double[] coordinateVector) {
        for(int i =0; i<dimensions; i++)
            coordinates[index][i] = coordinateVector[i];
    }

    public void setValue(int index, double value){
        values[index] = value;
    }

    public void setVariance(int index, double value){
        variances[index] = value;
    }

    public void writeDataVector(){
        System.out.println("Coordinate | Value");
        for(int i =0; i<numberOfData; i++)
            System.out.println(getCoordinates(i)[0] + " | " + getValue(i));
    }

    public int getDimensions() {
        return dimensions;
    }

    public int getNumberOfData() {
        return numberOfData;
    }

    public boolean isHasVariance() {
        return hasVariance;
    }

    public int getTotalColumns() {
        return totalColumns;
    }

    public double[] getCoordinates(int index){
        return coordinates[index];
    }

    public double getValue(int index){
        return values[index];
    }

    public double getVariance(int index){
        return variances[index];
    }

    public double[] getValueVector(){return values;}
}

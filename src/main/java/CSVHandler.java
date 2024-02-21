package main.java;
import java.io.*;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Scanner;

public class CSVHandler {
    final private String fileName;
    File csvFile;
    private int numberOfRecords;
    private int numberOfValues;
    private ArrayList<ArrayList<String>> records;
    private final String COMMA_DELIMITER = ",";
    private Scanner csvScanner;
    private Scanner recordScanner;

    public CSVHandler(String fileName) {
        this.fileName = fileName;
        csvFile = new File(fileName);
        try {
            if (!csvExists())
                throw new NoSuchFileException(fileName);
        }
        catch(NoSuchFileException nsfExcp){
            System.out.println("File: " + fileName + " does not exist or is not a file.");
            return;
        }

        records = new ArrayList<>();
        countRecords();
        countValuesPerRecord();
    }

    public CSVHandler(String fileName, int numberOfRecords, int numberOfValues) {
        this.fileName = fileName;
        csvFile = new File(fileName);
        try {
            if(csvFile.createNewFile())
                System.out.println("File " + fileName + " created.");
            else
                System.out.println("File " + fileName + " already exists ands will be overwritten.");
        }
        catch(IOException ioExcp){
            System.out.println("Error opening file: " + fileName);
            ioExcp.printStackTrace();
        }

        setNumberOfRecords(numberOfRecords);
        setNumberOfValues(numberOfValues);
        records = new ArrayList<>();
    }

    private void createCSVScanner(){
        try {
            csvScanner = new Scanner(csvFile);
        }
        catch (FileNotFoundException fnfexcp){
            System.out.println("File: " + fileName + " not found.");
        }
    }

    private void createRecordScanner(String record){
        recordScanner = new Scanner(record).useDelimiter(COMMA_DELIMITER);
    }

    private void countValuesPerRecord() {
        createCSVScanner();
        int valuesCount = 0;
        if(csvScanner.hasNextLine()) {
            createRecordScanner(csvScanner.next());
            while(recordScanner.hasNext()){
                if(recordScanner.next().length() > 0)
                    valuesCount++;
            }
        }
        setNumberOfValues(valuesCount);
    }

    private void countRecords() {
        int recordsCount = 0;
        createCSVScanner();
        while(csvScanner.hasNextLine()) {
            recordsCount++;
            csvScanner.next();
        }
        setNumberOfRecords(recordsCount);
        csvScanner.close();
    }

    private boolean csvExists(){
        return csvFile.isFile();
    }

    private void readCSV(){
        createCSVScanner();
        try {
            while(csvScanner.hasNextLine())
                records.add(parseRecordFromLine(csvScanner.nextLine()));
        }
        catch(Exception excp){
            System.out.println("Error reading CSV file.");
        }
    }

    private ArrayList<String> parseRecordFromLine(String line) {
        ArrayList<String> list = new ArrayList<>();
        createRecordScanner(line);
        while(recordScanner.hasNext())
                list.add(recordScanner.next());
        return list;
    }

    private void writeCSV(ArrayList<ArrayList<String>> list, ArrayList<String> headings){
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(csvFile);
        } catch (IOException ioExcp) {
            System.out.println("Error opening CSV file: " + csvFile.getName());
        }

        printWriter.write(convertToCSV(headings) + System.lineSeparator());

        list.stream().map(this::convertToCSV).forEach(printWriter::println);
        printWriter.close();
    }

    private String convertToCSV(ArrayList<String> values){
//        return values.stream().collect(Collectors.joining(COMMA_DELIMITER));
        return String.join(COMMA_DELIMITER,values);
    }

    public int getNumberOfRecords() {
        return numberOfRecords;
    }

    public int getNumberOfValues() {
        return numberOfValues;
    }

    public ArrayList<ArrayList<String>> getRecords() {
        readCSV();
        return records;
    }

    public void setRecords(ArrayList<ArrayList<String>> list, ArrayList<String> headings){
        writeCSV(list, headings);
    }

    private void setNumberOfRecords(int numberOfRecords) {
        this.numberOfRecords = numberOfRecords;
    }

    private void setNumberOfValues(int numberOfValues) {
        this.numberOfValues = numberOfValues;
    }
}

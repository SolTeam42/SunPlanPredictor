package com.ft;

import com.opencsv.CSVReader;
import matrix.Matrix;
import matrix.NoSquareException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import regression.MultiLinear;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ReadExcelFile {

    private static SheetData readXLSXFile(String fileName) throws IOException {
        InputStream fileInputStream = new FileInputStream(fileName);

        DataHeader dataHeader = null;
        SheetData sheetData = null;

        XSSFWorkbook wb = new XSSFWorkbook(fileInputStream);
        XSSFSheet sheet = wb.getSheetAt(0);

        for (Row row : sheet) {
            if (dataHeader == null) {
                // create data header
                dataHeader = new DataHeader(10);

                for (Cell cell : row) {
                    if (cell.getCellTypeEnum() == CellType.STRING) {
                        dataHeader.addColumnName(cell.getStringCellValue());
                    } else {
                        throw new RuntimeException("non-string value in header");
                    }
                }
            } else {

                if (sheetData == null) {
                    sheetData = new SheetData(dataHeader);
                }

                // fill in the data
                List<String> rowData = new ArrayList<>(dataHeader.getHeaderSize());

                for (Cell cell : row) {
                    if (cell.getCellTypeEnum() == CellType.NUMERIC || cell.getCellTypeEnum() == CellType.STRING) {
                        rowData.add(String.valueOf(cell.getNumericCellValue()));
                    } else {
                        throw new RuntimeException("non-expected value in data");
                    }
                }

                sheetData.addRow(rowData);
            }

        }

        return sheetData;
    }


    private static SheetData readCSVFile(String fileName) throws IOException {

        DataHeader dataHeader = null;
        SheetData sheetData = null;

        CSVReader reader = new CSVReader(new FileReader(fileName));

        String[] csvLine;
        while ((csvLine = reader.readNext()) != null) {

            if (dataHeader == null) {
                dataHeader = new DataHeader(csvLine.length);
                for (String columnName : csvLine) {
                    dataHeader.addColumnName(columnName);
                }
                sheetData = new SheetData(dataHeader);
                continue;
            }

            List<String> rowData = Arrays.asList(csvLine);
            sheetData.addRow(rowData);
        }

        reader.close();

        return sheetData;
    }

    public static void main(String[] args) throws IOException {
//        SheetData sheetData = readXLSXFile("data.xlsx");

//        ID,Sec,Date,Time,Barometric_Preasure,Humidity,Solar_Radiation,Temperature,Wind_Direction,Wind_Speed

        SheetData sheetData = readCSVFile("data.csv");

        String[] attributeColumns = {
                //"Time" // calculate: sun rays angle
                "Barometric_Preasure",
                "Humidity",
                "Temperature",
                "Wind_Direction",
                "Wind_Speed",
        };

        createRegressionCoefficientsMultiLinearRegression(sheetData, "Solar_Radiation", Arrays.asList(attributeColumns));

    }

    private static void createRegressionCoefficientsMultiLinearRegression(SheetData sheetData, String resultColumn, List<String> attributeColumns) {
        double[][] xValues = new double[sheetData.size()][attributeColumns.size()];
        double[][] yValues = new double[sheetData.size()][1];

        for (int i = 0; i < sheetData.size(); i++) {
            List<String> stringValues = sheetData.getValues(i, attributeColumns);

            double[] values = new double[attributeColumns.size()];
            for (int j = 0; j < attributeColumns.size(); j++) {
                values[j] = Double.valueOf(stringValues.get(j));
            }

            xValues[i] = values;
            yValues[i] = new double[]{Double.valueOf(sheetData.getValue(i, resultColumn))};
        }

        Matrix X = new Matrix(xValues);
        Matrix Y = new Matrix(yValues);

        MultiLinear ml = new MultiLinear(X, Y, false);

        try {
            Matrix beta = ml.calculate();
            System.out.println(Arrays.deepToString(beta.getValues()));
        } catch (NoSquareException e) {
            e.printStackTrace();
        }

    }

    private static void createRegressionCoefficientsLinearRegression(SheetData sheetData, String resultColumn, List<String> attributeColumns) {

        List<Double> coefficients = new ArrayList<>(attributeColumns.size() + 1);
        for (int i = 0; i < attributeColumns.size(); i++) {
            coefficients.add(0.0);
        }
        coefficients.add(1.0); // coefficient of external attribute

        for (int i = 0; i < sheetData.size(); i++) {

            // select values
            List<String> stringValues = sheetData.getValues(i, attributeColumns);
            List<Double> values = new ArrayList<>(attributeColumns.size());
            stringValues.forEach(v -> values.add(Double.valueOf(v)));
            values.add(1.0); // external attribute

            // test predictor
            double actualResult = calculateFunction(coefficients, values);
            double desiredResult = Double.valueOf(sheetData.getValue(i, resultColumn));

            // train predictor
            coefficients = updateCoefficients(coefficients, values, actualResult, desiredResult);

        }

        System.out.println("===================");
        System.out.println(coefficients);

    }

    private static double calculateFunction(List<Double> coefficients, List<Double> values) {
        if (coefficients.size() != values.size()) {
            throw new AssertionError("coefficients size mismatch :" + coefficients + " values:" + values);
        }

        double result = 0.0;

        for (int i = 0; i < coefficients.size(); i++) {
            result += coefficients.get(i) * values.get(i);
        }

        return result;
    }

    private static List<Double> updateCoefficients(List<Double> coefficients, List<Double> values, double actualResult, double desiredResult) {
        List<Double> newCoefficients = new ArrayList<>(coefficients.size());

        for (int i = 0; i < coefficients.size(); i++) {
            double newCoefficient = coefficients.get(i) + (desiredResult - actualResult) * values.get(i);
            newCoefficients.add(newCoefficient);
        }

        return newCoefficients;
    }

}
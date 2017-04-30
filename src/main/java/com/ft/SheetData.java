package com.ft;

import java.util.ArrayList;
import java.util.List;

class SheetData {

    private final List<List<String>> sheetValues;
    private final DataHeader dataHeader;

    SheetData(DataHeader dataHeader) {
        this.sheetValues = new ArrayList<>();
        this.dataHeader = dataHeader;
    }

    void addRow(List<String> rowData) {
        sheetValues.add(rowData);
    }

    private List<String> getRow(int index) {
        return sheetValues.get(index);
    }

    List<String> getValues(int index, List<String> selectedColumns) {
        List<String> values = new ArrayList<>(selectedColumns.size());

        for (String selectedColumn : selectedColumns) {
            values.add(getValue(index, selectedColumn));
        }

        return values;
    }

    String getValue(int index, String selectedColumn) {
        int columnIndex = dataHeader.getColumnIndex(selectedColumn);
        return getRow(index).get(columnIndex);
    }

    int size() {
        return sheetValues.size();
    }
}

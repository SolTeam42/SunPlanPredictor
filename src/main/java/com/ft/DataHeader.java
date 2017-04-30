package com.ft;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

class DataHeader {

    private final Map<String, Integer> dataIndices;

    public DataHeader(int initialCapacity) {
        dataIndices = new LinkedHashMap<>(initialCapacity);
    }

    public void addColumnName(String columnName) {
        dataIndices.put(columnName, dataIndices.size());
    }

    public int getColumnIndex(String columnName) {
        return dataIndices.get(columnName); // no time for null safety :)
    }

    public int getHeaderSize() {
        return dataIndices.size();
    }

}

package matrix;

public class Matrix {

    private int rows;
    private int cols;
    private double[][] data;

    public Matrix(double[][] dat) {
        this.data = dat;
        this.rows = dat.length;
        this.cols = dat[0].length;
    }

    public Matrix(int row, int col) {
        this.rows = row;
        this.cols = col;
        data = new double[row][col];
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public double[][] getValues() {
        return data;
    }

    public void setValues(double[][] values) {
        this.data = values;
    }

    public void setValueAt(int row, int col, double value) {
        data[row][col] = value;
    }

    public double getValueAt(int row, int col) {
        return data[row][col];
    }

    public boolean isSquare() {
        return rows == cols;
    }

    public int size() {
        if (isSquare()) {
            return rows;
        }
        return -1;
    }

    public Matrix multiplyByConstant(double constant) {
        Matrix mat = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                mat.setValueAt(i, j, data[i][j] * constant);
            }
        }
        return mat;
    }

    public Matrix insertColumnWithValue1() {
        Matrix X_ = new Matrix(this.getRows(), this.getCols() + 1);
        for (int i = 0; i < X_.getRows(); i++) {
            for (int j = 0; j < X_.getCols(); j++) {
                if (j == 0) X_.setValueAt(i, j, 1.0);
                else X_.setValueAt(i, j, this.getValueAt(i, j - 1));

            }
        }
        return X_;
    }
}

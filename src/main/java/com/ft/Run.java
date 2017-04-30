package com.ft;

import matrix.Matrix;
import matrix.NoSquareException;
import regression.MultiLinear;

import java.util.Arrays;

public class Run {
    public static void main(String[] args) throws NoSquareException {
        Matrix X = new Matrix(new double[][]{{4, 0, 1}, {7, 1, 1}, {6, 1, 0}, {2, 0, 0}, {3, 0, 1}});
        Matrix Y = new Matrix(new double[][]{{27}, {29}, {23}, {20}, {21}});
        MultiLinear ml = new MultiLinear(X, Y,true);
        Matrix beta = ml.calculate();

        System.out.println(Arrays.deepToString(beta.getValues()));
    }
}

package espresso;

import java.util.concurrent.ThreadLocalRandom;

public class Matrix {
  public static void mismatch(boolean c, Matrix a, Matrix b, String s) {
    String m = String.format("Matrix.%s: mismatched dimensions (%d, %d) and (%d, %d)", s, a.rows, a.cols, b.rows, b.cols);
    CrashMe.fatal(c, m);
  }

  public static void print(Matrix m) {
    String s = "[";
    for (int i = 0; i < m.rows; i++) {
      s += (i == 0) ? " [ " : "  [ ";
      for (int j = 0; j < m.cols; j++) {
        s += String.format("%010f ", m.array[i][j]);
      }
      s += (i == m.rows - 1) ? "] " : "]\n";
    }
    s += "]\n";
    System.out.printf("%s", s);
  }

  /* Allocate a matrix with some number of rows and columns,
     do not fill it with anything. */
  public Matrix(int rows, int cols) {
    this.rows = rows;
    this.cols = cols;
    array = new double[rows][cols];
  }

  public Matrix(double[][] array) {
    this.rows = array.length;
    this.cols = array[0].length;
    this.array = array;
  }

  /* Initialize a matrix to all zeros. */
  public static Matrix zeros(int rows, int cols) {
    Matrix z = new Matrix(rows, cols);
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        z.array[i][j] = 0.0;
      }
    }
    return z;
  }

  /* Initialize a one-hot column vector. */
  public static Matrix oneHot(int cols, int where) {
    Matrix o = Matrix.zeros(1, cols);
    o.array[0][where] = 1;
    return o;
  }

  /* Initialize a matrix with random numbers between min and max. */
  public static Matrix random(int rows, int cols, double min, double max) {
    Matrix r = new Matrix(rows, cols);
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++)
        r.array[i][j] = ThreadLocalRandom.current().nextDouble(min, max);
    }
    return r;
  }

  /* Matrix arithmetic. Note that `dot()' is specifically intended for traditional
     matrix multiplication (as the generalization of vector dot products), while
     `multiply()' is for component-wise multiplication. */

  public static Matrix dot(Matrix a, Matrix b) {
    Matrix r = Matrix.zeros(a.rows, b.cols);
    mismatch(a.cols == b.rows, a, b, "dot");
    for (int i = 0; i < a.rows; i++) {
      for (int j = 0; j < b.cols; j++) {
        for (int k = 0; k < a.cols; k++) {
          r.array[i][j] += a.array[i][k] * b.array[k][j];
        }
      }
    }
    return r;
  }

  public static Matrix multiply(Matrix a, Matrix b) {
    Matrix r = new Matrix(a.rows, a.cols);
    mismatch(a.rows == b.rows && a.cols == b.cols, a, b, "multiply");
    for (int i = 0; i < a.rows; i++) {
      for (int j = 0; j < a.cols; j++) {
        r.array[i][j] = a.array[i][j] * b.array[i][j];
      }
    }
    return r;
  }
  
  public static Matrix add(Matrix a, Matrix b) {
    mismatch(a.rows == b.rows && a.cols == b.cols, a, b, "add");
    Matrix r = new Matrix(a.rows, a.cols);
    for (int i = 0; i < a.rows; i++) {
      for (int j = 0; j < a.cols; j++) {
        r.array[i][j] = a.array[i][j] + b.array[i][j];
      }
    }
    return r;
  }

  public static Matrix subtract(Matrix a, Matrix b) {
    Matrix r = new Matrix(a.rows, a.cols);
    for (int i = 0; i < a.rows; i++) {
      for (int j = 0; j < a.cols; j++) {
        r.array[i][j] = a.array[i][j] - b.array[i][j];
      }
    }
    return r;
  }

  public Matrix transpose() {
    Matrix r = new Matrix(cols, rows);
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        r.array[j][i] = array[i][j];
      }
    }
    return r;
  }

  /* Logistic functions for ML. */

  public static Matrix sigmoid(Matrix z) {
    Matrix r = new Matrix(z.rows, z.cols);
    for (int i = 0; i < z.rows; i++) {
      for (int j = 0; j < z.cols; j++) {
        r.array[i][j] = 1.0 / (1 + Math.exp(-z.array[i][j]));
      }
    }
    return r;
  }
  
  public static Matrix sigmoidPrime(Matrix z) {
    Matrix r = new Matrix(z.rows, z.cols);
    for (int i = 0; i < z.rows; i++) {
      for (int j = 0; j < z.cols; j++) {
        double ez = Math.exp(z.array[i][j]);
        r.array[i][j] = ez / Math.pow(1 + ez, 2);
      }
    }
    return r;
  }

  /* Take the top left element of the matrix. */
  public double scalarize() {
    return array[0][0];
  }

  public int rows;
  public int cols;
  public double[][] array;
}

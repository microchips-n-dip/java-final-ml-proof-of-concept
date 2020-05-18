package espresso;

/* Let f be a function.
   This implementation of function approximations produces a family g_t
   satisfying (f - g_{t + 1})^2 < (f - g_t)^2. */

public class Approximation {
  public static class Guess {
    public Guess(int num) {
      z = new Matrix[num];
      a = new Matrix[num];
    }

    public Matrix[] z;
    public Matrix[] a;
    public Matrix yhat;
  }

  /* Compute the result of approximation theta. */
  public static Guess forward(Approximation theta, Matrix x) {
    Guess guess = new Guess(theta.weights.length);
    Matrix a = x;
    for (int i = 0; i < theta.weights.length; i++) {
      guess.a[i] = a;
      guess.z[i] = Matrix.dot(a, theta.weights[i]);
      a = Matrix.sigmoid(guess.z[i]);
    }
    guess.yhat = a;
    return guess;
  }

  public static class Gradient {
    public Gradient(Approximation theta) {
      grads = new Matrix[theta.weights.length];
    }

    public Gradient(Gradient old) {
      grads = new Matrix[old.grads.length];
    }

    public static Gradient fresh(Approximation theta) {
      Gradient grad = new Gradient(theta);
      for (int i = 0; i < grad.grads.length; i++) {
        int rows = theta.weights[theta.weights.length - 1 - i].rows;
        int cols = theta.weights[theta.weights.length - 1 - i].cols;
        grad.grads[i] = Matrix.zeros(rows, cols);
      }
      return grad;
    }

    public Matrix[] grads;
  }

  /* Compute the direction in which the approximation \theta_t needs to be adjusted
     to obtain \theta_{t + 1}. */
  public static Gradient dydW(Approximation theta, Guess guess, Matrix delta1) {
    int nl = theta.weights.length - 1;
    Gradient dJdW = new Gradient(theta);
    Matrix zPrime = Matrix.sigmoidPrime(guess.z[nl]);
    Matrix delta = Matrix.multiply(delta1, zPrime);
    dJdW.grads[0] = Matrix.dot(guess.a[nl].transpose(), delta);
    /* Iterate the rest of the backprop. */
    for (int i = 1; i < theta.weights.length; i++) {
      nl--; /* (reverse) step to the next layer. */
      zPrime = Matrix.sigmoidPrime(guess.z[nl]);
      delta = Matrix.multiply(Matrix.dot(delta, theta.weights[nl + 1].transpose()), zPrime);
      dJdW.grads[i] = Matrix.dot(guess.a[nl].transpose(), delta);
    }
    return dJdW;
  }

  public static Gradient dJdW(Approximation theta, Guess guess, Matrix y) {
    Matrix delta1 = Matrix.subtract(guess.yhat, y);
    return dydW(theta, guess, delta1);
  }

  public static Gradient accumulate(Gradient old, Gradient dJdW) {
    Gradient updated = new Gradient(old);
    for (int i = 0; i < updated.grads.length; i++)
      updated.grads[i] = Matrix.add(old.grads[i], dJdW.grads[i]);
    return updated;
  }

  public static Approximation train(Approximation theta, Gradient dJdW) {
    for (int i = 0; i < dJdW.grads.length; i++)
      theta.weights[i] = Matrix.subtract(theta.weights[i], dJdW.grads[dJdW.grads.length - i - 1]);
    return theta;
  }

  public static void print(Approximation theta) {
    System.out.printf("Approximation parameters:\n");
    for (int i = 0; i < theta.weights.length; i++)
      Matrix.print(theta.weights[i]);
  }

  /* Initialize the approximation with a random configuration. */
  public Approximation(int[] sizes) {
    weights = new Matrix[sizes.length - 1];
    for (int i = 0; i < sizes.length - 1; i++)
      weights[i] = Matrix.random(sizes[i], sizes[i + 1], -3.0f, 3.0f);
  }

  public Matrix[] weights;
}





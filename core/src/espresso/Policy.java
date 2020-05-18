package espresso;

public class Policy {
  public static Action.ScoredAction policy(Approximation theta, Action[] actions, Matrix obs) {
    /* Collect all of our approximations for the rewards of each action. */
    Approximation.Guess[] guesses = new Approximation.Guess[actions.length];
    for (int i = 0; i < actions.length; i++) {
      Matrix a = actions[i].matrix;
      Matrix e = new Matrix(1, obs.cols + a.cols);
      for (int j = 0; j < obs.cols; j++)
        e.array[0][j] = obs.array[0][j];
      for (int j = 0; j < a.cols; j++)
        e.array[0][j + obs.cols] = a.array[0][j];
      guesses[i] = Approximation.forward(theta, e);
    }
    /* Figure out which guess is best. */
    int besti = 0;
    double bestq = 0;
    for (int i = 0; i < guesses.length; i++) {
      double q = guesses[i].yhat.scalarize();
      if ((i == 0) || (q > bestq)) {
        besti = i;
        bestq = q;
      }
    }
    /* Add small chance of taking a random action to ensure exploration of the action space. */
    if (Math.random() < 0.1)
      besti = (int)(Math.random() * 4);
    Action besta = actions[besti];
    Approximation.Guess bestguess = guesses[besti];
    return new Action.ScoredAction(besta, bestguess);
  }
}

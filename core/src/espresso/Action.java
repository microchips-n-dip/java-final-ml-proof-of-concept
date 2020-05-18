package espresso;

public abstract class Action {
  public static class ScoredAction {
    public ScoredAction(Action action, Approximation.Guess guess) {
      this.action = action;
      this.guess = guess;
    }

    public Action action;
    public Approximation.Guess guess;
  }

  public int number;
  public Matrix matrix;
}


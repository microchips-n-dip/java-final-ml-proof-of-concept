package com.mygdx.game;

import espresso.Matrix;
import espresso.Approximation;
import espresso.Action;
import espresso.Policy;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Input.Keys;

public class MyGdxGame extends ApplicationAdapter {
  int[] hyperparams;
  Approximation theta;
  Approximation.Gradient grad;
  Action[] actions;
  GameEnvironment genv;
  double last100;
  double totalscore;
  static final int updateInterval = 100;
  int count;
	
  @Override
  public void create () {
    hyperparams = new int[]{8, 7, 8, 5, 1};
    theta = new Approximation(hyperparams);
    grad = Approximation.Gradient.fresh(theta);
    count = 0;
    genv = new GameEnvironment();
    last100 = 0;
    totalscore = 0;
    actions = new Action[]{
      new GameEnvironment.DoNothing(),
      new GameEnvironment.TurnRightAction(),
      new GameEnvironment.TurnLeftAction(),
      new GameEnvironment.BoostAction()
    };
  }

  @Override
  public void render () {
    Action.ScoredAction action = Policy.policy(theta, actions, GameEnvironment.observe(genv));
    GameEnvironment.GameEnvAction a = (GameEnvironment.GameEnvAction) action.action;
    genv = a.act(genv);
    GameEnvironment.draw(genv);
    Matrix score = GameEnvironment.reward(genv);
    last100 += score.scalarize();
    totalscore += score.scalarize();
    Approximation.Gradient dJdW = Approximation.dJdW(theta, action.guess, score);
    grad = Approximation.accumulate(grad, dJdW);
    if (count % updateInterval == 0) {
      theta = Approximation.train(theta, grad);
      grad = Approximation.Gradient.fresh(theta);
      System.out.printf("average of last 100 steps %f\n", last100 / updateInterval);
      System.out.printf("total average %f\n", totalscore / count);
      last100 = 0;
      genv = GameEnvironment.helpOut(genv);
    }
    count++;
  }
	
  @Override
  public void dispose () {
    GameEnvironment.dispose(genv);
  }
}

package com.mygdx.game;

import espresso.Matrix;
import espresso.Action;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

public class GameEnvironment {
  public static abstract class GameEnvAction extends Action {
    public abstract GameEnvironment act(GameEnvironment env);
  }

  public static class DoNothing extends GameEnvAction {
    public DoNothing() {
      number = 0;
      matrix = Matrix.oneHot(4, 0);
    }

    public GameEnvironment act(GameEnvironment env) {
      return env;
    }
  }

  /* Rocket actions. */

//  public class LaunchAction extends GameEnvAction {
//    public GameEnvironment act(GameEnvironment env) {
//      Rocket rocket = env.rocket;
//      rocket.body.setGravityScale(1);
//      Vector2 vel = body.getLinearVelocity();
//      rocket.body.setLinearVelocity(vel.add(rocket.launchVel));
//      return env;
//    }
//  }

  public static class BoostAction extends GameEnvAction {
    public BoostAction() {
      number = 1;
      matrix = Matrix.oneHot(4, 1);
    }

    public GameEnvironment act(GameEnvironment env) {
      env.rocket = Rocket.boost(env.rocket);
      return env;
    }
  }

  public static class TurnRightAction extends GameEnvAction {
    public TurnRightAction() {
      number = 2;
      matrix = Matrix.oneHot(4, 2);
    }

    public GameEnvironment act(GameEnvironment env) {
      Rocket rocket = env.rocket;
      rocket.body.setAngularVelocity(-1);
      return env;
    }
  }

  public static class TurnLeftAction extends GameEnvAction {
    public TurnLeftAction() {
      number = 3;
      matrix = Matrix.oneHot(4, 3);
    }

    public GameEnvironment act(GameEnvironment env) {
      Rocket rocket = env.rocket;
      rocket.body.setAngularVelocity(1);
      return env;
    }
  }

  public static Matrix observe(GameEnvironment env) {
    Rocket rocket = env.rocket;
    double x = rocket.sprite.getX() / 10f;
    double y = rocket.sprite.getY() / 10f;
    double t = 1 - Math.pow(Math.cos(rocket.body.getAngle()), 2);
    double u = rocket.body.getAngularVelocity() / 90;
    return new Matrix(new double[][]{{x, y, t, u}});
  }

  public static Matrix reward(GameEnvironment env) {
    double r = 50 * (1 - Math.exp(-0.001 * env.rocket.body.getPosition().y));
    return new Matrix(new double[][]{{r}});
  }

  public static GameEnvironment helpOut(GameEnvironment env) {
    if (env.rocket.sprite.getY() < 50)
      env.rocket.body.setTransform(new Vector2(50f, 50f), env.rocket.body.getAngle());
    return env;
  }

  public static void draw(GameEnvironment env) {
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    env.world.step(1f/144f, 6, 2);
    env.camera.position.set(env.rocket.body.getPosition().x, env.rocket.body.getPosition().y,0);
    env.camera.update();
    env.batch.setProjectionMatrix(env.camera.combined);
    env.batch.begin();
    Rocket.draw(env.batch, env.rocket);
//    Cannon.draw(env.batch, env.cannon);
    env.batch.end();
  }

  public static void dispose(GameEnvironment env) {
    env.batch.dispose();
  }

  public GameEnvironment() {
    camera = new OrthographicCamera();
    camera.setToOrtho(false, 800, 480);
    batch = new SpriteBatch();
    world = new World(new Vector2(0f, -98f), true);
    Texture rocketSheet = new Texture(Gdx.files.internal("imgs/rocket.png"));
    rocket = new Rocket(rocketSheet, world);
  }

  public Rocket rocket;
//  public Cannon cannon;

  public World world;

  public OrthographicCamera camera;
  public SpriteBatch batch;
}




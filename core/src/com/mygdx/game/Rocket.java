package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;

import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Rocket {

  public static float rocketAngle(Rocket rocket) {
    Vector2 pos = rocket.body.getPosition();
    Vector2 localPoint = new Vector2(pos.x + 0.5f * rocket.sprite.getWidth(),
                                     pos.y - 0.5f * rocket.sprite.getHeight());
    Vector2 localVector = rocket.body.getLocalPoint(localPoint);
    float angle = -1 * (float) Math.toRadians(localVector.angle());
    return angle;
  }

  public static Rocket boost(Rocket rocket) {
    rocket.time += Gdx.graphics.getDeltaTime();
    float addedvel = rocket.power;
    float angle = rocketAngle(rocket);
    Vector2 vel = rocket.body.getLinearVelocity();
    Vector2 impulse = new Vector2(addedvel * (float) Math.cos(angle),
                                  addedvel * (float) Math.sin(angle));
    rocket.body.setLinearVelocity(vel.add(impulse));
    return rocket;
  }

  public static Vector2 bodyToSpritePosition(Rocket rocket) {
    float sx = rocket.body.getPosition().x - 0.5f * rocket.sprite.getWidth();
    float sy = rocket.body.getPosition().y - 0.5f * rocket.sprite.getHeight();
    return new Vector2(sx, sy);
  }

  public static float bodyToSpriteRotation(Rocket rocket) {
    return (float) Math.toDegrees(rocket.body.getAngle());
  }

  /* Draw the rocket. */
  public static void draw(SpriteBatch batch, Rocket rocket) {
    /* Set the sprite position. */
    Vector2 spritepos = bodyToSpritePosition(rocket);
    float spriterot = bodyToSpriteRotation(rocket);
    rocket.sprite.setPosition(spritepos.x, spritepos.y);
    rocket.sprite.setRotation(spriterot);
    /* Do the batch render. */
    batch.setColor(rocket.red, rocket.green, rocket.blue, rocket.alpha);
    TextureRegion frame = rocket.animation.getKeyFrame(rocket.time, true);
    batch.draw(frame,
               rocket.sprite.getX(),
               rocket.sprite.getY(),
               rocket.sprite.getOriginX(),
               rocket.sprite.getOriginY(),
               rocket.sprite.getWidth(),
               rocket.sprite.getHeight(),
               rocket.sprite.getScaleX(),
               rocket.sprite.getScaleY(),
               rocket.sprite.getRotation());
  }

  public Rocket(Texture sheet, World world) {
    /* Load in rocket animation as strips from a single sheet of all possible textures. */
    TextureRegion[][] chopped =
      TextureRegion.split(sheet,
                          sheet.getWidth() / FRAME_COLS,
                          sheet.getHeight() / FRAME_ROWS);
    TextureRegion[] frames = new TextureRegion[FRAME_ROWS * FRAME_COLS];
    for (int i = 0; i < FRAME_ROWS; i++) {
      for (int j = 0; j < FRAME_COLS; j++) {
        frames[i * FRAME_COLS + j] = chopped[i][j];
      }
    }
    animation = new Animation<TextureRegion>(0.025f, frames);
    time = 0;
    /* Create the rocket sprite. */
    sprite = new Sprite(frames[0]);
    /* Initialize the rocket body. */
    BodyDef bodyDef = new BodyDef();
    bodyDef.type = BodyDef.BodyType.DynamicBody;
    bodyDef.position.set((sprite.getX()+sprite.getWidth()/2), (sprite.getY()+sprite.getHeight()/2));
    body = world.createBody(bodyDef);
    body.setGravityScale(1);
    body.setFixedRotation(true);
//    body.setAngularDamping(2 * (float) airResistance);
    power = 100000;
  }

  /* Position and rotation state. */
  public Sprite sprite;
  public Body body;

  public static final float red = 1.0f;
  public static final float green = 1.0f;
  public static final float blue = 1.0f;
  public static final float alpha = 1.0f;

  /* Rocket upgrade stats. */
  //public Vector2 launchVel;
  public float power;

  /* Rocket's local time (which is a thing that exists for some reason?) */
  public float time;

  /* Rocket's animation. */
  public static final int FRAME_ROWS = 1;
  public static final int FRAME_COLS = 4;
  public Animation<TextureRegion> animation;

}


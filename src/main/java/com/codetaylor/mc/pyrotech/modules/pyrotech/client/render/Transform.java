package com.codetaylor.mc.pyrotech.modules.pyrotech.client.render;

import com.codetaylor.mc.athenaeum.util.QuaternionHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.util.vector.Quaternion;

public class Transform {

  public static final Transform IDENTITY = new Transform(
      new Vec3d(0, 0, 0),
      new Quaternion(),
      new Vec3d(1, 1, 1)
  );

  public static final Quaternion NO_ROTATION = new Quaternion();

  public static Vec3d translate(double x, double y, double z) {

    return new Vec3d(x, y, z);
  }

  public static Quaternion rotate(double x, double y, double z, double angle) {

    return QuaternionHelper.setFromAxisAngle(new Quaternion(), (float) x, (float) y, (float) z, (float) Math.toRadians(angle));
  }

  public static Quaternion rotate() {

    return NO_ROTATION;
  }

  public static Vec3d scale(double x, double y, double z) {

    return new Vec3d(x, y, z);
  }

  public final Vec3d translation;
  public final Quaternion rotation;
  public final Vec3d scale;

  public Transform(Vec3d translation, Quaternion rotation, Vec3d scale) {

    this.translation = translation;
    this.rotation = rotation;
    this.scale = scale;
  }
}
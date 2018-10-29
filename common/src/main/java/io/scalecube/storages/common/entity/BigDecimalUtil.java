package io.scalecube.storages.common.entity;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.math.BigDecimal;
import java.math.BigInteger;

public final class BigDecimalUtil {

  public static void writeObject(BigDecimal value, ObjectOutput out) throws IOException {
    BigInteger bigInteger = value.unscaledValue();
    out.writeInt(value.scale());
    if (bigInteger.bitLength() > 63) { // put as a bytes' array
      byte[] bytes = bigInteger.toByteArray();
      out.writeInt(bytes.length);
      out.write(bytes);
    } else { // put as a long value
      out.writeInt(0);
      out.writeLong(bigInteger.longValue());
    }
  }

  public static BigDecimal readObject(ObjectInput in) throws IOException {
    int scale = in.readInt();
    int bytesLength = in.readInt();
    if (bytesLength == 0) { // value as a long
      long unscaledValue = in.readLong();
      return BigDecimal.valueOf(unscaledValue, scale);
    }
    // value as a bytes' array
    byte[] bytes = new byte[bytesLength];
    in.read(bytes);
    return new BigDecimal(new BigInteger(bytes), scale);
  }
}

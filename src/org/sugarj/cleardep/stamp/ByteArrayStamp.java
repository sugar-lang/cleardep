package org.sugarj.cleardep.stamp;

import java.util.Arrays;

public class ByteArrayStamp implements Stamp {
  private static final long serialVersionUID = 3600975022352030761L;
  
  public final Stamper stamper;
  public final byte[] val;
  
  public ByteArrayStamp(Stamper stamper, byte[] val) {
    this.stamper = stamper;
    this.val = val;
  }
  
  @Override
  public Stamper getStamper() {
    return stamper;
  }
  
  @Override
  public boolean equals(Object o) {
    return o instanceof ByteArrayStamp && Arrays.equals(val, ((ByteArrayStamp) o).val);
  }

}

package org.sugarj.cleardep.stamp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.sugarj.common.FileCommands;
import org.sugarj.common.path.Path;

/**
 * @author Sebastian Erdweg
 */
public class FileContentStamper implements Stamper {

  private static final long serialVersionUID = 7688772212399111636L;

  private FileContentStamper() {}
  public static final Stamper instance = new FileContentStamper();
  
  /**
   * @see org.sugarj.cleardep.stamp.Stamper#stampOf(org.sugarj.common.path.Path)
   */
  @Override
  public Stamp stampOf(Path p) {
    if (!FileCommands.exists(p))
      return new ValueStamp<>(this, new byte[0]);
    
    if (p.getFile().isDirectory()) {
      Map<Path, Stamp> stamps = new HashMap<>();
      stamps.put(p, new ValueStamp<>(this, p.getFile().lastModified()));
      
      for (Path sub : FileCommands.listFilesRecursive(p))
        if (sub.getFile().isDirectory())
          stamps.put(sub, new ValueStamp<>(this, sub.getFile().lastModified()));
        else
          stamps.put(sub, fileContentStamp(sub));
      
      return new ValueStamp<>(this, stamps);
    }
    
    return fileContentStamp(p);
  }

  private Stamp fileContentStamp(Path p) {
    try {
      return new ByteArrayStamp(this, FileCommands.readFileAsByteArray(p));
    } catch (IOException e) {
      e.printStackTrace();
      return new ByteArrayStamp(this, null);
    }
  }
}

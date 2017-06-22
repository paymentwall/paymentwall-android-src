package glide.load.resource.file;

import glide.load.resource.SimpleResource;
import java.io.File;

/**
 * A simple {@link glide.load.engine.Resource} that wraps a {@link File}.
 */
public class FileResource extends SimpleResource<File> {
  public FileResource(File file) {
    super(file);
  }
}

package glide.load.resource.file;

import glide.load.Options;
import glide.load.ResourceDecoder;
import glide.load.engine.Resource;
import java.io.File;

/**
 * A simple {@link glide.load.ResourceDecoder} that creates resource for a given {@link
 * File}.
 */
public class FileDecoder implements ResourceDecoder<File, File> {

  @Override
  public boolean handles(File source, Options options) {
    return true;
  }

  @Override
  public Resource<File> decode(File source, int width, int height, Options options) {
    return new FileResource(source);
  }
}

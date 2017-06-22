package glide.load.resource.transcode;

import glide.load.engine.Resource;
import glide.load.resource.bytes.BytesResource;
import glide.load.resource.gif.GifDrawable;
import glide.util.ByteBufferUtil;
import java.nio.ByteBuffer;

/**
 * An {@link glide.load.resource.transcode.ResourceTranscoder} that converts {@link
 * glide.load.resource.gif.GifDrawable} into bytes by obtaining the original bytes of
 * the GIF from the {@link glide.load.resource.gif.GifDrawable}.
 */
public class GifDrawableBytesTranscoder implements ResourceTranscoder<GifDrawable, byte[]> {
  @Override
  public Resource<byte[]> transcode(Resource<GifDrawable> toTranscode) {
    GifDrawable gifData = toTranscode.get();
    ByteBuffer byteBuffer = gifData.getBuffer();
    return new BytesResource(ByteBufferUtil.toBytes(byteBuffer));
  }
}

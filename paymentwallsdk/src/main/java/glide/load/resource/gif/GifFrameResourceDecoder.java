package glide.load.resource.gif;

import android.graphics.Bitmap;
import glide.gifdecoder.GifDecoder;
import glide.load.Options;
import glide.load.ResourceDecoder;
import glide.load.engine.Resource;
import glide.load.engine.bitmap_recycle.BitmapPool;
import glide.load.resource.bitmap.BitmapResource;

/**
 * Decodes {@link Bitmap}s from {@link GifDecoder}s representing a particular frame of a particular
 * GIF image.
 */
public final class GifFrameResourceDecoder implements ResourceDecoder<GifDecoder, Bitmap> {
  private final BitmapPool bitmapPool;

  public GifFrameResourceDecoder(BitmapPool bitmapPool) {
    this.bitmapPool = bitmapPool;
  }

  @Override
  public boolean handles(GifDecoder source, Options options) {
    return true;
  }

  @Override
  public Resource<Bitmap> decode(GifDecoder source, int width, int height,
      Options options) {
    Bitmap bitmap = source.getNextFrame();
    return BitmapResource.obtain(bitmap, bitmapPool);
  }
}

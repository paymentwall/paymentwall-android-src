package glide.load.resource.transcode;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import glide.Glide;
import glide.load.engine.Resource;
import glide.load.engine.bitmap_recycle.BitmapPool;
import glide.load.resource.bitmap.LazyBitmapDrawableResource;
import glide.util.Preconditions;

/**
 * An {@link glide.load.resource.transcode.ResourceTranscoder} that converts {@link
 * Bitmap}s into {@link BitmapDrawable}s.
 */
public class BitmapDrawableTranscoder implements ResourceTranscoder<Bitmap, BitmapDrawable> {
  private final Resources resources;
  private final BitmapPool bitmapPool;

  public BitmapDrawableTranscoder(Context context) {
    this(context.getResources(), Glide.get(context).getBitmapPool());
  }

  public BitmapDrawableTranscoder(Resources resources, BitmapPool bitmapPool) {
    this.resources = Preconditions.checkNotNull(resources);
    this.bitmapPool = Preconditions.checkNotNull(bitmapPool);
  }

  @Override
  public Resource<BitmapDrawable> transcode(Resource<Bitmap> toTranscode) {
    return LazyBitmapDrawableResource.obtain(resources, bitmapPool, toTranscode.get());
  }
}

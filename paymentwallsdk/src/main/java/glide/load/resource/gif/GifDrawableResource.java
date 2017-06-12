package glide.load.resource.gif;

import glide.load.resource.drawable.DrawableResource;

/**
 * A resource wrapping an {@link glide.load.resource.gif.GifDrawable}.
 */
public class GifDrawableResource extends DrawableResource<GifDrawable> {
  public GifDrawableResource(GifDrawable drawable) {
    super(drawable);
  }

  @Override
  public Class<GifDrawable> getResourceClass() {
    return GifDrawable.class;
  }

  @Override
  public int getSize() {
   return drawable.getSize();
  }

  @Override
  public void recycle() {
    drawable.stop();
    drawable.recycle();
  }
}

package glide.request.target;

import android.graphics.drawable.Drawable;
import glide.request.Request;

/**
 * A base {@link Target} for loading {@link glide.load.engine.Resource}s that provides
 * basic or empty implementations for most methods.
 *
 * <p> For maximum efficiency, clear this target when you have finished using or displaying the
 * {@link glide.load.engine.Resource} loaded into it using
 * {@link glide.RequestManager#clear(Target)}.</p>
 *
 * <p> For loading {@link glide.load.engine.Resource}s into {@link android.view.View}s,
 * {@link glide.request.target.ViewTarget} or
 * {@link glide.request.target.ImageViewTarget} are preferable.</p>
 *
 * @param <Z> The type of resource that will be received by this target.
 */
public abstract class BaseTarget<Z> implements Target<Z> {

  private Request request;

  @Override
  public void setRequest(Request request) {
    this.request = request;
  }

  @Override
  public Request getRequest() {
    return request;
  }

  @Override
  public void onLoadCleared(Drawable placeholder) {
    // Do nothing.
  }

  @Override
  public void onLoadStarted(Drawable placeholder) {
    // Do nothing.
  }

  @Override
  public void onLoadFailed(Drawable errorDrawable) {
    // Do nothing.
  }

  @Override
  public void onStart() {
    // Do nothing.
  }

  @Override
  public void onStop() {
    // Do nothing.
  }

  @Override
  public void onDestroy() {
    // Do nothing.
  }
}

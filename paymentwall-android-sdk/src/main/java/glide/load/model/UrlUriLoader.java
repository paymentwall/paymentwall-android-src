package glide.load.model;

import android.net.Uri;
import glide.load.Options;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Handles http/https Uris by delegating to the {@link ModelLoader} for {@link
 * glide.load.model.GlideUrl GlideUrls}.
 *
 * @param <Data> The type of data this Loader will obtain for a {@link Uri}.
 */
public class UrlUriLoader<Data> implements ModelLoader<Uri, Data> {
  private static final Set<String> SCHEMES = Collections.unmodifiableSet(
      new HashSet<>(
          Arrays.asList(
              "http",
              "https"
          )
      )
  );
  private final ModelLoader<GlideUrl, Data> urlLoader;

  public UrlUriLoader(ModelLoader<GlideUrl, Data> urlLoader) {
    this.urlLoader = urlLoader;
  }

  @Override
  public LoadData<Data> buildLoadData(Uri uri, int width, int height, Options options) {
    GlideUrl glideUrl = new GlideUrl(uri.toString());
    return urlLoader.buildLoadData(glideUrl, width, height, options);
  }

  @Override
  public boolean handles(Uri uri) {
    return SCHEMES.contains(uri.getScheme());
  }

  /**
   * Loads {@link InputStream InputStreams} from {@link Uri Uris} with http
   * or https schemes.
   */
  public static class StreamFactory implements ModelLoaderFactory<Uri, InputStream> {

    @Override
    public ModelLoader<Uri, InputStream> build(MultiModelLoaderFactory multiFactory) {
      return new UrlUriLoader<>(multiFactory.build(GlideUrl.class, InputStream.class));
    }

    @Override
    public void teardown() {
      // Do nothing.
    }
  }
}

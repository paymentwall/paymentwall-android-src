package glide.load.engine;

import glide.load.Key;
import glide.load.Options;
import glide.load.Transformation;
import java.util.Map;

class EngineKeyFactory {

  @SuppressWarnings("rawtypes")
  public EngineKey buildKey(Object model, Key signature, int width, int height,
      Map<Class<?>, Transformation<?>> transformations, Class<?> resourceClass,
      Class<?> transcodeClass, Options options) {
    return new EngineKey(model, signature, width, height, transformations, resourceClass,
        transcodeClass, options);
  }
}

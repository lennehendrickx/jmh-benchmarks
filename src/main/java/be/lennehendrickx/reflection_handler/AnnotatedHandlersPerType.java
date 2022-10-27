package be.lennehendrickx.reflection_handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.empty;

public class AnnotatedHandlersPerType {

  private final Map<Class<?>, AnnotatedHandlers> handlersPerType = new ConcurrentHashMap<>();

  public AnnotatedHandlers registerHandlersForType(
      Class<?> handlerType, Class<? extends Annotation> annotation) {
    return handlersPerType.computeIfAbsent(
        handlerType, type -> new AnnotatedHandlers(type, annotation));
  }

  public static class AnnotatedHandlers {
    private final Collection<AnnotatedHandler> handlers;

    private AnnotatedHandlers(Class<?> handlerType, Class<? extends Annotation> annotation) {
      handlers = toEventHandlers(handlerType, annotation);
    }

    public void handle(Object handler, Object parameter) {
      handlers.forEach(annotatedHandler -> annotatedHandler.invoke(handler, parameter));
    }

    private static List<AnnotatedHandler> toEventHandlers(
        Class<?> handlerType, Class<? extends Annotation> annotation) {
      return getMethods(handlerType)
          .filter(method -> method.isAnnotationPresent(annotation))
          .map(method -> AnnotatedHandler.annotatedHandler(method, annotation))
          .toList();
    }

    private static Stream<Method> getMethods(Class<?> handlerType) {
      if (handlerType.equals(Object.class)) {
        return empty();
      }

      return concat(
          getMethods(handlerType.getSuperclass()), stream(handlerType.getDeclaredMethods()));
    }
  }

  static class AnnotatedHandler {
    private final Class<?> parameterType;

    private final Method handlerMethod;

    static AnnotatedHandler annotatedHandler(
        Method method, Class<? extends Annotation> annotation) {
      return new AnnotatedHandler(method, annotation);
    }

    private AnnotatedHandler(Method method, Class<? extends Annotation> annotation) {
      assureOnlyOneParameter(method, annotation);
      assureIsPrivateMethod(method, annotation);
      this.parameterType = method.getParameterTypes()[0];
      this.handlerMethod = setAccessible(method);
    }

    void invoke(Object handler, Object parameter) {
      if (handles(parameter)) {
        invokeMethod(parameter, handler, handlerMethod);
      }
    }

    boolean handles(Object event) {
      return parameterType.isInstance(event);
    }

    private static void assureOnlyOneParameter(
        Method method, Class<? extends Annotation> annotation) {
      if (method.getParameterCount() != 1) {
        throw new IllegalStateException(
            "Methods with @"
                + annotation.getSimpleName()
                + " can have only one parameter: "
                + method);
      }
    }

    private static void assureIsPrivateMethod(
        Method method, Class<? extends Annotation> annotation) {
      if (!Modifier.isPrivate(method.getModifiers())) {
        throw new IllegalStateException(
            "@" + annotation.getSimpleName() + " can only be used on private methods: " + method);
      }
    }

    private void invokeMethod(Object parameter, Object handler, Method method) {
      try {
        method.invoke(handler, parameter);
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new RuntimeException(e);
      }
    }

    private static Method setAccessible(Method method) {
      method.setAccessible(true);
      return method;
    }
  }
}

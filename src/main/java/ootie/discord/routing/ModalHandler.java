package ootie.discord.routing;

import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Repeatable(ManyModalHandlers.class)
@Target(ElementType.METHOD)
public @interface ModalHandler {
    String value();

    boolean save() default true;
}

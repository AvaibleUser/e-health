package org.ehealth.ward.util;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@Target(TYPE)
@Retention(RUNTIME)
@ActiveProfiles("test")
@Inherited
@Documented
@WebMvcTest
@ContextConfiguration
public @interface ControllerTest {

    @AliasFor(annotation = WebMvcTest.class, attribute = "controllers")
    Class<?>[] controllers() default {};

    @AliasFor(annotation = ContextConfiguration.class, attribute = "classes")
    Class<?>[] classes() default {};
}

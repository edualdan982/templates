package com.farmacia.util.validacion;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = NroDocValidador.class)
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface NroDoc {

	String message() default "El numero de documento debe ser distinto de 0";

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };
}

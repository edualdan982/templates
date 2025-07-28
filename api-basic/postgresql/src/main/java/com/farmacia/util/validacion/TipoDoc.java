package com.farmacia.util.validacion;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = TipoDocValidador.class)
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface TipoDoc {

	String message() default "Este solo puede tomar valores de CI o PAS.";

	Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };
}

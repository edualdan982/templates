package edual.auth.validators;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = MatchStringsValidator.class)
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
/**
 * Description: Este validador hace coincidencia exacta con los valores que se
 * proporciona.
 * 
 * @author Edual Dan
 *         17 abr. 2023 16:31:05
 *
 */
public @interface MatchStrings {

  String message() default "Las entradas no son validas";

  String[] fieldMatch() default {};

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}

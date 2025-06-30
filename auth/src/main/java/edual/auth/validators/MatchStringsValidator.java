package edual.auth.validators;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 
 * @author Edual Dan
 *         17 abr. 2023 16:31:19
 *
 */
public class MatchStringsValidator implements ConstraintValidator<MatchStrings, String> {

  private String[] fieldMatch;

  @Override
  public void initialize(MatchStrings constraint) {
    this.fieldMatch = constraint.fieldMatch();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    boolean match = false;
    String msg = "";
    context.disableDefaultConstraintViolation();
    if (value != null) {
      Set<String> auxMatches = new HashSet<>(Arrays.asList(fieldMatch));
      if (value.isEmpty())
        msg = "El valor es vacio, no aceptan espacios en blanco";
      else {
        String[] aux = value.split(",");
        for (int i = 0; i < aux.length; i++) {
          match = auxMatches.contains(aux[i].trim());
          if (match)
            auxMatches.remove(aux[i]);
        }
        if (!match)
          msg = "El valor solo puede ser separados por coma los siguientes: " + Arrays.toString(fieldMatch)
              + ", sin repetirse";
      }
    } else
      msg = "El valor es nulo";
    context.buildConstraintViolationWithTemplate(msg).addConstraintViolation();
    return match;
  }

}

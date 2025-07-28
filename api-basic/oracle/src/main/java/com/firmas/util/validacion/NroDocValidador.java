package com.firmas.util.validacion;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NroDocValidador implements ConstraintValidator<NroDoc, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if(value == null)
			return false;
		if (value.equals("0"))
			return false;
		else
			return true;
	}

}

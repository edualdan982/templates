package com.farmacia.util.validacion;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TipoDocValidador implements ConstraintValidator<TipoDoc, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return value != null && (value.equals("CI") || value.equals("PAS"));
	}

}

package com.farmacia.util.proceso;

import java.text.DecimalFormat;

public class Redondeo {

	private Redondeo() {

	}

	public static Double redondear(Object obj) {
		DecimalFormat redondeo = new DecimalFormat("########.##");
		return Double.parseDouble(redondeo.format(obj));
	}

	public static Double redondear(Double d) {
		if (d == null)
			return 0.0;
		return Math.round(d * 100.0) / 100.0;
	}

	public static double redondeoBanquero(double valor) {
		// Redondea al número más cercano
		double redondeado = Math.round(valor);
		// Diferencia entre el valor original y el redondeado
		double diferencia = Math.abs(valor - redondeado);

		// Verificar si la diferencia es exactamente 0.5
		// Si el redondeado es impar, ajustar al par más cercano
		if (diferencia == 0.5 && redondeado % 2 != 0)
			redondeado = redondeado + (valor > redondeado ? 1 : -1);

		return redondeado;
	}
}

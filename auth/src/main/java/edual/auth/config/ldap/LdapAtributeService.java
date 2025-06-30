package edual.auth.config.ldap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.springframework.stereotype.Service;

@Service
public class LdapAtributeService implements ILdapAtributeService {

	@Override
	public Date convertirDateTime(String dataTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss.S'Z'");
		LocalDateTime dateTime = LocalDateTime.parse(dataTime, formatter);
		return Date.from(dateTime.toInstant(ZoneOffset.UTC));
	}

	@Override
	public Date convertirFecha(String fecha) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return format.parse(fecha);
		} catch (ParseException e) {
			return null;
		}
	}

	@Override
	public long convertirLong(String data) {
		return Long.parseLong(data);
	}

	@Override
	public int convertirInteger(String data) {
		return Integer.parseInt(data);
	}

	@Override
	public Date convertirTime(String time) {
		if (time == null)
			return null;
		long accountExpires = Long.parseLong(time);
		try {
			return new java.util.Date((accountExpires - 116444736000000000L) / 10000);
		} catch (Exception e) {
			return null;
		}
	}

}

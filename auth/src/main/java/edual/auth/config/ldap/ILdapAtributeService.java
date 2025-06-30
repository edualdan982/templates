package edual.auth.config.ldap;

import java.util.Date;

public interface ILdapAtributeService {
	
	public Date convertirDateTime(String dataTime);
	
	public Date convertirFecha(String fecha);
	
	public long convertirLong(String data);
	
	public int convertirInteger(String data);

	public Date convertirTime(String time);
}

package edual.auth.config.ldap;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class UserLdap implements Serializable {

  private String userAccountControl;
  /**
   * Fecha y Hora cuando la cuenta expira
   */
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private Date accountExpires;
  /**
   * Correo personal del usuario
   */
  private String correo;
  /**
   * Código de la ciudad
   */
  private Integer countryCode;
  /**
   * Descripcion del usuario
   */
  private String description;

  /**
   * Fecha de nacimiento, puede tener o no
   */
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private Date fechaNacimiento;

  /**
   * Nombres de la persona a la que pertenece la cuenta
   */
  private String givenName;

  /**
   * Telefono fijo, puede tener o no
   */
  private String homePhone;

  /**
   * Correo institucional a la que pertenece la cuenta
   */
  private String mail;

  /**
   * Apellidos de la persona
   */
  private String sn;
  /**
   * Telefono celular de la persona a la que pertenece la cuenta
   */
  private String mobile;

  /**
   * Direccion de la persona a la que pertenece la cuenta
   */
  private String streetAddress;

  /**
   * Numero de carnet de identidad de la persona a la que pertenece la cuenta No
   * se como se maneja a las personas que tiene complemento
   */
  private String uid;

  /**
   * Fecha y hora de cuando se cambio la cuenta
   */
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private Date whenChanged;
  /**
   * Fecha y hora de cuando se creo la cuenta
   */
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private Date whenCreated;

  /**
   * Nombre de usuario institucional
   */
  private String samAccountName;
  /**
   * Apellido Materno de la persona
   */
  private String snMaternal;
  /**
   * Apellido Paterno de la persona
   */
  private String snPaternal;

  public UserLdap() {
    this.whenCreated = new Date();
  }

  public UserLdap(String userAccountControl, Date accountExpires, String correo, Integer countryCode,
      String description, Date fechaNacimiento, String givenName, String homePhone, String mail, String sn,
      String mobile, String streetAddress, String uid, Date whenChanged, Date whenCreated, String samAccountName,
      String snMaternal, String snPaternal) {
    this.userAccountControl = userAccountControl;
    this.accountExpires = accountExpires;
    this.correo = correo;
    this.countryCode = countryCode;
    this.description = description;
    this.fechaNacimiento = fechaNacimiento;
    this.givenName = givenName;
    this.homePhone = homePhone;
    this.mail = mail;
    this.sn = sn;
    this.mobile = mobile;
    this.streetAddress = streetAddress;
    this.uid = uid;
    this.whenChanged = whenChanged;
    this.whenCreated = whenCreated;
    this.samAccountName = samAccountName;
    this.snMaternal = snMaternal;
    this.snPaternal = snPaternal;
  }

  public String getUserAccountControl() {
    return userAccountControl;
  }

  public void setUserAccountControl(String userAccountControl) {
    this.userAccountControl = userAccountControl;
  }

  public Date getAccountExpires() {
    return accountExpires;
  }

  public void setAccountExpires(Date accountExpirationDate) {
    this.accountExpires = accountExpirationDate;
  }

  public String getCorreo() {
    return correo;
  }

  public void setCorreo(String correo) {
    this.correo = correo;
  }

  public Integer getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(Integer countryCode) {
    this.countryCode = countryCode;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Date getFechaNacimiento() {
    return fechaNacimiento;
  }

  public void setFechaNacimiento(Date fechaNacimiento) {
    this.fechaNacimiento = fechaNacimiento;
  }

  public String getGivenName() {
    return givenName;
  }

  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }

  public String getHomePhone() {
    return homePhone;
  }

  public void setHomePhone(String homePhone) {
    this.homePhone = homePhone;
  }

  public String getMail() {
    return mail;
  }

  public void setMail(String mail) {
    this.mail = mail;
  }

  public String getSn() {
    return sn;
  }

  public void setSn(String sn) {
    this.sn = sn;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getStreetAddress() {
    return streetAddress;
  }

  public void setStreetAddress(String streetAddress) {
    this.streetAddress = streetAddress;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public Date getWhenChanged() {
    return whenChanged;
  }

  public void setWhenChanged(Date whenChanged) {
    this.whenChanged = whenChanged;
  }

  public Date getWhenCreated() {
    return whenCreated;
  }

  public void setWhenCreated(Date whenCreated) {
    this.whenCreated = whenCreated;
  }

  public String getSamAccountName() {
    return samAccountName;
  }

  public void setSamAccountName(String samAccountName) {
    this.samAccountName = samAccountName;
  }

  public String getSnMaternal() {
    return snMaternal;
  }

  public void setSnMaternal(String snMaternal) {
    this.snMaternal = snMaternal;
  }

  public String getSnPaternal() {
    return snPaternal;
  }

  public void setSnPaternal(String snPaternal) {
    this.snPaternal = snPaternal;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  private static final long serialVersionUID = 1L;
}
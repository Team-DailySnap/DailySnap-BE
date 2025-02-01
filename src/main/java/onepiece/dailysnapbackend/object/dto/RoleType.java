package onepiece.dailysnapbackend.object.dto;

public enum RoleType {
  USER("USER"),
  GUEST("GUEST"),
  ADMIN("ADMIN");

  RoleType(String user) {

  }

  public String getValue() {
    return this.name();
  }
}

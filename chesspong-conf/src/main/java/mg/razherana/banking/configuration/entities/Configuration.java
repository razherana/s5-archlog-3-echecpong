package mg.razherana.banking.configuration.entities;

import java.io.Serializable;

import jakarta.persistence.*;

@Entity
@Table(name = "configurations")
public class Configuration implements Serializable {
  /**
   * Unique identifier for the user.
   * Auto-generated using database identity strategy.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * Cannot be null.
   */
  @Column(nullable = false)
  private String name;

  /**
   * Cannot be null.
   */
  @Column(nullable = false)
  private String value;

  /**
   * Returns a string representation of the user.
   * The password is excluded for security reasons.
   * 
   * @return a string representation containing id, name, and email
   */
  @Override
  public String toString() {
    return "Configurations{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", value='" + value + '\'' +
        '}';
  }

  /**
   * @return the id
   */
  public Integer getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the value
   */
  public String getValue() {
    return value;
  }

  /**
   * @param value the value to set
   */
  public void setValue(String value) {
    this.value = value;
  }
}

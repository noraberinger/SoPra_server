package ch.uzh.ifi.hase.soprafs22.rest.dto;

import java.util.Date;

public class UserPostDTO {

  private String username;
  private Date creation_date;
  private Long id;
  private boolean logged_in;
  private Date birthday;
  private String password;


  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Date getCreation_date() { return creation_date; }

  public void setCreation_date(Date creation_date) { this.creation_date = creation_date; }

  public Long getId() { return id; }

  public void setId(Long id) { this.id = id; }

  public boolean isLogged_in() { return logged_in; }

  public void setLogged_in(boolean logged_in) { this.logged_in = logged_in; }

  public Date getBirthday() { return birthday; }

  public void setBirthday(Date birthday) { this.birthday = birthday; }

  public String getPassword() { return password; }

  public void setPassword(String password) { this.password = password; }
}


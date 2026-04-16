package com.rigel.app.model;

//import com.app.todoapp.model.User;

//LoginResponse.java
public class ThirdPartyResponse {
 private String code;
 private Data data;
 private String message;
 private String status;

 // Getters and Setters
 public String getCode() { return code; }
 public void setCode(String code) { this.code = code; }

 public Data getData() { return data; }
 public void setData(Data data) { this.data = data; }

 public String getMessage() { return message; }
 public void setMessage(String message) { this.message = message; }

 public String getStatus() { return status; }
 public void setStatus(String status) { this.status = status; }

 // Nested Data class
 public static class Data {
     private String access_token;
     private User user;

     // Getters and Setters
     public String getAccess_token() { return access_token; }
     public void setAccess_token(String access_token) { this.access_token = access_token; }

     public User getUser() { return user; }
     public void setUser(User user) { this.user = user; }
 }

 // Nested User class
// public static class User {
//     private int id;
//     private String name;
//     private String email_id;
//     private String mobile_no;
//     private String country_code;
//     private int status;
//     private String created_at;
//     private String role;
//     private String gender;
//     private String lastPasswordResetDate;
//
//     // Getters and Setters
//     public int getId() { return id; }
//     public void setId(int id) { this.id = id; }
//
//     public String getName() { return name; }
//     public void setName(String name) { this.name = name; }
//
//     public String getEmail_id() { return email_id; }
//     public void setEmail_id(String email_id) { this.email_id = email_id; }
//
//     public String getMobile_no() { return mobile_no; }
//     public void setMobile_no(String mobile_no) { this.mobile_no = mobile_no; }
//
//     public String getCountry_code() { return country_code; }
//     public void setCountry_code(String country_code) { this.country_code = country_code; }
//
//     public int getStatus() { return status; }
//     public void setStatus(int status) { this.status = status; }
//
//     public String getCreated_at() { return created_at; }
//     public void setCreated_at(String created_at) { this.created_at = created_at; }
//
//     public String getRole() { return role; }
//     public void setRole(String role) { this.role = role; }
//
//     public String getGender() { return gender; }
//     public void setGender(String gender) { this.gender = gender; }
//
//     public String getLastPasswordResetDate() { return lastPasswordResetDate; }
//     public void setLastPasswordResetDate(String lastPasswordResetDate) { this.lastPasswordResetDate = lastPasswordResetDate; }
// }
}

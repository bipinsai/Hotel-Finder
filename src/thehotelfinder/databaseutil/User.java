/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thehotelfinder.databaseutil;

/**
 *
 * @author Rohith
 */
public class User {
    //private static int id;
    private String name;
    private String dob;
    private String address[];
    private String email;
    private String username;
    private String password;

    public User(String name, String  dob, String  address[], String  email, String username, String  password){
      //  id++;
        this.name = name;
        this.dob = dob;
        this.address = address;
        this.email = email;
        this.username = username;
        this.password = password;
    }
    
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String[] getAddress() {
        return address;
    }

    public void setAddress(String address[]) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    
}

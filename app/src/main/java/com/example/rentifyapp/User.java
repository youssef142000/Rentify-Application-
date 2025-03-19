package com.example.rentifyapp;
// we should create a subclasses for the lessors and rentors
public class User {
    private String firstName, lastName, email, UserType, Password;
    public User(){

    } // why this is here??

    public User(String firstName, String lastName, String email, String UserType, String Password){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.UserType = UserType;
        this.Password = Password;
    }

    public String getEmail() { return email; }
    public String getUserType() { return UserType; }
    public  String getFirstname(){
        return firstName;
    }
    public String getLastname(){
        return lastName;
    }

    public String getFullName(){
        return firstName+" "+ lastName;
    }
public  String getPassword(){
        return Password;
}

}

package com.awatar.mainpage;

public class database_handler {

    private String name;
    private String password;
    private  String phno;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhno() {
        return phno;
    }

    public void setPhno(String phno) {
        this.phno = phno;
    }

    public database_handler(String name, String password, String phno) {
        this.name=name;
        this.password=password;
        this.phno=phno;
    }


}

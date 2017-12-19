package com.knowurcodes.musicplayer;

/**
 * Created by user on 10/14/2017.
 */

public class SignUpDetails
{
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public long getPhone() {
        return phone;
    }

    private String name;
    private String email;
    private long phone;

    public SignUpDetails(String name,
                         String email, long phone)
    {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}

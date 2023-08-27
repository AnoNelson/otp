package com.example.ussd.security;


import com.example.ussd.model.UserCore;
import lombok.Data;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Data
public class AuthenticatedUser {
    private String id;
    private String username;
    protected List<SimpleGrantedAuthority> authorityList;

    public AuthenticatedUser() {
    }

    public AuthenticatedUser(UserCore userCore) {
        this.id = userCore.getId();
        this.username = userCore.getUsername();
        this.authorityList = userCore.getAuthorityList();
    }

    public AuthenticatedUser(String id, String username, List<SimpleGrantedAuthority> authorityList) {
        this.id = id;
        this.username = username;
        this.authorityList = authorityList;
    }

    public boolean hasAuthority(String auth) {
        for (int i = 0; i < authorityList.size(); i++)
            if (authorityList.get(i).getAuthority().equals(auth))
                return true;
        return false;
    }

    @Override
    public String toString() {
        return "UserSimpleDetails{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", authorityList=" + authorityList +
                '}';
    }
}

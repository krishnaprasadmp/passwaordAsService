package com.braincorp.com.passwordasservice.dal;

import com.braincorp.com.passwordasservice.model.Groups;
import com.braincorp.com.passwordasservice.model.Users;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface IDao {

    List<Users> getAllUserdDao();
    List<Groups> getAllGroupDao();
}

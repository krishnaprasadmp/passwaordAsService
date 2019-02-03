package com.braincorp.com.passwordasservice.repos;

import com.braincorp.com.passwordasservice.dal.DaoImpl;
import com.braincorp.com.passwordasservice.model.Groups;
import com.braincorp.com.passwordasservice.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PasswdRepository {

    private DaoImpl dao;

    @Autowired
    PasswdRepository(DaoImpl dao) {
        this.dao = dao;
    }

    public List<Users> getAllUsersRepo() {
        return dao.getAllUserdDao();
    }


    public List<Groups> getAllGroupsRepo() {
        return dao.getAllGroupDao();
    }


}

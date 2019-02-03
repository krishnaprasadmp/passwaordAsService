package com.braincorp.com.passwordasservice.service;

import com.braincorp.com.passwordasservice.model.Groups;
import com.braincorp.com.passwordasservice.model.Users;

import java.util.List;

public interface IPasswdService {

    List<Users> getAllUsersService();

    List<Groups> getGroupForGivenUserService(Users users);

    Users getUserService(Users users);

    List<Groups> getAllGroupsService();

    Groups getGroupService(Groups groups);

    List<Users> getUsersBasedOffInputs(Users userToRetrieve);

    List<Groups> getGroupsBasedOffInputs(Groups groupToRetrieve);
}

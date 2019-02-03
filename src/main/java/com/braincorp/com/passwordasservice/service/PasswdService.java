package com.braincorp.com.passwordasservice.service;

import com.braincorp.com.passwordasservice.model.Groups;
import com.braincorp.com.passwordasservice.model.Users;
import com.braincorp.com.passwordasservice.repos.PasswdRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.braincorp.com.passwordasservice.helper.Constants.DEFAULTVALUELONG;
import static com.braincorp.com.passwordasservice.helper.Constants.DEFAULTVALUESTRING;

@Service
public class PasswdService implements IPasswdService {

    private static final String CLASS_NAME = PasswdService.class.getName();
    private static final Logger logger = LoggerFactory.getLogger(CLASS_NAME);
    private PasswdRepository passwdRepository;
    private List<Users> usersList;
    private List<Groups> groupList;


    @Autowired
    public PasswdService(PasswdRepository passwdRepository) {
        this.passwdRepository = passwdRepository;
    }


    @Override
    public List<Users> getAllUsersService() {
        return passwdRepository.getAllUsersRepo();
    }

    @Override
    public List<Groups> getGroupForGivenUserService(Users users) {
        groupList = passwdRepository.getAllGroupsRepo();
        usersList = passwdRepository.getAllUsersRepo();
        List<Groups> filteredgroups = groupList;
        try {
            Users requestedUser = usersList.stream().filter(x -> x.getUid() == users.getUid()).findAny().orElse(null);
            if(requestedUser == null) {
                return new ArrayList<>();
            }
            filteredgroups = groupList.stream().filter(x -> x.getGid() == requestedUser.getGid()).collect(Collectors.toList());
        } catch (Exception ex) {
            filteredgroups = null;
            logger.error("Error while querying the list of groups for a user for a given uid " + users.getUid(), ex);
        }
        return filteredgroups;
    }

    @Override
    public Users getUserService(Users users) {
        usersList = passwdRepository.getAllUsersRepo();
        Users returnedUser = users;
        try {
            returnedUser = usersList.stream().filter(x -> x.getUid() == users.getUid()).findAny().orElse(null);
        } catch (Exception ex) {
            logger.error("Error while querying user for a given uid :" + users.getUid(), ex);
        }
        return returnedUser;
    }

    @Override
    public List<Groups> getAllGroupsService() {
        return passwdRepository.getAllGroupsRepo();
    }

    @Override
    public Groups getGroupService(Groups groups) {
        groupList = passwdRepository.getAllGroupsRepo();
        Groups returnedGroup = groups;
        try {
            returnedGroup = groupList.stream().filter(x -> x.getGid() == groups.getGid()).findAny().orElse(null);
        } catch (Exception ex) {
            logger.error("Error while querying user for a given uid :" + groups.getGid(), ex);
        }

        return returnedGroup;
    }


    @Override
    public List<Users> getUsersBasedOffInputs(Users userToRetrieve) {
        usersList = passwdRepository.getAllUsersRepo();

        List<Users> filteredUsers = usersList;
        boolean criteriaFound = false;

        try {

            if (userToRetrieve.getUid() != Long.parseLong(DEFAULTVALUELONG)) {
                criteriaFound = true;
                filteredUsers = usersList.stream().filter(x -> x.getUid() == userToRetrieve.getUid()).collect(Collectors.toList());
            }

            if (!userToRetrieve.getName().equals(DEFAULTVALUESTRING)) {
                criteriaFound = true;
                filteredUsers = filteredUsers.stream().filter(x -> x.getName().equalsIgnoreCase(userToRetrieve.getName())).collect(Collectors.toList());
            }

            if (!userToRetrieve.getComment().equals(DEFAULTVALUESTRING)) {
                criteriaFound = true;
                filteredUsers = filteredUsers.stream().filter(x -> x.getComment().equalsIgnoreCase(userToRetrieve.getComment())).collect(Collectors.toList());
            }
            if (!userToRetrieve.getHome().equals(DEFAULTVALUESTRING)) {
                criteriaFound = true;
                filteredUsers = filteredUsers.stream().filter(x -> x.getHome().equalsIgnoreCase(userToRetrieve.getHome())).collect(Collectors.toList());
            }
            if (userToRetrieve.getGid() != Long.parseLong(DEFAULTVALUELONG)) {
                criteriaFound = true;
                filteredUsers = filteredUsers.stream().filter(x -> x.getGid() == userToRetrieve.getGid()).collect(Collectors.toList());
            }
            if (!userToRetrieve.getShell().equals(DEFAULTVALUESTRING)) {
                criteriaFound = true;
                filteredUsers = filteredUsers.stream().filter(x -> x.getShell().equalsIgnoreCase(userToRetrieve.getShell())).collect(Collectors.toList());
            }
        } catch (Exception ex) {
            logger.error("Error ocuured when querying user list with optional parameters :" + userToRetrieve, ex);
        }


        return (criteriaFound) ? filteredUsers : new ArrayList<>();

    }

    @Override
    public List<Groups> getGroupsBasedOffInputs(Groups groupToRetrieve) {
        groupList = passwdRepository.getAllGroupsRepo();

        List<Groups> filteredUsers = groupList;
        boolean criteriaFound = false;

        try {

            if (!groupToRetrieve.getName().equals(DEFAULTVALUESTRING)) {
                criteriaFound = true;
                filteredUsers = filteredUsers.stream().filter(x -> x.getName().equalsIgnoreCase(groupToRetrieve.getName())).collect(Collectors.toList());
            }

            if (groupToRetrieve.getGid() != Long.parseLong(DEFAULTVALUELONG)) {
                criteriaFound = true;
                filteredUsers = filteredUsers.stream().filter(x -> x.getGid() == groupToRetrieve.getGid()).collect(Collectors.toList());
            }
            if (groupToRetrieve.getMembers() != null) {
                criteriaFound = true;
                filteredUsers = filteredUsers.stream().filter(x -> CollectionUtils.containsAny(x.getMembers(), groupToRetrieve.getMembers())).collect(Collectors.toList());
            }
        } catch (Exception ex) {
            logger.error("Error ocuured when querying group list with optional parameters :" + groupToRetrieve, ex);
        }


        return (criteriaFound) ? filteredUsers : new ArrayList<>();
    }

}

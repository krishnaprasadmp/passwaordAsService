package com.braincorp.com.passwordasservice.controller;

import com.braincorp.com.passwordasservice.dal.DaoImpl;
import com.braincorp.com.passwordasservice.model.Groups;
import com.braincorp.com.passwordasservice.model.Users;
import com.braincorp.com.passwordasservice.service.PasswdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.braincorp.com.passwordasservice.helper.Constants.DEFAULTVALUELONG;
import static com.braincorp.com.passwordasservice.helper.Constants.DEFAULTVALUESTRING;

@RestController
@RequestMapping("/api")
public class PasswordAsServiceController {

    private static final String CLASS_NAME = PasswordAsServiceController.class.getName();
    private static Logger logger = LoggerFactory.getLogger(CLASS_NAME);
    @Autowired
    PasswdService passwdService;
    @Autowired
    DaoImpl dao;

    /**
     * Ping for status check
     *
     * @return UTC Time Stamp
     */
    @GetMapping(value = "/ping")
    public ResponseEntity<String> ping() {
        Instant instant = Instant.now();
        return ResponseEntity.ok(instant.toString());
    }

    /**
     * @return a list of users on the system, as defined in /etc/passwd
     */
    @GetMapping(value = "/users")
    public ResponseEntity<List<Users>> getAllUsers() {
        String methodName = "getAllUsers";
        List<Users> usersList = null;
        try {
            usersList = passwdService.getAllUsersService();
        } catch (Exception ex) {
            logger.error(methodName + ": Error occured when trying to fetch all the users ", ex);
            return new ResponseEntity<>(usersList, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<List<Users>>(usersList, HttpStatus.OK);
    }


    /**
     * @param uid
     * @return a single user with uid
     */
    @GetMapping(value = "/users/{uid}")
    public ResponseEntity<Users> getUser(@PathVariable("uid") long uid) {
        String methodName = "getUser";
        Users users = new Users();
        try {
            users.setUid(uid);
            users = passwdService.getUserService(users);
            if (users == null) {
                logger.error(methodName + ": User not found for the given: " + uid);
                return new ResponseEntity<Users>(users, HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            logger.error(methodName+ ": Error occured when trying to fetch the user: " + uid, ex);
            return new ResponseEntity<Users>(users, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Users>(users, HttpStatus.OK);
    }


    /**
     * @return list of all groups on the system, a defined by /etc/group
     */
    @GetMapping(value = "/groups")
    public ResponseEntity<List<Groups>> getAllGroups() {
        String methodName = "getAllGroups";
        List<Groups> groupList = null;
        try {
            groupList = passwdService.getAllGroupsService();
        } catch (Exception ex) {
            logger.error(methodName + ": Error occured when trying to fetch all the groups ", ex);
            return new ResponseEntity<>(groupList, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<List<Groups>>(groupList, HttpStatus.OK);
    }


    /**
     * @param gid
     * @return a single group
     */
    @GetMapping(value = "/groups/{gid}")
    public ResponseEntity<Groups> getGroup(@PathVariable("gid") long gid) {
        String methodName = "getUser";
        Groups groups = new Groups();
        try {
            groups.setGid(gid);
            groups = passwdService.getGroupService(groups);
            if (groups == null) {
                logger.error(methodName + ": Group not found for the given: " + gid);
                return new ResponseEntity<Groups>(groups, HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            logger.error(methodName+ ": Error occured when trying to fetch the group: " + gid, ex);
            return new ResponseEntity<Groups>(groups, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Groups>(groups, HttpStatus.OK);
    }


    /**
     * @param userid
     * @return all the groups for a given user
     */
    @RequestMapping("/users/{userid}/groups")
    public ResponseEntity<List<Groups>> getGroupForGivenUser(@PathVariable("userid") long userid) {
        String methodName = "getUser";
        List<Groups> groups = null;
        Users users = new Users();
        try {
            users.setUid(userid);
            groups = passwdService.getGroupForGivenUserService(users);
            if (groups.isEmpty()) {
                logger.error(methodName+ ": No groups found for a given user: " + userid);
                return new ResponseEntity<List<Groups>>(groups, HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            logger.error(methodName+ ": Error occured when trying to fetch the group for given user: " + userid, ex);
            return new ResponseEntity<List<Groups>>(groups, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<List<Groups>>(groups, HttpStatus.OK);

    }


    /**
     * @param name
     * @param uid
     * @param gid
     * @param comment
     * @param home
     * @param shell
     * @return returns list of users matching all or any of the following field
     */
    @GetMapping("/users/query")
    public ResponseEntity<List<Users>> getFilterListUsers(@RequestParam(required = false, defaultValue = DEFAULTVALUESTRING) String name, @RequestParam(required = false, defaultValue = DEFAULTVALUELONG) long uid,
                                                          @RequestParam(required = false, defaultValue = DEFAULTVALUELONG) long gid, @RequestParam(required = false, defaultValue = DEFAULTVALUESTRING) String comment,
                                                          @RequestParam(required = false, defaultValue = DEFAULTVALUESTRING) String home, @RequestParam(required = false, defaultValue = DEFAULTVALUESTRING) String shell) {

        String methodName ="getFilterListUsers()";
        List<Users> usersList = null;
        try {
            Users users = new Users(name, uid, gid, comment, home, shell);

            usersList = passwdService.getUsersBasedOffInputs(users);

            if (usersList.isEmpty()) {
                logger.error(methodName + ": No groups found for a given users filter query: " + users);
                return new ResponseEntity<List<Users>>(usersList, HttpStatus.NOT_FOUND);
            }
        }
        catch (Exception e) {
            return  new ResponseEntity<List<Users>>(HttpStatus.BAD_REQUEST);
        }


        return new ResponseEntity<List<Users>>(usersList, HttpStatus.OK);
    }


    /**
     * @param name
     * @param gid
     * @param members
     * @return returns list of groups matching all or any of the following field
     */
    @GetMapping("/groups/query")
    public ResponseEntity<List<Groups>> getGroupFilterUsers(@RequestParam(required = false, defaultValue = DEFAULTVALUESTRING) String name, @RequestParam(required = false, defaultValue = "-1") long gid,
                                                            @RequestParam(required = false) ArrayList<String> members) {
        String methodName = "getGroupFilterUsers()";
        List<Groups> groupList = null;
        try {
            Groups groups = new Groups(name, gid, members);

            groupList = passwdService.getGroupsBasedOffInputs(groups);

            if (groupList.isEmpty()) {new ArrayList<>();
                logger.error(methodName + ": No groups found for a given groups filter query: " + groups);
                return new ResponseEntity<List<Groups>>(groupList, HttpStatus.NOT_FOUND);
            }
        }
        catch (Exception e) {
            return  new ResponseEntity<List<Groups>>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<List<Groups>>(groupList, HttpStatus.OK);

    }

}

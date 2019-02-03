package com.braincorp.com.passwordasservice.dal;

import com.braincorp.com.passwordasservice.exception.ResourceNotFoundException;
import com.braincorp.com.passwordasservice.model.Groups;
import com.braincorp.com.passwordasservice.model.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DaoImpl implements IDao {

    private ArrayList<Users> usersList = new ArrayList<>();
    private ArrayList<Groups> groupsList = new ArrayList<>();
    private static final String CLASS_NAME = DaoImpl.class.getName();
    private static final Logger logger = LoggerFactory.getLogger(CLASS_NAME);


    @Override
    public List<Users> getAllUserdDao() {

        return fetchUsersFromTerminal();
    }

    @Override
    public List<Groups> getAllGroupDao() {

        return fetchGroupsFromTerminal();
    }


    private ArrayList<Users> fetchUsersFromTerminal() {
        String methodname = "fetchUsersFromTerminal()";
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("bash", "-c", "cat /etc/passwd");
            Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();
            usersList.clear();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            String[] singleLine = output.toString().split("\n");

            for (String aSingleLine : singleLine) {
                String[] filter = aSingleLine.split(":");
                Users users = new Users(filter[0], Long.parseLong(filter[2]), Long.parseLong(filter[3]), filter[4], filter[5], filter[6]);
                usersList.add(users);
            }
            process.destroy();

        } catch (Exception e) {
          logger.error(methodname + ": Error occured when parsing the terminal output for the user /etc/passwd : ",e);
        }
        return usersList;
    }

    private ArrayList<Groups> fetchGroupsFromTerminal() {
        String methodname = "fetchGroupsFromTerminal()";
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("bash", "-c", "cat /etc/group");
            Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();
            ArrayList<String> membersList = new ArrayList<>();
            groupsList.clear();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            String[] singleLine = output.toString().split("\n");

            for (String aSingleLine : singleLine) {
                membersList.clear();
                String eachLine = aSingleLine;
                String[] filter;
                if (eachLine.endsWith(":")) {
                    filter = eachLine.split(":");
                    Groups groups = new Groups(filter[0], Long.parseLong(filter[2]), membersList);
                    groupsList.add(groups);
                } else if (eachLine.contains(",")) {
                    String[] checkMember = eachLine.split(":");
                    String[] memberFilter = checkMember[3].split(",");
                    Groups groups = new Groups(checkMember[0], Long.parseLong(checkMember[2]), new ArrayList<>(Arrays.asList(memberFilter)));
                    groupsList.add(groups);
                } else {
                    String[] checkMember = eachLine.split(":");
                    Groups groups = new Groups(checkMember[0], Long.parseLong(checkMember[2]), new ArrayList<>(Arrays.asList(checkMember[3])));
                    groupsList.add(groups);
                }

            }
            process.destroy();

        } catch (Exception e) {
            logger.error(methodname + " Error occured when parsing the terminal output for the Groups /etc/group :",e);
        }
        return groupsList;
    }


}


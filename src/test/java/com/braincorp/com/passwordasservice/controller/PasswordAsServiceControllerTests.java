package com.braincorp.com.passwordasservice.controller;


import com.braincorp.com.passwordasservice.model.Groups;
import com.braincorp.com.passwordasservice.model.Users;
import com.braincorp.com.passwordasservice.service.PasswdService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PasswordAsServiceControllerTests {

    @MockBean
    PasswdService passwdService;
    @Autowired
    PasswordAsServiceController passwordAsServiceController;
    @Autowired
    protected WebApplicationContext wac;
    private  List<Users> usersList = new ArrayList<Users>();
    private List<Groups> groupsList = new ArrayList<Groups>();
    Users users;
    Groups groups;
    MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = standaloneSetup(this.passwordAsServiceController).build();
        users = new Users("games",5,60,"games","/usr/games","/usr/sbin/nologin");
        groups = new Groups("avahi",122,new ArrayList<>());
        usersList.add(users);
        groupsList.add(groups);
    }

    @Test
    public void pingRequestTes() {
        ResponseEntity<String> responseEntity = passwordAsServiceController.ping();
        Assert.assertEquals(responseEntity.getStatusCode(),HttpStatus.OK);
    }

    @Test
    public void get_all_users_return_ok() throws Exception{
       when(passwdService.getAllUsersService()).thenReturn(usersList);

       mockMvc.perform(get("/api/users/").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void  get_user_by_id_returns_ok() throws Exception {
        when(passwdService.getUserService(any())).thenReturn(users);
        mockMvc.perform(get("/api/users/5").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.name", is("games")))
                .andExpect(jsonPath("$.uid", is(5)));
    }

    @Test
    public void get_user_by_id_returns_not_found() throws Exception {
        Users users1 = new Users();
        users1.setUid(12);
        when(passwdService.getUserService(users1)).thenReturn(users);
        mockMvc.perform(get("/api/users/12").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void get_users_by_id_return_bad_request() throws Exception {
        when(passwdService.getUserService(any())).thenReturn(users);
        mockMvc.perform(get("/api/users/abc").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }


    @Test
    public void get_all_groups_return_ok() throws Exception{
        when(passwdService.getAllGroupsService()).thenReturn(groupsList);

        mockMvc.perform(get("/api/groups/").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void  get_groups_by_id_returns_ok() throws Exception {
        when(passwdService.getUserService(any())).thenReturn(users);
        mockMvc.perform(get("/api/users/122").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("games")))
                .andExpect(jsonPath("$.uid", is(5)));
    }

    @Test
    public void get_group_by_id_returns_not_found() throws Exception {
        Groups groups1 = new Groups();
        groups1.setGid(12);
        when(passwdService.getGroupService(groups1)).thenReturn(groups);
        mockMvc.perform(get("/api/groups/12").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void get_groups_by_id_return_bad_request() throws Exception {
        when(passwdService.getGroupService(any())).thenReturn(groups);
        mockMvc.perform(get("/api/groups/abc").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }


    @Test
    public void get_groups_based_on_user_id_returns_ok() throws Exception {
        when(passwdService.getGroupForGivenUserService(any())).thenReturn(groupsList);
        mockMvc.perform(get("/api/users/5/groups").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].gid" ,is(122)));
    }


    @Test
    public void get_groups_based_on_user_id_returns_not_found() throws Exception {
        Users users1 = new Users();
        users1.setUid(12);
        when(passwdService.getGroupForGivenUserService(users1)).thenReturn(groupsList);
        mockMvc.perform(get("/api/users/12/groups").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void get_users_for_matching_query_returns_ok() throws Exception {
        when(passwdService.getUsersBasedOffInputs(any())).thenReturn(usersList);
        mockMvc.perform(get("/api/users/query?name=games").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name",is("games")));

    }


    @Test
    public void get_users_for_matching_query_returns_not_found()  throws Exception{
        Users users1 = new Users();
        users.setName("jamie");
        when(passwdService.getUsersBasedOffInputs(users1)).thenReturn(usersList);
        mockMvc.perform(get("/api/users/query?name=jamie").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    public void get_groups_for_matching_query_returns_ok() throws Exception {
        when(passwdService.getGroupsBasedOffInputs(any())).thenReturn(groupsList);
        mockMvc.perform(get("/api/groups/query?name=avahi").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name",is("avahi")));

    }

    @Test
    public void get_groups_for_matching_query_returns_not_found()  throws Exception{
        Groups groups1 = new Groups();
        groups1.setName("jamie");
        when(passwdService.getGroupsBasedOffInputs(groups1)).thenReturn(groupsList);
        mockMvc.perform(get("/api/groups/query?name=jamie").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }



 }
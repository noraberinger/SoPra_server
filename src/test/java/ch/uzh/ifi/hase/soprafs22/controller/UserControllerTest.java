package ch.uzh.ifi.hase.soprafs22.controller;

//import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

  /*@Test
  public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
    // given
    User user = new User();
    //user.setName("Firstname Lastname");
    user.setUsername("testUsername");
    user.setPassword("testPassword");
    user.setLogged_in(true);
    user.setId(1L);
    user.setToken("1");
    user.setCreation_date(new Date());
    user.setBirthday(new Date());
    //user.setStatus(UserStatus.OFFLINE);
    List<User> allUsers = Collections.singletonList(user);
    // this mocks the UserService -> we define above what the userService should
    // return when getUsers() is called
    given(userService.getUsers()).willReturn(allUsers);
    // when
    MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);
    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        //.andExpect(jsonPath("$[0].name", is(user.getName())))
        .andExpect(jsonPath("$[0].id", is(user.getId().intValue())))
        .andExpect(jsonPath("$[0].token", is(user.getToken())))
        .andExpect(jsonPath("$[0].birthday", is(user.getBirthday())))
        .andExpect(jsonPath("$[0].creation_date", is(user.getCreation_date())))
        .andExpect(jsonPath("$[0].logged_in", is(user.getLogged_in())))
        .andExpect(jsonPath("$[0].username", is(user.getUsername())));
  }*/

    @Test
    public void createUser_validInput_userCreated() throws Exception {
        //201 status for POST
        User user = new User();
        user.setId(1L);
        //user.setName("Test User");
        user.setUsername("testUsername");
        user.setPassword("testPassword");
        user.setToken("1");
        user.setLogged_in(true);
        user.setCreation_date(new Date());
        user.setBirthday(new Date());
        //user.setStatus(UserStatus.ONLINE);

        UserPostDTO userPostDTO = new UserPostDTO();
        //userPostDTO.setName("Test User");
        userPostDTO.setUsername("testUsername");
        userPostDTO.setPassword("testPassword");

        given(userService.createUser(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                //.andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.token", is(user.getToken())))
                //.andExpect(jsonPath("$.birthday", is(user.getBirthday())))
                //.andExpect(jsonPath("$.creation_date", is(user.getCreation_date())))
                .andExpect(jsonPath("$.logged_in", is(user.getLogged_in())));
        //.andExpect(jsonPath("$.status", is(user.getStatus().toString())));
    }

    @Test
    public void invalidInputUsername_whenPostUser_thenReturnConflict() throws Exception{
        //Error 409 for POST
        User user = new User();
        user.setUsername("testUsername");
        user.setPassword("testPassword");

        //create a user with same username and password >> shouldn't be allowed
        User user2 = new User();
        user2.setUsername("testUsername");
        user.setPassword("testPassword");

        given(userService.createUser(Mockito.any())).willReturn(user);
        given(userService.createUser(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.CONFLICT));

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user));

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest2 = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user2));

        // then
        mockMvc.perform(postRequest2)
                .andExpect(status().isConflict());
    }

    @Test
    public void validId_whenGetUserId_thenReturnUser() throws Exception {
        //200 status for GET
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setPassword("testPassword");
        user.setLogged_in(true);

        given(userService.getUserById(user.getId())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/users/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON);

        //then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.logged_in", is(user.getLogged_in())));
    }

    @Test
    public void invalidId_whenGetUserId_thenReturnUser() throws Exception {
        //Error 404 for GET
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setPassword("testPassword");
        user.setLogged_in(true);

        given(userService.getUserById(user.getId())).willReturn(user);
        given(userService.getUserById(user.getId()+1)).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder getRequest = get("/users/{id}", user.getId()+1)
                .contentType(MediaType.APPLICATION_JSON);

        //then
        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    public void validInput_whenPutUserId_thenReturnNoContent() throws Exception {
        //204 status update for PUT
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setPassword("testPassword");
        user.setToken("1");
        user.setLogged_in(true);

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword("testUsername");
        userPostDTO.setPassword("testPassword");


        given(userService.createUser(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/users/{id}", user.getId()+1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        //then
        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());
    }

    @Test
    public void invalidId_whenPutUserId_thenReturnNotFound() throws Exception {
        //Error 404 for PUT
        User user = new User();
        user.setId(1L);
        user.setUsername("testUsername");
        user.setPassword("testPassword");
        user.setToken("1");
        user.setLogged_in(true);

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("testUsername");

        Mockito.doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Id not found")).when(userService).updateUser(Mockito.any());

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        //then
        mockMvc.perform(putRequest)
                .andExpect(status().isNotFound());
    }

    /**
     * Helper Method to convert userPostDTO into a JSON string such that the input
     * can be processed
     * Input will look like this: {"name": "Test User", "username": "testUsername"}
     *
     * @param object
     * @return string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }
}
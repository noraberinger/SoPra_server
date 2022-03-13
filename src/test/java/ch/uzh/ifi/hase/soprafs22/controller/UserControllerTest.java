package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

  @Test
  public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
    // given
    User user = new User();
    //user.setName("Firstname Lastname");
    user.setUsername("firstname@lastname");
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
        .andExpect(jsonPath("$[0].id", is(user.getId().longValue())))
        .andExpect(jsonPath("$[0].token", is(user.getToken())))
        .andExpect(jsonPath("$[0].birthday", is(user.getBirthday())))
        .andExpect(jsonPath("$[0].creation_date", is(user.getCreation_date())))
        .andExpect(jsonPath("$[0].logged_in", is(user.getLogged_in())))
        .andExpect(jsonPath("$[0].username", is(user.getUsername())));
  }

  @Test
  public void createUser_validInput_userCreated() throws Exception {
    // given
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
        .andExpect(jsonPath("$.birthday", is(user.getBirthday())))
        .andExpect(jsonPath("$.creation_date", is(user.getCreation_date())))
        .andExpect(jsonPath("$.logged_in", is(user.getLogged_in())));
        //.andExpect(jsonPath("$.status", is(user.getStatus().toString())));
  }

  @Test
  void login_validInput_thenUserLogin() throws Exception {
      // given
      User user = new User();
      user.setId(1L);
      user.setUsername("testUsername");
      user.setPassword("testPassword");
      user.setToken("1");
      user.setLogged_in(true);
      user.setCreation_date(new Date());
      user.setBirthday(new Date());

      UserPostDTO userPostDTO = new UserPostDTO();
      //userPostDTO.setName("Test User");
      userPostDTO.setUsername("testUsername");
      userPostDTO.setPassword("testPassword");

      given(userService.loginUser(Mockito.any())).willReturn(user);

      // when/then -> do the request + validate the result
      MockHttpServletRequestBuilder postRequest = post("/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(userPostDTO));

      // then
      mockMvc.perform(postRequest)
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.logged_in", is(user.getLogged_in())));
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
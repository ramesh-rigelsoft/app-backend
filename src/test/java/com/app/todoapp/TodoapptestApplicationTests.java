package com.app.todoapp;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.aspectj.lang.annotation.Before;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.app.todoapp.model.UsersDetailsData;
import com.rigel.app.model.User;
import com.rigel.app.model.dto.UserDto;
import com.rigel.app.security.JwtTokenUtil;
import com.rigel.app.service.IUserLogOutIn;
import com.rigel.app.service.IUserService;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
class TodoapptestApplicationTests {
	
	
//	@Mock
//	private IUserService userService;
//	
//	@InjectMocks
//	private UsersDetailsData usersDetailsData;
//	
//	@Autowired
//    private MockMvc mockMvc;
//	
//	@BeforeAll
//    public void testUserLogin() throws Exception {
//        User user = new User();
//        Mockito.when(userService.findUserById(user.getId())).thenReturn(user);
//        MvcResult result = mockMvc.perform(post("/api/user/login?username=rameshkumar12111@gmail.com&password=1231"))
//                .andExpect(status().isOk()).andReturn();
//        String jsonResponse = result.getResponse().getContentAsString();
//        String token = com.jayway.jsonpath.JsonPath.read(jsonResponse, "$.data.access_token");
//        usersDetailsData.setToken("Bearer "+token);
//    }
//	
//	@Test
//    public void testTaskList() throws Exception {
//        User user = new User();
//        Mockito.when(userService.findUserById(user.getId())).thenReturn(user);
//        String token=usersDetailsData.getToken();
//        mockMvc.perform(get("/api/todoTask/taskList?startIndex=0&maxPageSize=10")
//        .header("Authorization", token))
//                .andExpect(status().isNotFound());
////                .andExpect(jsonPath("$.data.user.name").value("Ramesh"));
////        usersDetailsData.setToken(jsonPath("$.data.access_token").toString());
//    }


}

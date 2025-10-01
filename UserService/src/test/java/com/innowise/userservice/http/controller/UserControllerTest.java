package com.innowise.userservice.http.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.userservice.dto.UserDto;
import com.innowise.userservice.http.exception.ResourceNotFoundException;
import com.innowise.userservice.service.impl.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private final UserDto sampleUser = new UserDto(
            1L,
            "Darya",
            "Shparko",
            "darya@example.com",
            LocalDate.of(1990, 1, 1),
            List.of()
    );

    private final UserDto createdUser = new UserDto(
            1L,
            "Darya",
            "Shparko",
            "darya@example.com",
            LocalDate.of(1990, 1, 1),
            List.of()
    );

    @Test
    @DisplayName("GET /users/id/{id} should return user by ID")
    void getUserById_shouldReturnUser() throws Exception {
        Mockito.when(userService.findById(1L)).thenReturn(sampleUser);

        mockMvc.perform(get("/api/v1/users?id=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").value("darya@example.com"));
    }

    @Test
    @DisplayName("GET /users/email/{email} should return user by email")
    void getUserByEmail_shouldReturnUser() throws Exception {
        Mockito.when(userService.findByEmail("darya@example.com")).thenReturn(sampleUser);

        mockMvc.perform(get("/api/v1/users?email=darya@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Darya"));
    }

    @Test
    @DisplayName("GET /users should return all users")
    void findAll_shouldReturnList() throws Exception {
        Mockito.when(userService.findAll(Mockito.any(), Mockito.any())).thenReturn(new PageImpl<>(List.of(sampleUser)));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    @DisplayName("GET /users should return 204 if empty")
    void findAll_shouldReturnNoContent() throws Exception {
        Mockito.when(userService.findAll(Mockito.any(), Mockito.any()))
                .thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /users/batch should return users by IDs")
    void findUsersByIds_shouldReturnList() throws Exception {
        Mockito.when(userService.findAll(Mockito.any(), Mockito.any())).thenReturn(new PageImpl<>(List.of(sampleUser)));

        mockMvc.perform(get("/api/v1/users")
                        .param("ids", "1", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").value("darya@example.com"));
    }

    @Test
    @DisplayName("POST /users should create a user")
    void createUser_shouldReturnCreatedUser() throws Exception {
        UserDto request = new UserDto(
                1L,
                "Darya",
                "Shparko",
                "darya@example.com",
                LocalDate.of(1990, 1, 1),
                List.of()
        );

        Mockito.when(userService.create(any())).thenReturn(createdUser);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("PUT /users should update a user")
    void updateUser_shouldReturnOk() throws Exception {
        UserDto request = new UserDto(
                1L,
                "Darya",
                "Shparko",
                "darya@example.com",
                LocalDate.of(1990, 1, 1),
                List.of()
        );

        mockMvc.perform(put("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Mockito.verify(userService).update(request);
    }

    @Test
    @DisplayName("DELETE /users/{id} should delete user")
    void deleteUser_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(userService).delete(1L);
    }

    @Test
    @DisplayName("GET /users/id/{id} should return 404 if user not found")
    void getUserById_shouldReturnNotFound() throws Exception {
        Mockito.when(userService.findById(99L)).thenThrow(new ResourceNotFoundException("User", 99));

        mockMvc.perform(get("/api/v1/users?id=99"))
                .andExpect(status().isNotFound());
    }
}

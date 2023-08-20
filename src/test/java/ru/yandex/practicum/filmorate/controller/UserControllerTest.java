package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void createUserTest() throws Exception {
        mockMvc.perform(
                        post("/users")
                                .content("{\n" +
                                        "  \"login\": \"dolore\",\n" +
                                        "  \"name\": \"Nick Name\",\n" +
                                        "  \"email\": \"mail@mail.ru\",\n" +
                                        "  \"birthday\": \"1946-08-20\"\n" +
                                        "}")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/users")
                                .content("{\n" +
                                        "  \"login\": \"dolore ullamco\",\n" +
                                        "  \"email\": \"yandex@mail.ru\",\n" +
                                        "  \"birthday\": \"2446-08-20\"\n" +
                                        "}")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateUserTest() throws Exception {
        mockMvc.perform(
                        post("/users")
                                .content("{\n" +
                                        "  \"login\": \"dolore\",\n" +
                                        "  \"name\": \"Nick Name\",\n" +
                                        "  \"email\": \"mail@mail.ru\",\n" +
                                        "  \"birthday\": \"1946-08-20\"\n" +
                                        "}")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        put("/users")
                                .content("{\n" +
                                        "  \"login\": \"doloreUpdate\",\n" +
                                        "  \"name\": \"est adipisicing\",\n" +
                                        "  \"id\": 1,\n" +
                                        "  \"email\": \"mail@yandex.ru\",\n" +
                                        "  \"birthday\": \"1976-09-20\"\n" +
                                        "}")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        try {
            mockMvc.perform(
                    put("/users")
                            .content("{\n" +
                                    "  \"login\": \"doloreUpdate\",\n" +
                                    "  \"name\": \"est adipisicing\",\n" +
                                    "  \"id\": 9999,\n" +
                                    "  \"email\": \"mail@yandex.ru\",\n" +
                                    "  \"birthday\": \"1976-09-20\"\n" +
                                    "}")
                            .contentType(MediaType.APPLICATION_JSON)
            );

        } catch (Exception e) {
            assertTrue(e.getCause() instanceof ValidationException);
        }
    }
}
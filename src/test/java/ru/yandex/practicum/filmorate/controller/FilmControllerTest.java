package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void createFilmTest() throws Exception {
        mockMvc.perform(
                        post("/films")
                                .content("{\n" +
                                        "  \"name\": \"nisi eiusmod\",\n" +
                                        "  \"description\": \"adipisicing\",\n" +
                                        "  \"releaseDate\": \"1967-03-25\",\n" +
                                        "  \"duration\": 100\n" +
                                        "}")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/films")
                                .content("{\n" +
                                        "  \"name\": \"\",\n" +
                                        "  \"description\": \"Description\",\n" +
                                        "  \"releaseDate\": \"1900-03-25\",\n" +
                                        "  \"duration\": 200\n" +
                                        "}")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateFilmTest() throws Exception {
        mockMvc.perform(
                        post("/films")
                                .content("{\n" +
                                        "  \"name\": \"nisi eiusmod\",\n" +
                                        "  \"description\": \"adipisicing\",\n" +
                                        "  \"releaseDate\": \"1967-03-25\",\n" +
                                        "  \"duration\": 100\n" +
                                        "}")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        put("/films")
                                .content("{\n" +
                                        "  \"id\": 1,\n" +
                                        "  \"name\": \"Film Updated\",\n" +
                                        "  \"releaseDate\": \"1989-04-17\",\n" +
                                        "  \"description\": \"New film update decription\",\n" +
                                        "  \"duration\": 190,\n" +
                                        "  \"rate\": 4\n" +
                                        "}")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        try{


        mockMvc.perform(
                        put("/films")
                                .content("{\n" +
                                        "    \"timestamp\": \"2023-06-08T21:49:42.303+00:00\",\n" +
                                        "    \"status\": 500,\n" +
                                        "    \"error\": \"Internal Server Error\",\n" +
                                        "    \"path\": \"/films\"\n" +
                                        "}")
                                .contentType(MediaType.APPLICATION_JSON)
                );
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof ValidationException);
        }
    }
}
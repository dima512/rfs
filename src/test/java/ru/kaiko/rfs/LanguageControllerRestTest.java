package ru.kaiko.rfs;

import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(LanguageControllerRest.class)
public class LanguageControllerRestTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private LanguageRepo languageRepo;

    private List<Language> preparedAnswer;

    @Before
    public void setUp() throws Exception {
        preparedAnswer = new ArrayList<>();
        preparedAnswer.add(new Language().setId(1L).setName("scala").setAuthor("odersky")
                .setCurrentVersion("2.12.8").setFeature("functional and oop").setYear(2003));

        preparedAnswer.add(new Language().setId(2L).setName("java").setAuthor("gosling")
                .setCurrentVersion("12").setFeature("oop").setYear(1995));

        preparedAnswer.add(new Language().setId(3L).setName("javascript").setAuthor("eich")
                .setCurrentVersion("ECMAScript 2018").setFeature("frontend").setYear(1995));

        preparedAnswer.add(new Language().setId(4L).setName("elixir").setAuthor("valim")
                .setCurrentVersion("1.8").setFeature("functional and actor model").setYear(2012));
    }

    @Test
    public void getAllShort() throws Exception {
        when(languageRepo.findAll()).thenReturn(this.preparedAnswer);

        String answer = this.mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("scala", "java", "elixir", "javascript")))
                .andReturn().getResponse().getContentAsString();

        assertFalse(answer.contains("\"id\""));
        assertFalse(answer.contains("\"year\""));
        assertFalse(answer.contains("ECMAScript 2018"));
        assertFalse(answer.contains("functional and actor model"));
    }

    @Test
    public void getAllFull() throws Exception {
        when(languageRepo.findAll()).thenReturn(this.preparedAnswer);

        this.mvc.perform(get("/full"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(1, 2, 3, 4)))
                .andExpect(jsonPath("$[3].feature", containsString("functional and actor model")));

        verify(languageRepo, times(1)).findAll();
    }

    @Test
    public void pagination() throws Exception {
        Page<Language> languagePage = Mockito.mock(Page.class);
        when(languageRepo.findAll(PageRequest.of(1, 2))).thenReturn(languagePage);
        when(languagePage.getContent()).thenReturn(this.preparedAnswer.subList(2, 4));

        this.mvc.perform(get("/pagination")
                .param("page", "1")
                .param("size", "2"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].feature").exists())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].name").exists())
                .andExpect(jsonPath("$[1].name").value("elixir"));

        verify(languageRepo, times(1)).findAll(PageRequest.of(1, 2));
        verify(languagePage, times(1)).getContent();
    }

    @Test
    public void getAllByYear() throws Exception {
        when(languageRepo.findAllByYear(1995))
                .thenReturn(this.preparedAnswer.stream().filter(l -> l.getYear() == 1995).collect(Collectors.toList()));

        this.mvc.perform(get("/year/1995"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("java", "javascript")));

        verify(languageRepo, times(1)).findAllByYear(1995);
    }

    @Test
    public void getById() throws Exception {
        when(languageRepo.findById(1L))
                .thenReturn(Optional.of((Language) this.preparedAnswer.stream().filter(l -> l.getId() == 1L).toArray()[0]));

        this.mvc.perform(get("/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author", containsString("odersky")));

        verify(languageRepo, times(1)).findById(1L);
    }

    @Test
    public void getByIdReturnNone() throws Exception {
        when(languageRepo.findById(99L))
                .thenReturn(Optional.empty());

        String answer = this.mvc.perform(get("/99"))
                .andExpect(status().isNoContent())
                .andReturn().getResponse().getContentAsString();

        assertTrue(answer.equals(""));
        verify(languageRepo, times(1)).findById(99L);
    }


    @Test
    public void create() throws Exception {
//        without ID because it's removed anyway
        Language lan = new Language().setName("python").setAuthor("guido")
                .setCurrentVersion("3.7.2").setFeature("dynamic and oop").setYear(1991);

        when(languageRepo.save(lan)).thenReturn(lan);

        String resJson = new ObjectMapper().writeValueAsString(lan);

        this.mvc.perform(MockMvcRequestBuilders.post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(resJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.author", containsString("guido")));

        Mockito.verify(languageRepo,times(1)).save(lan);
    }

    @Test
    public void put() throws Exception {
        Language lan = new Language().setId(5L).setName("python").setAuthor("guido")
                .setCurrentVersion("3.7.2").setFeature("dynamic and oop").setYear(1991);

        when(languageRepo.save(lan)).thenReturn(lan);

        String resJson = new ObjectMapper().writeValueAsString(lan);

        this.mvc.perform(MockMvcRequestBuilders.put("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(resJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author", containsString("guido")));

        verify(languageRepo, times(1)).save(lan);
    }

    @Test
    public void patch() throws Exception {
        Language languageDB = (Language) this.preparedAnswer.stream().filter(l -> l.getId() == 1L).toArray()[0];
        when(languageRepo.findById(1L)).thenReturn(Optional.of(languageDB));

        Language lan = new Language().setId(5L).setName("python")
                .setCurrentVersion("3.7.2").setYear(1991);

        Language languageToSave = LanguageControllerRest.changeProperties(languageDB, lan);
        when(languageRepo.save(languageToSave)).thenReturn(languageToSave);

        String resJson = new ObjectMapper().writeValueAsString(languageToSave);

        this.mvc.perform(MockMvcRequestBuilders.patch("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(resJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author", containsString("odersky")));

        verify(languageRepo, times(1)).findById(Mockito.any());
        verify(languageRepo, times(1)).save(Mockito.any());
    }

    @Test
    public void patchReturnNone() throws Exception {
        when(languageRepo.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Language lan = new Language().setId(5L).setName("python")
                .setCurrentVersion("3.7.2").setYear(1991);

        String resJson = new ObjectMapper().writeValueAsString(lan);

        this.mvc.perform(MockMvcRequestBuilders.patch("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(resJson))
                .andExpect(status().isNoContent());

        verify(languageRepo, times(1)).findById(Mockito.any());
    }


    @Test
    public void delete() throws Exception {
        when(languageRepo.findById(1L))
                .thenReturn(Optional.of((Language) this.preparedAnswer.stream().filter(l -> l.getId() == 1L).toArray()[0]));

        doNothing().when(languageRepo).deleteById(1L);

        assertTrue(this.mvc.perform(MockMvcRequestBuilders.delete("/1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString().equals(""));

        verify(languageRepo, times(1)).deleteById(1L);
        verify(languageRepo, times(1)).findById(1L);
    }

    @Test
    public void deleteReturnNone() throws Exception {
        when(languageRepo.findById(99L)).thenReturn(Optional.empty());

        String answer = this.mvc.perform(MockMvcRequestBuilders.delete("/99"))
                .andExpect(status().isNoContent())
                .andReturn().getResponse().getContentAsString();

        assertTrue(answer.equals(""));
        verify(languageRepo, times(1)).findById(99L);
    }

    @Test
    public void changeProperties() {
        Language languageDB = this.preparedAnswer.get(0);

        Language lan = new Language().setId(5L).setName("python")
                .setCurrentVersion("3.7.2").setYear(1991);

        Language languageToSave = LanguageControllerRest.changeProperties(languageDB, lan);

        assertTrue(languageToSave.getAuthor().equals("odersky"));
        assertTrue(languageToSave.getYear() == 1991);
        assertFalse(languageToSave.getName() == null);
    }
}
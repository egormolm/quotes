package com.example.quotes;

import com.example.quotes.entity.EnergyLevel;
import com.example.quotes.entity.Quote;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiTests {

    @Autowired
    private MockMvc mvc;

    private ObjectMapper mapper = new ObjectMapper();

    private Quote createQuote(String isin, Double bid, Double ask) {
        Quote result = new Quote();
        result.setIsin(isin);
        result.setAsk(ask);
        result.setBid(bid);
        return result;
    }

    @Test
    void testInvalidQuoteIsin() throws Exception {
        Quote quoteWithInvalidIsin = createQuote("invalid", 1d, 2d);
        Exception ex = mvc.perform(post("/quotes/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(quoteWithInvalidIsin))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn().getResolvedException();

        assertTrue(ex.getMessage().contains("Invalid isin!"));
    }

    @Test
    void testInvalidQuoteBidAsk() throws Exception {
        Quote quoteWithInvalidBidAsk = createQuote("123456789123", 2d, 1d);
        Exception ex = mvc.perform(post("/quotes/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(quoteWithInvalidBidAsk))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn().getResolvedException();

        assertTrue(ex.getMessage().contains("Constraints failed! Bid should be less than Ask."));
    }

    @Test
    void testInvalidQuoteNullBidAsk() throws Exception {
        Quote quoteWithInvalidNullBidAsk = createQuote("123456789123", null, null);
        Exception ex = mvc.perform(post("/quotes/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(quoteWithInvalidNullBidAsk))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn().getResolvedException();

        assertTrue(ex.getMessage().contains("Constraints failed! Bid should be less than Ask."));
    }

    @Test
    void testInvalidQuoteBidAskAndIsin() throws Exception {
        Quote quoteWithInvalidBidAskAndIsin = createQuote("invalid", 2d, 1d);
        Exception ex = mvc.perform(post("/quotes/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(quoteWithInvalidBidAskAndIsin))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn().getResolvedException();

        assertTrue(ex.getMessage().contains("Constraints failed! Bid should be less than Ask."));
        assertTrue(ex.getMessage().contains("Invalid isin!"));
    }

    @Test
    void testSaveCorrectQuoteAndGetElvl() throws Exception {
        Quote correctQuote = createQuote("123456789123", 1d, 2d);
        mvc.perform(post("/quotes/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(correctQuote))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        ResultActions resultElvl = mvc.perform(get("/quotes/elvl")
                .contentType(MediaType.APPLICATION_JSON)
                .param("isin", correctQuote.getIsin()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));

        MvcResult result = resultElvl.andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        EnergyLevel elvl = mapper.readValue(contentAsString, EnergyLevel.class);

        assertEquals(correctQuote.getIsin(), elvl.getIsin());

        ResultActions resultElvls = mvc.perform(get("/quotes/elvl/")
                .contentType(MediaType.APPLICATION_JSON)
                .param("isin", correctQuote.getIsin()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));

        result = resultElvls.andReturn();
        contentAsString = result.getResponse().getContentAsString();
        List<EnergyLevel> elvls = mapper.readValue(contentAsString, new TypeReference<List<EnergyLevel>>(){});

        assertEquals(1, elvls.size());
        assertEquals(correctQuote.getIsin(), elvls.get(0).getIsin());
    }
}

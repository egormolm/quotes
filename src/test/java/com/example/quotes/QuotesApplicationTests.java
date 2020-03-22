package com.example.quotes;

import com.example.quotes.entity.Quote;
import com.example.quotes.service.QuotesService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class QuotesApplicationTests {

    @Autowired
    private QuotesService quotesService;

    private Quote createQuote(String isin, Double bid, Double ask) {
        Quote result = new Quote();
        result.setIsin(isin);
        result.setAsk(ask);
        result.setBid(bid);
        return result;
    }

    @Test
    void test() {
        List<Quote> manyDifferentQuotes = new ArrayList<>();
        long startIsin = 100000000000L;
        int count = 1000;

        for (int i = 1; i < count + 1; i++) {
            String isin = String.valueOf(startIsin + i);
            Double bid = (double) (i);
            Double ask = (double) (i + 1);
            manyDifferentQuotes.add(createQuote(isin, bid, ask));
            quotesService.addQuote(createQuote(isin, bid, ask));
        }

        Assertions.assertEquals(count, quotesService.getByIsin("100000001000").getElvl(), 0.00001);
    }

	@Test
	void testInvalidQuote() {

	}

}

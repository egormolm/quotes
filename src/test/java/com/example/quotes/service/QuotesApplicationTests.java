package com.example.quotes.service;

import com.example.quotes.entity.EnergyLevel;
import com.example.quotes.entity.Quote;
import com.example.quotes.repository.QuoteRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class QuotesApplicationTests {

    @Autowired
    private QuotesService quotesService;

    @Autowired
    private QuoteRepository quoteRepository;

    private Quote createQuote(String isin, Double bid, Double ask) {
        Quote result = new Quote();
        result.setIsin(isin);
        result.setAsk(ask);
        result.setBid(bid);
        return result;
    }

    @BeforeEach
    void clearAll() {
        quoteRepository.deleteAll();
        quotesService.energyLevels.clear();
    }

    @Test
    void testNewElvlWithNullBid() {
        String isin = "100000000000";
        Double bid = null;
        Double ask = 1d;
        quotesService.addQuote(createQuote(isin, bid, ask));
        Assertions.assertEquals(ask, quotesService.getByIsin(isin).getElvl(), 0.00001);
    }

    @Test
    void testNewElvlWithNullAsk() {
        String isin = "100000000000";
        Double bid = 1d;
        Double ask = null;
        quotesService.addQuote(createQuote(isin, bid, ask));
        Assertions.assertEquals(bid, quotesService.getByIsin(isin).getElvl(), 0.00001);
    }

    @Test
    void testNewElvlWithBidAndAsk() {
        String isin = "100000000000";
        Double bid = 1d;
        Double ask = 2d;
        quotesService.addQuote(createQuote(isin, bid, ask));
        Assertions.assertEquals(bid, quotesService.getByIsin(isin).getElvl(), 0.00001);
    }

    @Test
    void testUpdateElvlOnExistedIsin() {
        String isin = "100000000000";
        Double bid = 1d;
        Double ask = 2d;
        quotesService.addQuote(createQuote(isin, bid, ask)); // elvl=bid=1
        quotesService.addQuote(createQuote(isin, bid + 1, ask + 1)); //bid > elvl -> elvl = bid
        Assertions.assertEquals(bid + 1, quotesService.getByIsin(isin).getElvl(), 0.00001);
    }

    @Test
    void testUpdateElvlOnExistedIsin2() {
        String isin = "100000000000";
        Double bid = 2d;
        Double ask = 3d;
        quotesService.addQuote(createQuote(isin, bid, ask)); // elvl=bid=2
        quotesService.addQuote(createQuote(isin, bid - 1, ask - 1)); // bid < elvl && ask < elvl, -> elvl = ask
        Assertions.assertEquals(ask - 1, quotesService.getByIsin(isin).getElvl(), 0.00001);
    }

    @Test
    void testUpdateElvlOnExistedIsinWithNullBid() {
        String isin = "100000000000";
        Double bid = 2d;
        Double ask = 3d;
        quotesService.addQuote(createQuote(isin, bid, ask)); // elvl=bid=2
        quotesService.addQuote(createQuote(isin, null, ask - 1)); // bid < elvl && ask < elvl, -> elvl = ask
        Assertions.assertEquals(ask - 1, quotesService.getByIsin(isin).getElvl(), 0.00001);
    }

    @Test
    void testUpdateElvlOnExistedIsinWithNullAsk() {
        String isin = "100000000000";
        Double bid = 1d;
        Double ask = 2d;
        quotesService.addQuote(createQuote(isin, bid, ask)); // elvl=bid=1
        quotesService.addQuote(createQuote(isin, bid + 1, null)); //bid > elvl -> elvl = bid
        Assertions.assertEquals(bid + 1, quotesService.getByIsin(isin).getElvl(), 0.00001);
    }


    @Test
    void testCorrectSavingAllElvlsOnDifferentIsins() {
        List<Quote> manyDifferentQuotes = new ArrayList<>();
        long startIsin = 100000000000L;
        int count = 100000;

        for (int i = 1; i < count + 1; i++) {
            String isin = String.valueOf(startIsin + i);
            Double bid = (double) (i);
            Double ask = (double) (i + 1);
            Quote newQuote = createQuote(isin, bid, ask);
            manyDifferentQuotes.add(newQuote);
            quotesService.addQuote(newQuote);
        }
        List<EnergyLevel> expected = manyDifferentQuotes
                .stream()
                .map(quote -> new EnergyLevel(quote.getIsin(), quote.getBid()))
                .collect(Collectors.toList());

        List<EnergyLevel> actual = new ArrayList<>(quotesService.getAll());
        actual.sort(Comparator.comparing(EnergyLevel::getIsin));
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testCorrectSavingAllElvlsOnDifferenAndSametIsins() {
        List<Quote> manyDifferentQuotes = new ArrayList<>();
        long startIsin = 100000000000L;
        int count = 100000;

        for (int i = 1; i < count + 1; i++) {
            String isin = String.valueOf(startIsin + i);
            Double bid = (double) (i);
            Double ask = (double) (i + 1);
            Quote newQuote = createQuote(isin, bid, ask);
            quotesService.addQuote(newQuote);
            Quote newQuoteOnSameIsin = createQuote(isin, null, ask - 2);
            quotesService.addQuote(newQuoteOnSameIsin);
            manyDifferentQuotes.add(newQuoteOnSameIsin);
        }
        List<EnergyLevel> expected = manyDifferentQuotes
                .stream()
                .map(quote -> new EnergyLevel(quote.getIsin(), quote.getAsk()))
                .collect(Collectors.toList());

        List<EnergyLevel> actual = new ArrayList<>(quotesService.getAll());
        actual.sort(Comparator.comparing(EnergyLevel::getIsin));
        Assertions.assertEquals(expected, actual);
    }
}

package com.example.quotes.service;

import com.example.quotes.entity.EnergyLevel;
import com.example.quotes.entity.Quote;
import com.example.quotes.repository.QuoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class QuotesService {

    ConcurrentHashMap<String, EnergyLevel> energyLevels = new ConcurrentHashMap<>();

    private final QuoteRepository quoteRepository;

    public void addQuote(Quote quote) {
        quoteRepository.save(quote);
        energyLevels.compute(quote.getIsin(), (isin, value) -> {
            Double elvl = null;
            if (value == null) {
                elvl = quote.getBid() == null ? quote.getAsk() : quote.getBid();
            } else {
                if (quote.getBid() != null && quote.getBid() > value.getElvl()) {
                    elvl = quote.getBid();
                } else if (quote.getAsk() != null && quote.getAsk() < value.getElvl()) {
                    elvl = quote.getAsk();
                }
            }
            if (elvl == null) return value;
            return new EnergyLevel(isin, elvl);
        });
    }

    public EnergyLevel getByIsin(String isin) {
        return energyLevels.get(isin);
    }

    public Collection<EnergyLevel> getAll() {
        return Collections.unmodifiableCollection(energyLevels.values());
    }
}

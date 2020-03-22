package com.example.quotes.web;

import com.example.quotes.entity.EnergyLevel;
import com.example.quotes.entity.Quote;
import com.example.quotes.service.QuotesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("quotes")
@RequiredArgsConstructor
public class QuotesController {

    private final QuotesService quotesService;

    @GetMapping("/elvl/")
    public Collection<EnergyLevel> getAllEnergyLevels() {
        return quotesService.getAll();
    }

    @GetMapping("/elvl")
    public EnergyLevel getEnergyLevelByIsin(@RequestParam String isin) {
        return quotesService.getByIsin(isin);
    }

    @PostMapping("/add")
    public void addQuote(@RequestBody @Valid Quote quote) {
        quotesService.addQuote(quote);
    }

}

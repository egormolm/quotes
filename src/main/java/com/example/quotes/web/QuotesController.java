package com.example.quotes.web;

import com.example.quotes.entity.EnergyLevel;
import com.example.quotes.entity.Quote;
import com.example.quotes.service.QuotesService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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

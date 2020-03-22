package com.example.quotes.constraints;

import com.example.quotes.entity.Quote;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BidAskValidator implements ConstraintValidator<BidAskConstraint, Quote> {
    @Override
    public void initialize(BidAskConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(Quote value, ConstraintValidatorContext context) {
        if (value.getBid() == null && value.getAsk() == null) {
            return false;
        }
        if (value.getAsk() == null || value.getBid() == null) {
            return true;
        }
        return value.getBid() < value.getAsk();
    }
}

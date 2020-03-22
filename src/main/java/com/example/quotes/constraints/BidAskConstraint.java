package com.example.quotes.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = BidAskValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BidAskConstraint {
    String message() default "Constraints failed! Bid should be less than Ask.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

package com.example.quotes.entity;

import com.example.quotes.constraints.BidAskConstraint;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Entity
@BidAskConstraint
public class Quote {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JsonIgnore
    private Long id;
    @Size(min = 12, max = 12, message = "Invalid isin!")
    private String isin;
    @CreationTimestamp
    @JsonIgnore
    private LocalDateTime timestamp;
    private Double bid;
    private Double ask;
}

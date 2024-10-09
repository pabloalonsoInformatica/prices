package com.inditex.technicaltest.prices.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AppError {
    
    private String message;
    private String error;
    private int status;
    private LocalDateTime date;
}

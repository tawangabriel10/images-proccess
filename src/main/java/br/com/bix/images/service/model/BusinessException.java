package br.com.bix.images.service.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BusinessException extends RuntimeException{

    private String message;
}

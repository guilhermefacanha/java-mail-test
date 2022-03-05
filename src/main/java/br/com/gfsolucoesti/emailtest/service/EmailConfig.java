package br.com.gfsolucoesti.emailtest.service;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@ToString
@Getter
@Setter
public class EmailConfig implements Serializable {

    private static final long serialVersionUID = -6225061944159641876L;

    private String usuario;
    private String from;
    @ToString.Exclude
    private String senha;
    private String host;
    private String port;
    private boolean debug;
    private boolean ssl;
    private boolean tsl;

}
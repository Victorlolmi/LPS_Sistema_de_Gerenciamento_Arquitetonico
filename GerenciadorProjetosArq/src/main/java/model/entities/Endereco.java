/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entities;
import javax.persistence.*;

/**
 * @author Viktin
 */
@Entity
@Table(name = "enderecos")
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String logradouro; 

    @Column(nullable = false)
    private String bairro;
    
    // Nullable proposital para suportar terrenos ou locais "S/N" (Sem Número)
    private String numero;
    
    // Length 20 dá margem para salvar com máscara (12345-678) ou formatos internacionais
    @Column(nullable = false, length = 20)
    private String cep;
    
    // TODO: Avaliar adicionar constraint (nullable = false) para cidade no futuro
    private String cidade;
    
    public Endereco(){}
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getLogradouro() {
        return logradouro;
    }
    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getBairro() {
        return bairro;
    }
    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getNumero() {
        return numero;
    }
    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getCep() {
        return cep;
    }
    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getCidade() {
        return cidade;
    }
    public void setCidade(String cidade) {
        this.cidade = cidade;
    }
}
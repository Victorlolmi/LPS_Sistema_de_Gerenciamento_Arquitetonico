/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entities;
import javax.persistence.*;
/**
 *
 * @author Viktin
 */
@Entity
@Table(name = "enderecos")
public class Endereco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Gera o ID com autoincremento
    private Long id;
    @Column(nullable = false)// Coluna n pode ser nula
    private String logadouro;
    @Column(nullable = false)
    private String bairro;
    private String numero;
    
    public Endereco(){}
    
    
    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the logadouro
     */
    public String getLogadouro() {
        return logadouro;
    }

    /**
     * @param logadouro the logadouro to set
     */
    public void setLogadouro(String logadouro) {
        this.logadouro = logadouro;
    }

    /**
     * @return the bairro
     */
    public String getBairro() {
        return bairro;
    }

    /**
     * @param bairro the bairro to set
     */
    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    /**
     * @return the numero
     */
    public String getNumero() {
        return numero;
    }

    /**
     * @param numero the numero to set
     */
    public void setNumero(String numero) {
        this.numero = numero;
    }
   
    
    
}

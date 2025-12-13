/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entities;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author juans
 */
@Entity
@Table(name = "projetos")
public class Projeto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String status; 
    
    @Column(columnDefinition = "TEXT") 
    private String descricao;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_previsao")
    private LocalDate dataPrevisao;

    private Double orcamento;
    
    // Relacionamento com Cliente (Muitos projetos para um cliente)
    @ManyToOne(fetch = FetchType.EAGER)// no nosso sistema nao tem problema
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    public Projeto() {}

    public Projeto(Long id, String nome, String status, Cliente cliente) {
        this.id = id;
        this.nome = nome;
        this.status = status;
        this.cliente = cliente;
    }
    
    
    public String getDescricao() { 
        return descricao; 
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao; 
    }

    public LocalDate getDataInicio() {
        return dataInicio; 
    }
    
    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio; 
    }

    public LocalDate getDataPrevisao() {
        return dataPrevisao; 
    }
    
    public void setDataPrevisao(LocalDate dataPrevisao) {
        this.dataPrevisao = dataPrevisao; 
    }

    public Double getOrcamento() {
        return orcamento; 
    }
    
    public void setOrcamento(Double orcamento) { 
        this.orcamento = orcamento;
    }
    
    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() { 
        return nome; 
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getStatus() {
        return status; 
    }
    
    public void setStatus(String status) {
        this.status = status;
    }

    public Cliente getCliente() {
        return cliente; 
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente; 
    }
}

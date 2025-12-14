/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entities;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Viktin
 */
@Entity
@Table(name = "despesas")
public class Despesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String descricao; // Ex: "Cimento CP-II"

    @Column(nullable = false)
    private Double valor;

    @Column(nullable = false)
    private LocalDate dataDespesa;

    private String categoria; // Ex: "Material", "Mão de Obra"
    
    private String fornecedor; // Ex: "Casa das Tintas"
    
    private String status; // Ex: "Pago", "Pendente"
    
    // --- NOVOS CAMPOS FINAIS ---
    @Column(name = "forma_pagamento")
    private String formaPagamento; // Ex: "Pix", "Cartão de Crédito"
    
    @Column(columnDefinition = "TEXT")
    private String observacoes; // Ex: "Nota fiscal 455, inclui frete"
    // ---------------------------

    @ManyToOne
    @JoinColumn(name = "projeto_id", nullable = false)
    private Projeto projeto;

    public Despesa() {
        this.dataDespesa = LocalDate.now();
        this.status = "Pendente";
    }

    public Despesa(String descricao, Double valor, String categoria, String fornecedor, String status, String formaPagamento, Projeto projeto) {
        this.descricao = descricao;
        this.valor = valor;
        this.categoria = categoria;
        this.fornecedor = fornecedor;
        this.status = status;
        this.formaPagamento = formaPagamento;
        this.projeto = projeto;
        this.dataDespesa = LocalDate.now();
    }

    // --- Getters e Setters ---

    public Long getId() { 
        return id; 
    }
    public void setId(Long id) { 
        this.id = id; 
    }

    public String getDescricao() { 
        return descricao; 
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao; 
    }

    public Double getValor() { 
        return valor; 
    }
    public void setValor(Double valor) {
        this.valor = valor;
    }

    public LocalDate getDataDespesa() { 
        return dataDespesa; 
    }
    public void setDataDespesa(LocalDate dataDespesa) { 
        this.dataDespesa = dataDespesa; 
    }

    public String getCategoria() {
        return categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria; 
    }

    public String getFornecedor() { 
        return fornecedor;
    }
    public void setFornecedor(String fornecedor) { 
        this.fornecedor = fornecedor; 
    }

    public String getStatus() { 
        return status;
    }
    public void setStatus(String status) { 
        this.status = status;
    }
    
    public String getFormaPagamento() { 
        return formaPagamento; 
    }
    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento; 
    }

    public String getObservacoes() { 
        return observacoes;
    }
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Projeto getProjeto() { 
        return projeto;
    }
    public void setProjeto(Projeto projeto) { 
        this.projeto = projeto;
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entities;

import java.time.LocalDate;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
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

    // TODO: Refatorar para Enum (EM_ANDAMENTO, CONCLUIDO, CANCELADO)
    @Column(nullable = false)
    private String status; 
    
    // TEXT para suportar descrições longas (> 255 chars)
    @Column(columnDefinition = "TEXT") 
    private String descricao;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_previsao")
    private LocalDate dataPrevisao;
    
    @Column(name = "orcamento") 
    private Double orcamento;
    
    // Cascade ALL: Salvar o projeto já persiste as alterações do Terreno
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "terreno_id", referencedColumnName = "id", nullable = true)
    private Terreno terreno;
    
    // RELACIONAMENTO COM CLIENTE
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    // RELACIONAMENTO COM GESTOR ---
    @ManyToOne
    @JoinColumn(name = "gestor_id", nullable = true) 
    private Usuario gestor;

    
    @javax.persistence.Column(name = "caminho_modelo_3d")
    private String caminhoModelo3D;

    public Projeto() {}

    // Construtor atualizado
    public Projeto(Long id, String nome, String status, Cliente cliente, Gestor gestor) {
        this.id = id;
        this.nome = nome;
        this.status = status;
        this.cliente = cliente;
        this.gestor = gestor;
    }
    
    // Getters e Setters
    
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
    
    public Usuario getGestor() {
        return gestor;
    }

    public void setGestor(Usuario gestor) {
        this.gestor = gestor;
    }
    public Terreno getTerreno() {
        return terreno;
    }
    public void setTerreno(Terreno terreno) {
        this.terreno = terreno;
    }
    
    public String getCaminhoModelo3D() {
        return caminhoModelo3D; 
    }
    public void setCaminhoModelo3D(String caminhoModelo3D) { 
        this.caminhoModelo3D = caminhoModelo3D; 
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Viktin
 */
@Entity
@Table(name = "terrenos")
public class Terreno {

    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @OneToOne(cascade = CascadeType.ALL) 
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    private Endereco endereco;
    

    private String referencia;

    @Column(name = "area_total", nullable = false)
    private Double areaTotal; 

    private String topografia; 

    @Column(name = "tipo_solo")
    private String tipoSolo; 

    @Column(name = "coeficiente_aproveitamento")
    private Double coeficienteAproveitamento; 
    
    @Column(name = "taxa_ocupacao") 
    private Double taxaOcupacao;
    
    @Column(name = "valor_compra")
    private Double valorCompra;

    @Column(name = "gabarito_altura")
    private String gabaritoAltura; 

    @Column(columnDefinition = "TEXT")
    private String descricao;

    public Terreno() {
    }

    // Atualizei o construtor para receber o objeto Endereco
    public Terreno(Long id, String nome, Endereco endereco, Double areaTotal) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.areaTotal = areaTotal;
    }

    // --- Getters e Setters Atualizados ---

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

    // Getter retorna o OBJETO Endereco agora
    public Endereco getEndereco() {
        return endereco;
    }

    // Setter recebe o OBJETO Endereco agora
    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Double getAreaTotal() {
        return areaTotal;
    }

    public void setAreaTotal(Double areaTotal) {
        this.areaTotal = areaTotal;
    }

    public String getTopografia() {
        return topografia;
    }

    public void setTopografia(String topografia) {
        this.topografia = topografia;
    }

    public String getTipoSolo() {
        return tipoSolo;
    }

    public void setTipoSolo(String tipoSolo) {
        this.tipoSolo = tipoSolo;
    }

    public Double getCoeficienteAproveitamento() {
        return coeficienteAproveitamento;
    }

    public void setCoeficienteAproveitamento(Double coeficienteAproveitamento) {
        this.coeficienteAproveitamento = coeficienteAproveitamento;
    }

    public String getGabaritoAltura() {
        return gabaritoAltura;
    }

    public void setGabaritoAltura(String gabaritoAltura) {
        this.gabaritoAltura = gabaritoAltura;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * @return the taxaOcupacao
     */
    public Double getTaxaOcupacao() {
        return taxaOcupacao;
    }

    /**
     * @param taxaOcupacao the taxaOcupacao to set
     */
    public void setTaxaOcupacao(Double taxaOcupacao) {
        this.taxaOcupacao = taxaOcupacao;
    }

    /**
     * @return the valorCompra
     */
    public Double getValorCompra() {
        return valorCompra;
    }
    
    /**
     * @param valorCompra the valorCompra to set
     */
    public void setValorCompra(Double valorCompra) {
        this.valorCompra = valorCompra;
    }
    
   
}

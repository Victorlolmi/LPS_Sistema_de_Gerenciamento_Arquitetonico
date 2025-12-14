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
@Table(name = "documentos")
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome; // Ex: "Planta Baixa v1"

    private String tipo; // Ex: ".pdf", ".dwg", ".jpg"
    
    private String categoria; // Ex: "Terreno", "Legal", "Executivo"

    @Column(name = "caminho_arquivo", nullable = false)
    private String caminhoArquivo; // O endereço no computador: "C:/Projetos/Docs/planta.pdf"
    
    @Column(name = "data_upload")
    private LocalDate dataUpload;
    
    // Relacionamento: Vários documentos pertencem a UM projeto
    @ManyToOne
    @JoinColumn(name = "projeto_id", nullable = false)
    private Projeto projeto;

    public Documento() {
        this.dataUpload = LocalDate.now(); // Data automática ao criar
    }

    public Documento(Long id, String nome, String caminhoArquivo, Projeto projeto) {
        this.id = id;
        this.nome = nome;
        this.caminhoArquivo = caminhoArquivo;
        this.projeto = projeto;
        this.dataUpload = LocalDate.now();
    }

    // --- Getters e Setters ---

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

    public String getTipo() { 
        return tipo; 
    }
    public void setTipo(String tipo) { 
        this.tipo = tipo; 
    }

    public String getCategoria() { 
        return categoria; 
    }
    public void setCategoria(String categoria) { 
        this.categoria = categoria; 
    }

    public String getCaminhoArquivo() { 
        return caminhoArquivo; 
    }
    public void setCaminhoArquivo(String caminhoArquivo) { 
        this.caminhoArquivo = caminhoArquivo; 
    }

    public LocalDate getDataUpload() { 
        return dataUpload; 
    }
    public void setDataUpload(LocalDate dataUpload) { 
        this.dataUpload = dataUpload; 
    }

    public Projeto getProjeto() { 
        return projeto; 
    }
    public void setProjeto(Projeto projeto) { 
        this.projeto = projeto; 
    }
}

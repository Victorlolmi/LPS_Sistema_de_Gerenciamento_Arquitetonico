/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.components;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.*;
/**
 *
 * @author Viktin
 */
public class ImportadorObjAvancado {
     // Guarda os dados de cada material lido do arquivo .mtl
    private static class MaterialData {
        Color diffuseColor = Color.GRAY;
        String texturePath = null;
    }

    // Guarda as faces associadas a um material específico
    private static class MeshGroup {
        List<Integer> faces = new ArrayList<>();
        List<Integer> faceSmoothingGroups = new ArrayList<>();
        PhongMaterial material;
    }

    public static Group carregar(String caminhoArquivo) throws Exception {
        Group grupoModelo = new Group();
        File arquivoObj = new File(caminhoArquivo);
        String diretorioBase = arquivoObj.getParent();

        // Dados globais do OBJ
        List<Float> vertices = new ArrayList<>();
        List<Float> texCoords = new ArrayList<>();
        List<Float> normals = new ArrayList<>(); // (Opcional, não usado no TriangleMesh básico, mas lido)

        // Mapa de Materiais: Nome -> Dados
        Map<String, MaterialData> bibliotecaMateriais = new HashMap<>();
        
        // Mapa de Malhas: NomeMaterial -> Grupo de Faces
        Map<String, MeshGroup> malhasPorMaterial = new HashMap<>();
        
        String materialAtual = "default";
        
        // Cria material padrão
        MaterialData matDefault = new MaterialData();
        matDefault.diffuseColor = Color.LIGHTGRAY;
        bibliotecaMateriais.put("default", matDefault);
        
        // Prepara o grupo padrão
        MeshGroup grupoAtual = new MeshGroup();
        grupoAtual.material = new PhongMaterial(Color.LIGHTGRAY);
        malhasPorMaterial.put("default", grupoAtual);

        // --- 1. LER O ARQUIVO .OBJ ---
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivoObj))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\s+");

                switch (parts[0]) {
                    case "mtllib": // Referência ao arquivo de materiais
                        String mtlFileName = line.substring(7).trim();
                        lerArquivoMTL(new File(diretorioBase, mtlFileName), bibliotecaMateriais);
                        break;

                    case "usemtl": // Troca de material
                        materialAtual = parts[1];
                        if (!malhasPorMaterial.containsKey(materialAtual)) {
                            MeshGroup novoGrupo = new MeshGroup();
                            // Configura o material JavaFX baseado no que leu do MTL
                            MaterialData md = bibliotecaMateriais.getOrDefault(materialAtual, matDefault);
                            PhongMaterial mat = new PhongMaterial();
                            mat.setDiffuseColor(md.diffuseColor);
                            mat.setSpecularColor(Color.WHITE); // Brilho padrão
                            
                            if (md.texturePath != null) {
                                try {
                                    File texFile = new File(diretorioBase, md.texturePath);
                                    if(texFile.exists()) {
                                        mat.setDiffuseMap(new Image(new FileInputStream(texFile)));
                                    }
                                } catch (Exception e) {
                                    System.err.println("Erro ao carregar textura: " + md.texturePath);
                                }
                            }
                            novoGrupo.material = mat;
                            malhasPorMaterial.put(materialAtual, novoGrupo);
                        }
                        grupoAtual = malhasPorMaterial.get(materialAtual);
                        break;

                    case "v": // Vértice
                        vertices.add(Float.parseFloat(parts[1]));
                        vertices.add(Float.parseFloat(parts[2]));
                        vertices.add(Float.parseFloat(parts[3]));
                        break;

                    case "vt": // Coordenada de Textura
                        texCoords.add(Float.parseFloat(parts[1]));
                        // JavaFX inverte o eixo Y da textura (1 - V)
                        texCoords.add(1f - Float.parseFloat(parts[2])); 
                        break;

                    case "f": // Face
                        // Formatos: v, v/vt, v//vn, v/vt/vn
                        // Precisamos converter para triângulos e guardar índices
                        List<Integer> vIndices = new ArrayList<>();
                        List<Integer> vtIndices = new ArrayList<>();

                        for (int i = 1; i < parts.length; i++) {
                            String[] subParts = parts[i].split("/");
                            // Vértice (Base 1 -> Base 0)
                            vIndices.add(Integer.parseInt(subParts[0]) - 1);
                            
                            // Textura (se existir)
                            if (subParts.length > 1 && !subParts[1].isEmpty()) {
                                vtIndices.add(Integer.parseInt(subParts[1]) - 1);
                            } else {
                                vtIndices.add(0); // Dummy se não tiver textura
                            }
                        }

                        // Triangulação (Fan)
                        if (vIndices.size() >= 3) {
                            int v0 = vIndices.get(0);
                            int vt0 = vtIndices.get(0);
                            for (int i = 1; i < vIndices.size() - 1; i++) {
                                grupoAtual.faces.add(v0); grupoAtual.faces.add(vt0);
                                grupoAtual.faces.add(vIndices.get(i)); grupoAtual.faces.add(vtIndices.get(i));
                                grupoAtual.faces.add(vIndices.get(i+1)); grupoAtual.faces.add(vtIndices.get(i+1));
                            }
                        }
                        break;
                }
            }
        }

        // Se não tiver coordenadas de textura no arquivo, adiciona uma dummy
        if (texCoords.isEmpty()) {
            texCoords.add(0f); texCoords.add(0f);
        }

        // --- 2. CONSTRUIR OS MESHVIEWS ---
        // Para cada material, cria um MeshView separado e adiciona no grupo
        float[] verticesArray = listToFloatArray(vertices);
        float[] texCoordsArray = listToFloatArray(texCoords);

        for (Map.Entry<String, MeshGroup> entry : malhasPorMaterial.entrySet()) {
            MeshGroup mg = entry.getValue();
            if (mg.faces.isEmpty()) continue;

            TriangleMesh mesh = new TriangleMesh();
            mesh.getPoints().addAll(verticesArray);
            mesh.getTexCoords().addAll(texCoordsArray);
            mesh.getFaces().addAll(listToIntArray(mg.faces));

            MeshView meshView = new MeshView(mesh);
            meshView.setMaterial(mg.material);
            meshView.setCullFace(javafx.scene.shape.CullFace.NONE); // Mostra os dois lados

            grupoModelo.getChildren().add(meshView);
        }
        
        // Ajuste final de rotação (muitos OBJs vêm deitados ou invertidos)
        grupoModelo.setRotationAxis(Rotate.X_AXIS);
        //grupoModelo.setRotate(180); 

        return grupoModelo;
    }

    // --- MÉTODOS AUXILIARES ---
    
    private static void lerArquivoMTL(File file, Map<String, MaterialData> map) {
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String currentMtl = null;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("\\s+");

                if (parts[0].equals("newmtl")) {
                    currentMtl = parts[1];
                    map.put(currentMtl, new MaterialData());
                } else if (currentMtl != null) {
                    MaterialData md = map.get(currentMtl);
                    if (parts[0].equals("Kd")) { // Cor Difusa
                        md.diffuseColor = Color.color(
                            Float.parseFloat(parts[1]),
                            Float.parseFloat(parts[2]),
                            Float.parseFloat(parts[3])
                        );
                    } else if (parts[0].equals("map_Kd")) { // Mapa de Textura
                        // O nome do arquivo pode ter espaços, pega o resto da linha
                        md.texturePath = line.substring(7).trim();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static float[] listToFloatArray(List<Float> list) {
        float[] array = new float[list.size()];
        for (int i = 0; i < list.size(); i++) array[i] = list.get(i);
        return array;
    }

    private static int[] listToIntArray(List<Integer> list) {
        int[] array = new int[list.size()];
        for (int i = 0; i < list.size(); i++) array[i] = list.get(i);
        return array;
    }
}

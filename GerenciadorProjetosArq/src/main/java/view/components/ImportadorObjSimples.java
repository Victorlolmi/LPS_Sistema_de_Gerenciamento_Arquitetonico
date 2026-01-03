/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.components;

import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Viktin
 */
public class ImportadorObjSimples {private static class FloatList {
            float[] data = new float[1024];
            int size = 0;
            void add(float val) {
                if (size == data.length) {
                    float[] temp = new float[data.length * 2];
                    System.arraycopy(data, 0, temp, 0, data.length);
                    data = temp;
                }
                data[size++] = val;
            }
            float[] toArray() {
                float[] result = new float[size];
                System.arraycopy(data, 0, result, 0, size);
                return result;
            }
        }

        private static class IntList {
            int[] data = new int[1024];
            int size = 0;
            void add(int val) {
                if (size == data.length) {
                    int[] temp = new int[data.length * 2];
                    System.arraycopy(data, 0, temp, 0, data.length);
                    data = temp;
                }
                data[size++] = val;
            }
            int[] toArray() {
                int[] result = new int[size];
                System.arraycopy(data, 0, result, 0, size);
                return result;
            }
        }

        public static MeshView carregar(String caminhoArquivo) throws Exception {
            TriangleMesh mesh = new TriangleMesh();
            
            FloatList vertices = new FloatList();
            IntList faces = new IntList();
            FloatList texCoords = new FloatList(); 

            // Textura dummy obrigatória
            texCoords.add(0f);
            texCoords.add(0f);

            File file = new File(caminhoArquivo);
            System.out.println("Lendo arquivo: " + file.getAbsolutePath());
            
            try (BufferedReader reader = new BufferedReader(new FileReader(file), 8192)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    
                    // Vértices (v x y z)
                    if (line.startsWith("v ")) { 
                        String[] parts = line.split("\\s+");
                        // Ajuste de escala: Se o modelo for muito pequeno, multiplique aqui (ex: * 100)
                        float x = Float.parseFloat(parts[1]);
                        float y = Float.parseFloat(parts[2]);
                        float z = Float.parseFloat(parts[3]);
                        
                        vertices.add(x); 
                        vertices.add(y);
                        vertices.add(z);
                    } 
                    // Faces (f v1 v2 v3 ...)
                    else if (line.startsWith("f ")) {
                        String[] parts = line.split("\\s+");
                        List<Integer> faceIndices = new ArrayList<>();
                        
                        for (int i = 1; i < parts.length; i++) {
                            String part = parts[i];
                            String[] vInfo = part.split("/");
                            // OBJ é base-1, Java é base-0
                            int vIndex = Integer.parseInt(vInfo[0]) - 1;
                            faceIndices.add(vIndex);
                        }
                        
                        // Triangulação manual (Fan Triangulation)
                        // Converte polígonos (quadrados, pentágonos) em triângulos
                        // v0, v1, v2 -> v0, v2, v3 -> v0, v3, v4...
                        if (faceIndices.size() >= 3) {
                            int v0 = faceIndices.get(0);
                            for (int i = 1; i < faceIndices.size() - 1; i++) {
                                int v1 = faceIndices.get(i);
                                int v2 = faceIndices.get(i + 1);
                                
                                faces.add(v0); faces.add(0); // v0 + textura
                                faces.add(v1); faces.add(0); // v1 + textura
                                faces.add(v2); faces.add(0); // v2 + textura
                            }
                        }
                    }
                }
            }
            
            if (vertices.size == 0) {
                throw new Exception("Arquivo OBJ vazio ou sem vértices 'v'.");
            }

            System.out.println("Modelo carregado com " + (vertices.size/3) + " vértices e " + (faces.size/2/3) + " triângulos.");

            mesh.getPoints().addAll(vertices.toArray());
            mesh.getTexCoords().addAll(texCoords.toArray());
            mesh.getFaces().addAll(faces.toArray());

            return new MeshView(mesh);
        }
    }
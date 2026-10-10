/*
 * Decompiled with CFR 0.152.
 */
package com.badlogic.gdx.graphics.g3d.loader;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.model.data.ModelMaterial;
import com.badlogic.gdx.graphics.g3d.model.data.ModelTexture;
import com.badlogic.gdx.utils.Array;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class MtlLoader {
    public Array<ModelMaterial> materials = new Array();

    MtlLoader() {
    }

    public void load(FileHandle file) {
        ObjMaterial currentMaterial = new ObjMaterial();
        if (file == null || !file.exists()) {
            return;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.read()), 4096);
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens;
                if (line.length() > 0 && line.charAt(0) == '\t') {
                    line = line.substring(1).trim();
                }
                if ((tokens = line.split("\\s+"))[0].length() == 0 || tokens[0].charAt(0) == '#') continue;
                String key = tokens[0].toLowerCase();
                if (key.equals("newmtl")) {
                    ModelMaterial mat = currentMaterial.build();
                    this.materials.add(mat);
                    if (tokens.length > 1) {
                        currentMaterial.materialName = tokens[1];
                        currentMaterial.materialName = currentMaterial.materialName.replace('.', '_');
                    } else {
                        currentMaterial.materialName = "default";
                    }
                    currentMaterial.reset();
                    continue;
                }
                if (key.equals("ka")) {
                    currentMaterial.ambientColor = this.parseColor(tokens);
                    continue;
                }
                if (key.equals("kd")) {
                    currentMaterial.diffuseColor = this.parseColor(tokens);
                    continue;
                }
                if (key.equals("ks")) {
                    currentMaterial.specularColor = this.parseColor(tokens);
                    continue;
                }
                if (key.equals("tr") || key.equals("d")) {
                    currentMaterial.opacity = Float.parseFloat(tokens[1]);
                    continue;
                }
                if (key.equals("ns")) {
                    currentMaterial.shininess = Float.parseFloat(tokens[1]);
                    continue;
                }
                if (key.equals("map_d")) {
                    currentMaterial.alphaTexFilename = file.parent().child(tokens[1]).path();
                    continue;
                }
                if (key.equals("map_ka")) {
                    currentMaterial.ambientTexFilename = file.parent().child(tokens[1]).path();
                    continue;
                }
                if (key.equals("map_kd")) {
                    currentMaterial.diffuseTexFilename = file.parent().child(tokens[1]).path();
                    continue;
                }
                if (key.equals("map_ks")) {
                    currentMaterial.specularTexFilename = file.parent().child(tokens[1]).path();
                    continue;
                }
                if (!key.equals("map_ns")) continue;
                currentMaterial.shininessTexFilename = file.parent().child(tokens[1]).path();
            }
            reader.close();
        }
        catch (IOException e) {
            return;
        }
        ModelMaterial mat = currentMaterial.build();
        this.materials.add(mat);
    }

    private Color parseColor(String[] tokens) {
        float r = Float.parseFloat(tokens[1]);
        float g = Float.parseFloat(tokens[2]);
        float b = Float.parseFloat(tokens[3]);
        float a = 1.0f;
        if (tokens.length > 4) {
            a = Float.parseFloat(tokens[4]);
        }
        return new Color(r, g, b, a);
    }

    public ModelMaterial getMaterial(String name) {
        for (ModelMaterial m : this.materials) {
            if (!m.id.equals(name)) continue;
            return m;
        }
        ModelMaterial mat = new ModelMaterial();
        mat.id = name;
        mat.diffuse = new Color(Color.WHITE);
        this.materials.add(mat);
        return mat;
    }

    private static class ObjMaterial {
        String materialName = "default";
        Color ambientColor;
        Color diffuseColor;
        Color specularColor;
        float opacity;
        float shininess;
        String alphaTexFilename;
        String ambientTexFilename;
        String diffuseTexFilename;
        String shininessTexFilename;
        String specularTexFilename;

        public ObjMaterial() {
            this.reset();
        }

        public ModelMaterial build() {
            ModelMaterial mat = new ModelMaterial();
            mat.id = this.materialName;
            mat.ambient = this.ambientColor == null ? null : new Color(this.ambientColor);
            mat.diffuse = new Color(this.diffuseColor);
            mat.specular = new Color(this.specularColor);
            mat.opacity = this.opacity;
            mat.shininess = this.shininess;
            this.addTexture(mat, this.alphaTexFilename, 9);
            this.addTexture(mat, this.ambientTexFilename, 4);
            this.addTexture(mat, this.diffuseTexFilename, 2);
            this.addTexture(mat, this.specularTexFilename, 5);
            this.addTexture(mat, this.shininessTexFilename, 6);
            return mat;
        }

        private void addTexture(ModelMaterial mat, String texFilename, int usage) {
            if (texFilename != null) {
                ModelTexture tex = new ModelTexture();
                tex.usage = usage;
                tex.fileName = texFilename;
                if (mat.textures == null) {
                    mat.textures = new Array(1);
                }
                mat.textures.add(tex);
            }
        }

        public void reset() {
            this.ambientColor = null;
            this.diffuseColor = Color.WHITE;
            this.specularColor = Color.WHITE;
            this.opacity = 1.0f;
            this.shininess = 0.0f;
            this.alphaTexFilename = null;
            this.ambientTexFilename = null;
            this.diffuseTexFilename = null;
            this.shininessTexFilename = null;
            this.specularTexFilename = null;
        }
    }
}


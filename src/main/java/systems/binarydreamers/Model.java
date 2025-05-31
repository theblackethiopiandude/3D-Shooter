package systems.binarydreamers;
/*

import org.lwjgl.opengl.GL11;
import java.io.*;
import java.util.*;

public class Model {
    private List<float[]> vertices = new ArrayList<>();
    private List<int[]> faces = new ArrayList<>();

    public Model(String path) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream(path))))) {

            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("v ")) {
                    String[] tokens = line.split(" ");
                    float x = Float.parseFloat(tokens[1]);
                    float y = Float.parseFloat(tokens[2]);
                    float z = Float.parseFloat(tokens[3]);
                    vertices.add(new float[]{x, y, z});
                } else if (line.startsWith("f ")) {
                    String[] tokens = line.split(" ");
                    int v1 = Integer.parseInt(tokens[1].split("/")[0]) - 1;
                    int v2 = Integer.parseInt(tokens[2].split("/")[0]) - 1;
                    int v3 = Integer.parseInt(tokens[3].split("/")[0]) - 1;
                    faces.add(new int[]{v1, v2, v3});
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void render() {
        GL11.glBegin(GL11.GL_TRIANGLES);
        for (int[] face : faces) {
            for (int index : face) {
                float[] vertex = vertices.get(index);
                GL11.glVertex3f(vertex[0], vertex[1], vertex[2]);
            }
        }
        GL11.glEnd();
    }
}
*/

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.opengl.GL11.*;

public class Model {
    private AIScene scene;
    private String directory;
    private List<Float> vertices = new ArrayList<>();
    private List<Float> normals = new ArrayList<>();
    private List<Float> texCoords = new ArrayList<>();
    private List<Integer> indices = new ArrayList<>();

    public Model(String resourcePath, String directory) {
//        directory = resourcePath.substring(0, resourcePath.lastIndexOf('/'));
        this.directory = directory;

        scene = Assimp.aiImportFile(resourcePath, aiProcess_Triangulate | aiProcess_FlipUVs);
        if (scene == null || scene.mRootNode() == null) {
            throw new RuntimeException("Failed to load model: " + resourcePath);
        }

        processNode(scene.mRootNode(), scene);
    }

    private void processNode(AINode node, AIScene scene) {
        // Meshes in this node
        IntBuffer meshIndices = node.mMeshes();  // ✅ This is an IntBuffer

        for (int i = 0; i < node.mNumMeshes(); i++) {
            int meshIndex = meshIndices.get(i);
            AIMesh mesh = AIMesh.create(scene.mMeshes().get(meshIndex));
            processMesh(mesh, scene);
        }

        // Children nodes
        PointerBuffer children = node.mChildren();  // ✅ This one IS a PointerBuffer

        for (int i = 0; i < node.mNumChildren(); i++) {
            AINode childNode = AINode.create(children.get(i));
            processNode(childNode, scene);
        }
    }


    private class MeshData {
        List<Float> vertices = new ArrayList<>();
        List<Float> texCoords = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        int textureID = -1;
    }
    private final List<MeshData> meshes = new ArrayList<>();

    private void processMesh(AIMesh mesh, AIScene scene) {
        MeshData data = new MeshData();

        // Vertices and texture coords
        for (int i = 0; i < mesh.mNumVertices(); i++) {
            AIVector3D pos = mesh.mVertices().get(i);
            data.vertices.add(pos.x());
            data.vertices.add(pos.y());
            data.vertices.add(pos.z());

            if (mesh.mTextureCoords(0) != null) {
                AIVector3D tex = mesh.mTextureCoords(0).get(i);
                data.texCoords.add(tex.x());
                data.texCoords.add(tex.y());
            } else {
                data.texCoords.add(0.0f); data.texCoords.add(0.0f);
            }
        }

        for (int i = 0; i < mesh.mNumFaces(); i++) {
            AIFace face = mesh.mFaces().get(i);
            IntBuffer buf = face.mIndices();
            while (buf.hasRemaining()) {
                data.indices.add(buf.get());
            }
        }

        // Load texture
        int matIndex = mesh.mMaterialIndex();
        AIMaterial material = AIMaterial.create(scene.mMaterials().get(matIndex));

        AIString path = AIString.calloc();
        aiGetMaterialTexture(material, aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, null, null, null, null, null);
        String texPath = path.dataString().replace("\\", "/"); // Normalize slashes

        if (!texPath.isEmpty()) {
            // If texPath includes folder, extract just the filename
            String fileName = texPath.substring(texPath.lastIndexOf('/') + 1);

            // Then load from your known textures folder
            data.textureID = loadTexture("src/main/resources/models/" + directory + "/textures/" + fileName);
        }


        path.free();
        meshes.add(data);
    }


    public void render() {
        for (MeshData mesh : meshes) {
            if (mesh.textureID != -1) {
                glEnable(GL_TEXTURE_2D);
                glBindTexture(GL_TEXTURE_2D, mesh.textureID);
            }

            glBegin(GL_TRIANGLES);
            for (int i = 0; i < mesh.indices.size(); i++) {
                int index = mesh.indices.get(i);
                float tx = mesh.texCoords.get(index * 2);
                float ty = mesh.texCoords.get(index * 2 + 1);
                glTexCoord2f(tx, ty);

                float vx = mesh.vertices.get(index * 3);
                float vy = mesh.vertices.get(index * 3 + 1);
                float vz = mesh.vertices.get(index * 3 + 2);
                glVertex3f(vx, vy, vz);
            }
            glEnd();

            if (mesh.textureID != -1) {
                glDisable(GL_TEXTURE_2D);
            }
        }
    }


    private int loadTexture(String path) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            STBImage.stbi_set_flip_vertically_on_load(true);
            ByteBuffer image = STBImage.stbi_load(path, width, height, channels, 4);
            if (image == null) {
                throw new RuntimeException("Failed to load texture: " + path);
            }

            int texID = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, texID);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(), height.get(), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            STBImage.stbi_image_free(image);
            return texID;
        }
    }

}

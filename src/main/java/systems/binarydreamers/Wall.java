package systems.binarydreamers;

import static org.lwjgl.opengl.GL11.*;

public class Wall {
    public float x, y, z, width, height, depth;
    public int textureID;
    public boolean rotateY;

    public Wall(float x, float y, float z, float width, float height, float depth, int textureID, boolean rotateY) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.textureID = textureID;
        this.rotateY = rotateY;
    }

    public void render() {
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, textureID);
        glColor3f(1f, 1f, 1f);

        glPushMatrix();
        glTranslatef(x, y, z);

        if (rotateY) {
            glRotatef(90, 0, 1, 0);                  // rotate around Y axis
        }

        // Front face
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0); glVertex3f(0, 0, 0);
        glTexCoord2f(1, 0); glVertex3f(width, 0, 0);
        glTexCoord2f(1, 1); glVertex3f(width, height, 0);
        glTexCoord2f(0, 1); glVertex3f(0, height, 0);
        glEnd();

        // You can add more faces here if needed (back, sides)

        glPopMatrix();
        glDisable(GL_TEXTURE_2D);
    }
}

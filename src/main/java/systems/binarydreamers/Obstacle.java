package systems.binarydreamers;

import static org.lwjgl.opengl.GL11.*;

public class Obstacle {
    protected float x, y, z;
    protected float size = 2.0f; // default cube size

    public Obstacle(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void render() {
        glPushMatrix();
        glTranslatef(x, y, z);
        glColor3f(0.5f, 0.5f, 0.5f); // default gray
        CubeUtils.drawCube();
        glPopMatrix();
    }

    public boolean collidesWith(float px, float py, float pz) {
        float half = size / 2f;
        return (px > x - half && px < x + half) &&
                (py > y - half && py < y + half) &&
                (pz > z - half && pz < z + half);
    }
}



package systems.binarydreamers;

import static org.lwjgl.opengl.GL11.*;
import static systems.binarydreamers.CubeUtils.drawCube;

public class Enemy extends Obstacle {
    public boolean alive = true;

    public Enemy(float x, float y, float z) {
        super(x, y, z);
    }

    @Override
    public void render() {
        if (!alive) return;
        glPushMatrix();
        glTranslatef(x, y, z);
        glColor3f(1.0f, 0.0f, 0.0f); // red
        CubeUtils.drawCube();
        glPopMatrix();
    }
}



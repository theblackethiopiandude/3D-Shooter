package systems.binarydreamers;


import static org.lwjgl.opengl.GL11.*;

class Bullet {
    float x, y, z;
    float dx, dy, dz;
    float speed = 0.9F;

    Bullet(float startX, float startY, float startZ, float dirX, float dirY, float dirZ) {
        this.x = startX;
        this.y = startY;
        this.z = startZ;

        // Normalize direction
        float len = (float) Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
        this.dx = dirX / len;
        this.dy = dirY / len;
        this.dz = dirZ / len;
    }

    void update() {
        x += dx * speed;
        y += dy * speed;
        z += dz * speed;
    }

    void render() {
        glPushMatrix();
        glTranslatef(x, y, z);
        glBegin(GL_QUADS);
//        glColor3f(1.0f, 1.0f, 0.0f); // Yellow bullet

// Front
        glVertex3f(-0.05f, -0.05f, 0.05f);
        glVertex3f( 0.05f, -0.05f, 0.05f);
        glVertex3f( 0.05f,  0.05f, 0.05f);
        glVertex3f(-0.05f,  0.05f, 0.05f);

// Back
        glVertex3f(-0.05f, -0.05f, -0.05f);
        glVertex3f(-0.05f,  0.05f, -0.05f);
        glVertex3f( 0.05f,  0.05f, -0.05f);
        glVertex3f( 0.05f, -0.05f, -0.05f);

// Top
        glVertex3f(-0.05f,  0.05f, -0.05f);
        glVertex3f(-0.05f,  0.05f,  0.05f);
        glVertex3f( 0.05f,  0.05f,  0.05f);
        glVertex3f( 0.05f,  0.05f, -0.05f);

// Bottom
        glVertex3f(-0.05f, -0.05f, -0.05f);
        glVertex3f( 0.05f, -0.05f, -0.05f);
        glVertex3f( 0.05f, -0.05f,  0.05f);
        glVertex3f(-0.05f, -0.05f,  0.05f);

// Right
        glVertex3f(0.05f, -0.05f, -0.05f);
        glVertex3f(0.05f,  0.05f, -0.05f);
        glVertex3f(0.05f,  0.05f,  0.05f);
        glVertex3f(0.05f, -0.05f,  0.05f);

// Left
        glVertex3f(-0.05f, -0.05f, -0.05f);
        glVertex3f(-0.05f, -0.05f,  0.05f);
        glVertex3f(-0.05f,  0.05f,  0.05f);
        glVertex3f(-0.05f,  0.05f, -0.05f);

        glEnd();


        glPopMatrix();
    }
}


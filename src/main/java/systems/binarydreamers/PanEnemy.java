package systems.binarydreamers;

import static org.lwjgl.opengl.GL11.*;

public class PanEnemy {
    public static final float WIDTH = 1.2f;   // total width (from your aabbCollision logic)
    public static final float HEIGHT = 1.2f;  // height of the pan
    public static final float DEPTH = WIDTH;

    public float x, y, z;
    public float originZ;
    public float range = 5f;
    public float speed;
    public boolean alive = true;
    public Model model;

    private float time = 0f;

    public PanEnemy(float x, float y, float z, Model model, float speed) {
        this.x = x;
        this.originZ = z;
        this.y = y;
        this.z = z;
        this.model = model;
        this.speed = speed;
    }

    public void update(float deltaTime) {
        if (!alive) return;

        time += deltaTime;
        z = originZ + (float)Math.sin(time * speed) * range;
    }

    public void render() {
        if (!alive) return;

        glPushMatrix();
        glTranslatef(x, y, z);
        glScalef(0.04f, 0.04f, 0.04f); // scale pan size
        glRotatef(90, 0, 0, 1);
        model.render();
        glPopMatrix();

//        drawHitBox(WIDTH, HEIGHT);

    }

    private void drawHitBox(float width, float height){
        // Draw bounding box (hitbox)
        glPushMatrix();
        glTranslatef(x, y, z);

        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glColor4f(1.0f, 0f, 0f, 0.3f); // semi-transparent red

        float depth = width;   // same as width, assuming it's round

        float halfW = width / 2f;
        float halfD = depth / 2f;

        glBegin(GL_QUADS);

        // Front
        glVertex3f(-halfW, 0, -halfD);
        glVertex3f(halfW, 0, -halfD);
        glVertex3f(halfW, height, -halfD);
        glVertex3f(-halfW, height, -halfD);

        // Back
        glVertex3f(-halfW, 0, halfD);
        glVertex3f(halfW, 0, halfD);
        glVertex3f(halfW, height, halfD);
        glVertex3f(-halfW, height, halfD);

        // Left
        glVertex3f(-halfW, 0, -halfD);
        glVertex3f(-halfW, 0, halfD);
        glVertex3f(-halfW, height, halfD);
        glVertex3f(-halfW, height, -halfD);

        // Right
        glVertex3f(halfW, 0, -halfD);
        glVertex3f(halfW, 0, halfD);
        glVertex3f(halfW, height, halfD);
        glVertex3f(halfW, height, -halfD);

        // Top
        glVertex3f(-halfW, height, -halfD);
        glVertex3f(halfW, height, -halfD);
        glVertex3f(halfW, height, halfD);
        glVertex3f(-halfW, height, halfD);

        // Bottom
        glVertex3f(-halfW, 0, -halfD);
        glVertex3f(halfW, 0, -halfD);
        glVertex3f(halfW, 0, halfD);
        glVertex3f(-halfW, 0, halfD);

        glEnd();

        glDisable(GL_BLEND);
//        glEnable(GL_TEXTURE_2D);
        glPopMatrix();

    }
}


package systems.binarydreamers;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Game {

    private long window;

    public void run() {
        init();
        loop();
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        window = glfwCreateWindow(800, 600, "3D Shooting Arena", NULL, NULL);
        if (window == NULL) throw new RuntimeException("Failed to create GLFW window");

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1); // Enable v-sync
        glfwShowWindow(window);

        GL.createCapabilities();

        // Enable 3D projection matrix
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float aspect = 800f / 600f; // Window aspect ratio
        float fov = 60f;
        float zNear = 0.1f;
        float zFar = 100f;
        float ymax = (float) (Math.tan(Math.toRadians(fov / 2)) * zNear);
        float xmax = ymax * aspect;
        glFrustum(-xmax, xmax, -ymax, ymax, zNear, zFar);

        glMatrixMode(GL_MODELVIEW);

        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

    }



    private void loop() {
        glEnable(GL_DEPTH_TEST); // Enable depth for 3D

        glClearColor(0.1f, 0.1f, 0.2f, 0.0f);

        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            glLoadIdentity();

            processInput();

            // Move camera backward
            glTranslatef(0.0f, 0.0f, -5.0f);

            // Rotate scene slowly
            glRotatef((float) (glfwGetTime() * 20), 1.0f, 1.0f, 0.0f);

            // Draw cube
            drawCube();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void drawCube() {
        glBegin(GL_QUADS);

        // Front face (red)
        glColor3f(1.0f, 0.0f, 0.0f);
        glVertex3f(-1.0f, -1.0f, 1.0f);
        glVertex3f(1.0f, -1.0f, 1.0f);
        glVertex3f(1.0f, 1.0f, 1.0f);
        glVertex3f(-1.0f, 1.0f, 1.0f);

        // Back face (green)
        glColor3f(0.0f, 1.0f, 0.0f);
        glVertex3f(-1.0f, -1.0f, -1.0f);
        glVertex3f(-1.0f, 1.0f, -1.0f);
        glVertex3f(1.0f, 1.0f, -1.0f);
        glVertex3f(1.0f, -1.0f, -1.0f);

        // Top face (blue)
        glColor3f(0.0f, 0.0f, 1.0f);
        glVertex3f(-1.0f, 1.0f, -1.0f);
        glVertex3f(-1.0f, 1.0f, 1.0f);
        glVertex3f(1.0f, 1.0f, 1.0f);
        glVertex3f(1.0f, 1.0f, -1.0f);

        // Bottom face (yellow)
        glColor3f(1.0f, 1.0f, 0.0f);
        glVertex3f(-1.0f, -1.0f, -1.0f);
        glVertex3f(1.0f, -1.0f, -1.0f);
        glVertex3f(1.0f, -1.0f, 1.0f);
        glVertex3f(-1.0f, -1.0f, 1.0f);

        // Right face (magenta)
        glColor3f(1.0f, 0.0f, 1.0f);
        glVertex3f(1.0f, -1.0f, -1.0f);
        glVertex3f(1.0f, 1.0f, -1.0f);
        glVertex3f(1.0f, 1.0f, 1.0f);
        glVertex3f(1.0f, -1.0f, 1.0f);

        // Left face (cyan)
        glColor3f(0.0f, 1.0f, 1.0f);
        glVertex3f(-1.0f, -1.0f, -1.0f);
        glVertex3f(-1.0f, -1.0f, 1.0f);
        glVertex3f(-1.0f, 1.0f, 1.0f);
        glVertex3f(-1.0f, 1.0f, -1.0f);

        glEnd();
    }


    private void processInput() {
        float speed = 0.5f;

        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            glTranslatef(0.0f, 0.0f, speed); // Move forward
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            glTranslatef(0.0f, 0.0f, -speed); // Move back
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            glTranslatef(speed, 0.0f, 0.0f); // Strafe left
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            glTranslatef(-speed, 0.0f, 0.0f); // Strafe right
        }
    }



    public static void main(String[] args) {
        new Game().run();
    }
}


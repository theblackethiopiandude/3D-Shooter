package systems.binarydreamers;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Game {

    private long window;
    // Camera angles
    private float yaw = -90.0f;
    private float pitch = 0.0f;

    // Mouse tracking
    private double lastMouseX = 960, lastMouseY = 540;
    private boolean firstMouse = true;

    private final float playerRadius = 0.3f;
    private final float playerHeight = 1.5f;


    // Camera position and direction
    private float camX = 35f, camY = playerHeight, camZ = 0f;
    private float dirX = 0f, dirY = 0f, dirZ = -1f;

    private float yVelocity = 0.0f;
    private boolean isGrounded = true;

    private final float gravity = -0.02f;    // pull down each frame
    private final float jumpStrength = 0.4f; // upward boost
    private final float groundLevel = playerHeight;  // camera eye height when standing

    private final List<Bullet> bullets = new ArrayList<>();
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Obstacle> obstacles = new ArrayList<>();


    private int score = 0;

    private int ammo = 10;
    private int maxAmmo = 100;
    private float reloadTime = 2.0f;
    private float reloadTimer = 0f;
    private boolean reloading = false;

    private float respawnTimer = 0;
    private float respawnWait = 10f;

    private Model weaponModel;


    private float recoilOffset = 0f;
    private float recoilSpeed = 0.15f;     // speed of recoil "kick"
    private float recoilRecovery = 0.05f;  // how fast it returns to normal
    private boolean firing = false;


    private boolean muzzleFlashActive = false;
    private float muzzleFlashTimer = 0f;
    private final float muzzleFlashDuration = 0.05f; // visible for 50ms

    private int muzzleFlashTexture;
    private final List<Wall> walls = new ArrayList<>();
    private boolean flashlightOn = true;
    int groundTex;
    int wallHeight = 10;

    private List<PanEnemy> pans = new ArrayList<>();
    private final float PAN_HEIGHT = 1.0f;

    private final float EASY_SPEED = 5.8f;
    private final float MEDIUM_SPEED = 1.2f;
    private final float HARD_SPEED = 1.6f;
    private final float PRO_SPEED = 2.0f;

    private final List<Float> PAN_SPEEDS = List.of(EASY_SPEED, MEDIUM_SPEED, HARD_SPEED, PRO_SPEED);

    float deltaTime = 0.026f; // hardcoded for now (~60fps)

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

        window = glfwCreateWindow(1920, 1080, "3D Shooting Arena", NULL, NULL);
        if (window == NULL) throw new RuntimeException("Failed to create GLFW window");

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1); // Enable v-sync
        glfwShowWindow(window);

        GL.createCapabilities();

        weaponModel = new Model("src/main/resources/models/Gun/weapon.obj", "Gun"); // adjust path as needed
        muzzleFlashTexture = TextureLoader.loadTextureFromResource("textures/Lava.jpg");

        groundTex = TextureLoader.loadTextureFromResource("textures/ground.jpg");


        Model panModel = new Model("src/main/resources/models/Pan/Frying_Pan.obj", "Pan");



        for(int i = 0, offset = 50; i < 4; i++, offset-= 25) {

            float SPEED = PAN_SPEEDS.get(i);

            pans.add(new PanEnemy(-42, PAN_HEIGHT, offset - 6, panModel, SPEED));
            pans.add(new PanEnemy(-42, PAN_HEIGHT, offset - 8, panModel, SPEED));
            pans.add(new PanEnemy(-32, PAN_HEIGHT, offset - 10, panModel, SPEED));
            pans.add(new PanEnemy(-35, PAN_HEIGHT, offset - 12, panModel, SPEED));
            pans.add(new PanEnemy(-32, PAN_HEIGHT, offset - 14, panModel, SPEED));
            pans.add(new PanEnemy(-42, PAN_HEIGHT, offset - 16, panModel, SPEED));
            pans.add(new PanEnemy(-42, PAN_HEIGHT, offset - 18, panModel, SPEED));
        }

//        pans.add(new PanEnemy(-42, 1.0f, 44, panModel, EASY_SPEED));
//        pans.add(new PanEnemy(-42, 1.0f, 42, panModel, EASY_SPEED));
//        pans.add(new PanEnemy(-32, 1.0f, 40, panModel, EASY_SPEED));
//        pans.add(new PanEnemy(-35, 1.0f, 38, panModel, EASY_SPEED));
//        pans.add(new PanEnemy(-32, 1.0f, 36, panModel, EASY_SPEED));
//        pans.add(new PanEnemy(-42, 1.0f, 34, panModel, EASY_SPEED));
//        pans.add(new PanEnemy(-42, 1.0f, 32, panModel, EASY_SPEED));




//        glEnable(GL_POLYGON_OFFSET_FILL);
//        glPolygonOffset(1.0f, 1.0f);

//        glDisable(GL_LIGHTING);



//        glEnable(GL_LIGHTING);
//        glEnable(GL_LIGHT0);
//        glEnable(GL_COLOR_MATERIAL);
//
//        float[] lightPos = {0f, 5f, 10f, 1f};
//        FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
//        buffer.put(lightPos).flip();
//        glLightfv(GL_LIGHT0, GL_POSITION, buffer);
//
//        glColorMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE);
//
//        glEnable(GL_LIGHT1); // Flashlight
//        glLightf(GL_LIGHT1, GL_CONSTANT_ATTENUATION, 0.5f);
//        glLightf(GL_LIGHT1, GL_LINEAR_ATTENUATION, 0.2f);
//        glLightf(GL_LIGHT1, GL_QUADRATIC_ATTENUATION, 0.05f);




        int brickWallTex = TextureLoader.loadTextureFromResource("textures/brick_wall.jpg");
        walls.add(new Wall(-50, 0, -50, 25, wallHeight, 1, brickWallTex, false));
        walls.add(new Wall(-25, 0, -50, 25, wallHeight, 1, brickWallTex, false));
        walls.add(new Wall(0, 0, -50, 25, wallHeight, 1, brickWallTex, false));
        walls.add(new Wall(25, 0, -50, 25, wallHeight, 1, brickWallTex, false));

        walls.add(new Wall(-50, 0, 50, 25, wallHeight, 1, brickWallTex, false));
        walls.add(new Wall(-25, 0, 50, 25, wallHeight, 1, brickWallTex, false));
        walls.add(new Wall(0, 0, 50, 25, wallHeight, 1, brickWallTex, false));
        walls.add(new Wall(25, 0, 50, 25, wallHeight, 1, brickWallTex, false));

        walls.add(new Wall(-50, 0, 50, 25, wallHeight, 1, brickWallTex, true));
        walls.add(new Wall(-50, 0, 25, 25, wallHeight, 1, brickWallTex, true));
        walls.add(new Wall(-50, 0, 0, 25, wallHeight, 1, brickWallTex, true));
        walls.add(new Wall(-50, 0, -25, 25, wallHeight, 1, brickWallTex, true));

        walls.add(new Wall(50, 0, 50, 25, wallHeight, 1, brickWallTex, true));
        walls.add(new Wall(50, 0, 25, 25, wallHeight, 1, brickWallTex, true));
        walls.add(new Wall(50, 0, 0, 25, wallHeight, 1, brickWallTex, true));
        walls.add(new Wall(50, 0, -25, 25, wallHeight, 1, brickWallTex, true));

//        walls.add(new Wall(-50, 0, 50, 25, wallHeight-5, 10, brickWallTex, false));
        walls.add(new Wall(-50, 0, 25, 25, wallHeight-5, 10, brickWallTex, false));
        walls.add(new Wall(-50, 0, 0, 25, wallHeight-5, 10, brickWallTex, false));
        walls.add(new Wall(-50, 0, -25, 25, wallHeight-5, 10, brickWallTex, false));







        // Enable 3D projection matrix
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        float aspect = 1920f / 1080f; // Window aspect ratio
        float fov = 60f;
        float zNear = 0.1f;
        float zFar = 120f;
        float ymax = (float) (Math.tan(Math.toRadians(fov / 2)) * zNear);
        float xmax = ymax * aspect;
        glFrustum(-xmax, xmax, -ymax, ymax, zNear, zFar);

        glMatrixMode(GL_MODELVIEW);


        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        glfwSetCursorPosCallback(window, (w, xpos, ypos) -> {
            if (firstMouse) {
                lastMouseX = xpos;
                lastMouseY = ypos;
                firstMouse = false;
            }

            float sensitivity = 0.1f;
            float xoffset = (float)(xpos - lastMouseX) * sensitivity;
            float yoffset = (float)(lastMouseY - ypos) * sensitivity;

            lastMouseX = xpos;
            lastMouseY = ypos;

            yaw += xoffset;
            pitch += yoffset;

            // Clamp pitch
            if (pitch > 89.0f) pitch = 89.0f;
            if (pitch < -89.0f) pitch = -89.0f;

            // Update direction vector
            dirX = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
            dirY = (float) Math.sin(Math.toRadians(pitch));
            dirZ = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        });

        glfwSetMouseButtonCallback(window, (w, button, action, mods) -> {
            if(button == GLFW_MOUSE_BUTTON_1){
                if (!reloading && ammo > 0) {
                    bullets.add(new Bullet(camX, camY, camZ, dirX, dirY, dirZ));
                    ammo--;
                    playSound("shoot.wav");

                    firing = true;
                    recoilOffset = recoilSpeed;

                    muzzleFlashActive = true;
                    muzzleFlashTimer = muzzleFlashDuration;


                    if (ammo == 0) {
                        reloading = true;
                        playSound("reload.wav");
                        reloadTimer = reloadTime;
                    }
                }
            } else if (button == GLFW_MOUSE_BUTTON_2) {
                if(!reloading){
                    ammo = 0;
                    reloading = true;
                    playSound("reload.wav");
                    reloadTimer = reloadTime;
                }
            }
        });


    }



    private void loop() {
        glEnable(GL_DEPTH_TEST); // Enable depth for 3D

        glClearColor(0.1f, 0.1f, 0.2f, 0.0f);

        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            glLoadIdentity();

            processInput();



            gluLookAt(camX, camY, camZ, camX + dirX, camY + dirY, camZ + dirZ, 0f, 1f, 0f);



            // Draw cube
//            drawCube();


            glClearColor(0.5f, 0.7f, 1.0f, 1.0f); // sky blue




            drawGround();
            for (Wall wall : walls) {
                wall.render();
            }

            Iterator<Bullet> iterator = bullets.iterator();
            while (iterator.hasNext()) {
                Bullet bullet = iterator.next();
                bullet.update();

                boolean hit = false;

//                for (Enemy enemy : enemies) {
//                    if (!enemy.alive) continue;
//
//                    float dx = bullet.x - enemy.x;
//                    float dy = bullet.y - enemy.y;
//                    float dz = bullet.z - enemy.z;
//
//                    float distSq = dx * dx + dy * dy + dz * dz;
//                    if (distSq < 1.0f) { // adjust size for accuracy
//                        enemy.alive = false;
//                        score++;
//                        playSound("hit.wav");
//                        System.out.println("Score: " + score);
//                        hit = true;
//                        break;
//                    }
//                }

                for (PanEnemy pan : pans) {
                    if (!pan.alive) continue;

//                    float minX = pan.x - panWidth / 2f;
//                    float maxX = pan.x + panWidth / 2f;
//                    float minY = pan.y;
//                    float maxY = pan.y + panHeight;
//                    float minZ = pan.z - panDepth / 2f;
//                    float maxZ = pan.z + panDepth / 2f;
//
//                    if (aabbCollision(bulletMinX, bulletMinY, bulletMinZ,
//                            bulletMaxX, bulletMaxY, bulletMaxZ,
//                            minX, minY, minZ, maxX, maxY, maxZ)) {

//                    if (aabbCollision(
//                            bullet.x - 0.05f, bullet.y - 0.05f, bullet.z - 0.05f,
//                            bullet.x + 0.05f, bullet.y + 0.05f, bullet.z + 0.05f,
//                            pan.x - 0.3f, pan.y, pan.z - 0.3f,
//                            pan.x + 0.3f, pan.y + 0.6f, pan.z + 0.3f)) {

                    float width = PanEnemy.WIDTH;
                    float height = PanEnemy.HEIGHT;
                    float depth = PanEnemy.DEPTH;

                    if (aabbCollision(
                            bullet.x - 0.05f, bullet.y - 0.05f, bullet.z - 0.05f,
                            bullet.x + 0.05f, bullet.y + 0.05f, bullet.z + 0.05f,
                            pan.x - width / 2f, pan.y, pan.z - depth / 2f,
                            pan.x + width / 2f, pan.y + height, pan.z + depth / 2f)) {


                        pan.alive = false;
                        score++;
                        playSound("hit.wav");
                        System.out.println("Score: " + score);
                        hit = true;
                        break;

                    }
                }

                if (!hit) {
                    for (Wall wall : walls) {
                        if (aabbCollision(
                                bullet.x - 0.05f, bullet.y - 0.05f, bullet.z - 0.05f,
                                bullet.x + 0.05f, bullet.y + 0.05f, bullet.z + 0.05f,
                                wall)) {
                            hit = true;
                            break;
                        }
                    }
                }




                // If no hit and bullet is within reasonable world bounds, render
                if (!hit && (Math.abs(bullet.x) < 100) && (Math.abs(bullet.y) < 100) && (Math.abs(bullet.z) < 100)) {
                    bullet.render();
                } else {
                    iterator.remove(); // Remove bullet if hit or out-of-bounds
                }
            }

            if (reloading) {
                reloadTimer -= 0.016; // approx. frame time @ 60fps
                if (reloadTimer <= 0) {
                    ammo = maxAmmo;
                    reloading = false;
                }
            }

            for (PanEnemy pan : pans) {
                pan.render();
            }

            for (PanEnemy pan : pans) {
                pan.update(deltaTime);
            }







//            for (Enemy enemy : enemies) {
//                if (!enemy.alive) {
//                    respawnTimer += 0.016;
//                    if (respawnTimer > 5.0f) {
//                        enemy.x = (float)(Math.random() * 10 - 5);
//                        enemy.z = (float)(Math.random() * -10 - 5);
//                        enemy.alive = true;
//                        respawnTimer = 0;
//                    }
//                } else {
//                    enemy.render();
//                }
//            }

            for (PanEnemy panEnemy : pans) {
                if (!panEnemy.alive) {
                    respawnTimer += deltaTime;
                    if (respawnTimer > respawnWait) {
                        panEnemy.alive = true;
                        respawnTimer = 0;
                    }
                } else {
                    panEnemy.render();
                }
            }




            drawCrosshair();
            drawHUD();
//            drawGun();

            // Recoil animation logic
            if (firing) {
                recoilOffset -= recoilRecovery;
                if (recoilOffset <= 0) {
                    recoilOffset = 0;
                    firing = false;
                }
            }

            if (muzzleFlashActive) {
                muzzleFlashTimer -= 0.016f; // assuming ~60 FPS
                if (muzzleFlashTimer <= 0) {
                    muzzleFlashActive = false;
                }
            }

            drawMuzzleFlash();
            drawWeaponModel();

//
//            FloatBuffer lightPos = BufferUtils.createFloatBuffer(4);
//            lightPos.put(new float[]{camX, camY, camZ, 1f}).flip(); // ✅ Now 4 values
//            glLightfv(GL_LIGHT1, GL_POSITION, lightPos);
//
//
//            float[] spotDirection = { dirX, dirY, dirZ, 0f };
//            glLightfv(GL_LIGHT1, GL_SPOT_DIRECTION, spotDirection);
//
//
//
//            glLightf(GL_LIGHT1, GL_SPOT_CUTOFF, 30f); // flashlight cone angle
//            glLightf(GL_LIGHT1, GL_SPOT_EXPONENT, 50f); // sharpness


            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }




    private void processInput() {
        float speed = 0.1f;

        // Normalize direction vector
        float length = (float) Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
        float dx = dirX / length;
//        float dy = dirY / length;
        float dz = dirZ / length;

        if(glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS){
            speed *= 5.0f;
        }

        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS && isGrounded) {
            yVelocity = jumpStrength;
            isGrounded = false;
        }



        // Gravity
        yVelocity += gravity;
        float futureFeetY = camY - playerHeight / 2f + yVelocity;

        // Check what you'd land on
        VerticalCollisionResult result = checkVerticalCollision(futureFeetY);

        if (result.colliding()) {
            camY = result.surfaceY() + playerHeight / 2f + 0.001f;
            yVelocity = 0;
            isGrounded = true;
        } else if (futureFeetY <= groundLevel) {
            camY = groundLevel + playerHeight / 2f;
            yVelocity = 0;
            isGrounded = true;
        } else {
            camY += yVelocity;
            isGrounded = false;
        }

        if (glfwGetKey(window, GLFW_KEY_F) == GLFW_PRESS) {
            flashlightOn = !flashlightOn;
            if (flashlightOn) {
                glEnable(GL_LIGHT1);
            } else {
                glDisable(GL_LIGHT1);
            }
        }


        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {

            float nextX = camX + (dx * speed);
            float nextZ = camZ + (dz * speed);

            float nextCamY = isGrounded ? camY : futureFeetY + playerHeight / 2f;

            if (!isCollidingWithObstacles(nextX, nextCamY, nextZ)) {
                camX = nextX;
                camZ = nextZ;
            }
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {

            float nextX = camX - (dx * speed);
            float nextZ = camZ - (dz * speed);

            float nextCamY = isGrounded ? camY : futureFeetY + playerHeight / 2f;

            if (!isCollidingWithObstacles(nextX, nextCamY, nextZ)) {
                camX = nextX;
                camZ = nextZ;
            }
        }

        // Strafe = cross product of direction and up vector (0,1,0)
        float strafeX = -dz;
        float strafeZ = dx;

        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            float nextX = camX - (strafeX * speed);
            float nextZ = camZ - (strafeZ * speed);

            float nextCamY = isGrounded ? camY : futureFeetY + playerHeight / 2f;

            if (!isCollidingWithObstacles(nextX, nextCamY, nextZ)) {
                camX = nextX;
                camZ = nextZ;
            }
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            float nextX = camX + (strafeX * speed);
            float nextZ = camZ + (strafeZ * speed);

            float nextCamY = isGrounded ? camY : futureFeetY + playerHeight / 2f;

            if (!isCollidingWithObstacles(nextX, nextCamY, nextZ)) {
                camX = nextX;
                camZ = nextZ;
            }
        }






    }


    private void gluLookAt(float eyeX, float eyeY, float eyeZ,
                           float centerX, float centerY, float centerZ,
                           float upX, float upY, float upZ) {
        FloatBuffer matrix = BufferUtils.createFloatBuffer(16);
        float[] forward = {
                centerX - eyeX,
                centerY - eyeY,
                centerZ - eyeZ
        };

        // Normalize forward
        float flen = (float) Math.sqrt(forward[0] * forward[0] + forward[1] * forward[1] + forward[2] * forward[2]);
        forward[0] /= flen; forward[1] /= flen; forward[2] /= flen;

        // Side = forward x up
        float[] side = {
                forward[1] * upZ - forward[2] * upY,
                forward[2] * upX - forward[0] * upZ,
                forward[0] * upY - forward[1] * upX
        };

        // Normalize side
        float slen = (float) Math.sqrt(side[0] * side[0] + side[1] * side[1] + side[2] * side[2]);
        side[0] /= slen; side[1] /= slen; side[2] /= slen;

        // Recalculate up
        float[] up = {
                side[1] * forward[2] - side[2] * forward[1],
                side[2] * forward[0] - side[0] * forward[2],
                side[0] * forward[1] - side[1] * forward[0]
        };

        matrix.put(new float[] {
                side[0], up[0], -forward[0], 0,
                side[1], up[1], -forward[1], 0,
                side[2], up[2], -forward[2], 0,
                0,       0,     0,           1
        });
        matrix.flip();

        glMultMatrixf(matrix);
        glTranslatef(-eyeX, -eyeY, -eyeZ);
    }




    private boolean aabbCollision(
            float minX1, float minY1, float minZ1,
            float maxX1, float maxY1, float maxZ1,
            Obstacle obs) {

        float half = obs.size / 2f;
        float minX2 = obs.x - half;
        float maxX2 = obs.x + half;
        float minY2 = obs.y - half;
        float maxY2 = obs.y + half;
        float minZ2 = obs.z - half;
        float maxZ2 = obs.z + half;

        return (minX1 < maxX2 && maxX1 > minX2) &&
                (minY1 < maxY2 && maxY1 > minY2) &&
                (minZ1 < maxZ2 && maxZ1 > minZ2);
    }

    private boolean aabbCollision(
            float minX1, float minY1, float minZ1,
            float maxX1, float maxY1, float maxZ1,
            Wall wall) {

        float wallWidth = wall.rotateY ? wall.depth : wall.width;
        float wallDepth = wall.rotateY ? wall.width : wall.depth;

        float minX2 = wall.x;
        float minY2 = wall.y;
        float minZ2 = wall.z;
        float maxX2 = wall.x + wallWidth;
        float maxY2 = wall.y + wall.height;
        float maxZ2 = wall.z + wallDepth;

        return (minX1 < maxX2 && maxX1 > minX2) &&
                (minY1 < maxY2 && maxY1 > minY2) &&
                (minZ1 < maxZ2 && maxZ1 > minZ2);
    }

    private boolean aabbCollision(
            float minX1, float minY1, float minZ1,
            float maxX1, float maxY1, float maxZ1,
            float minX2, float minY2, float minZ2,
            float maxX2, float maxY2, float maxZ2) {

        return (minX1 < maxX2 && maxX1 > minX2) &&
                (minY1 < maxY2 && maxY1 > minY2) &&
                (minZ1 < maxZ2 && maxZ1 > minZ2);
    }


    private boolean isCollidingWithObstacles(float x, float y, float z) {
        float feetOffset = 0.05f; // <--- ignore collisions at feet level

        float minY = y + feetOffset; // <--- bump the bottom of the AABB slightly upward
        float maxY = y + playerHeight;

//        for (Obstacle obs : obstacles) {
//            if (aabbCollision(
//                    x - playerRadius, minY, z - playerRadius,
//                    x + playerRadius, maxY, z + playerRadius,
//                    obs)) {
//                return true;
//            }
//        }
//
//        for (Enemy enemy : enemies) {
//            if (enemy.alive && aabbCollision(
//                    x - playerRadius, minY, z - playerRadius,
//                    x + playerRadius, maxY, z + playerRadius,
//                    enemy)) {
//                return true;
//            }
//        }

        for (Wall wall : walls) {
            if (aabbCollision(
                    x - playerRadius, minY, z - playerRadius,
                    x + playerRadius, maxY, z + playerRadius,
                    wall)) {
                return true;
            }
        }

        return false;
    }


    VerticalCollisionResult checkVerticalCollision(float futureFeetY) {
        float maxSurfaceY = Float.NEGATIVE_INFINITY;
        boolean collided = false;

        for (Obstacle obj : Stream.concat(
                obstacles.stream(),
                enemies.stream().filter(e -> e.alive)
        ).toList()) {

            float half = obj.size / 2f;
            float top = obj.y + half;

            // Only check XZ bounds for standing on top
            boolean withinXZ = camX > obj.x - half && camX < obj.x + half &&
                    camZ > obj.z - half && camZ < obj.z + half;

            if (withinXZ && futureFeetY <= top && top > maxSurfaceY) {
                maxSurfaceY = top;
                collided = true;
            }
        }

        return new VerticalCollisionResult(collided, maxSurfaceY);
    }

    private void drawCrosshair() {
        // Save current projection and modelview

        glMatrixMode(GL_PROJECTION);
        glPushMatrix();
        glLoadIdentity();
        glOrtho(0, 800, 0, 600, -1, 1);  // 2D mode (window coords)

        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        glLoadIdentity();

        // Draw lines at center of screen
        glDisable(GL_DEPTH_TEST); // Draw over everything
        glLineWidth(2.0f);
        glColor3f(1.0f, 1.0f, 1.0f); // White

        glBegin(GL_LINES);
        glVertex2f(400 - 10, 300); // Horizontal left
        glVertex2f(400 + 10, 300); // Horizontal right

        glVertex2f(400, 300 - 10); // Vertical top
        glVertex2f(400, 300 + 10); // Vertical bottom
        glEnd();

        glEnable(GL_DEPTH_TEST); // Re-enable depth

        // Restore previous projection and modelview
        glPopMatrix();
        glMatrixMode(GL_PROJECTION);
        glPopMatrix();
        glMatrixMode(GL_MODELVIEW);
    }

    private void drawGun() {
        glPushMatrix();

        glLoadIdentity();
        glTranslatef(0.6f, -0.5f, -1.0f); // Position it at bottom right of screen

        glBegin(GL_QUADS);
        glColor3f(0.2f, 0.2f, 0.2f); // dark gray

        // Front face
        glVertex3f(-0.1f, -0.1f,  0.1f);
        glVertex3f( 0.1f, -0.1f,  0.1f);
        glVertex3f( 0.1f,  0.1f,  0.1f);
        glVertex3f(-0.1f,  0.1f,  0.1f);

        // Back face
        glVertex3f(-0.1f, -0.1f, -0.1f);
        glVertex3f(-0.1f,  0.1f, -0.1f);
        glVertex3f( 0.1f,  0.1f, -0.1f);
        glVertex3f( 0.1f, -0.1f, -0.1f);

        // Top, bottom, sides...
        // (You can copy/paste the remaining cube faces like in your cube code)

        glEnd();

        glPopMatrix();
    }

    private void playSound(String filename) {
        try {
            InputStream audioSrc = getClass().getResourceAsStream("/sounds/" + filename);
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawHUD() {
        // Switch to orthographic 2D mode
        glMatrixMode(GL_PROJECTION);
        glPushMatrix();
        glLoadIdentity();
        glOrtho(0, 800, 0, 600, -1, 1); // (left, right, bottom, top)

        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        glLoadIdentity();

        glDisable(GL_DEPTH_TEST);
        glColor3f(1f, 1f, 1f);

        // Draw ammo bar
        float barWidth = 100;
        float filled = ((float) ammo / maxAmmo) * barWidth;
        glBegin(GL_QUADS);
        glColor3f(1f, 1f, 1f); // border
        glVertex2f(20, 560);
        glVertex2f(20 + barWidth, 560);
        glVertex2f(20 + barWidth, 580);
        glVertex2f(20, 580);

        glColor3f(0f, 1f, 0f); // green fill
        glVertex2f(20, 560);
        glVertex2f(20 + filled, 560);
        glVertex2f(20 + filled, 580);
        glVertex2f(20, 580);
        glEnd();

        // Draw a block "score" counter
        glColor3f(1f, 1f, 0f);
        for (int i = 0; i < score; i++) {
            float x = 20 + i * 12;
            glBegin(GL_QUADS);
            glVertex2f(x, 590);
            glVertex2f(x + 8, 590);
            glVertex2f(x + 8, 600);
            glVertex2f(x, 600);
            glEnd();
        }

        // Show "Reloading..." block
        if (reloading) {
            glColor3f(1f, 0f, 0f);
            glBegin(GL_QUADS);
            glVertex2f(140, 560);
            glVertex2f(250, 560);
            glVertex2f(250, 580);
            glVertex2f(140, 580);
            glEnd();
        }

        glEnable(GL_DEPTH_TEST);

        // Restore projection
        glPopMatrix();
        glMatrixMode(GL_PROJECTION);
        glPopMatrix();
        glMatrixMode(GL_MODELVIEW);
    }

    private void drawGround() {
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, groundTex); // ← use the texture you loaded

        glColor3f(1f, 1f, 1f); // No tint

        glBegin(GL_QUADS);

        float tileSize = 10.0f; // Change for tighter/looser tiling

        glTexCoord2f(0, 0);           glVertex3f(-50.0f, 0.0f, -50.0f);
        glTexCoord2f(0, tileSize);    glVertex3f(-50.0f, 0.0f, 50.0f);
        glTexCoord2f(tileSize, tileSize); glVertex3f(50.0f, 0.0f, 50.0f);
        glTexCoord2f(tileSize, 0);    glVertex3f(50.0f, 0.0f, -50.0f);

        glEnd();

//        glDisable(GL_TEXTURE_2D);

        // Red line across center (Z axis)
        // Draw red strip across the ground (Z axis)
        glDisable(GL_TEXTURE_2D);  // Disable texture
        glColor3f(1.0f, 0.0f, 0.0f);  // Red

        float stripX = -22.0f;        // X position
        float thickness = 3.0f;       // Width along X
        float length = 100.0f;        // Length along Z (50 to -50)
        float yOffset = 0.01f;        // Slightly above ground to avoid z-fighting

        glBegin(GL_QUADS);
        glVertex3f(stripX - thickness / 2, yOffset, -length / 2);
        glVertex3f(stripX + thickness / 2, yOffset, -length / 2);
        glVertex3f(stripX + thickness / 2, yOffset, length / 2);
        glVertex3f(stripX - thickness / 2, yOffset, length / 2);
        glEnd();


    }


    private void drawWeaponModel() {
        glPushMatrix();
        glLoadIdentity();

        glTranslatef(0.6f, -1.0f, -1.0f - recoilOffset);  // apply recoil to Z-axis


        glScalef(0.15f, 0.15f, 0.15f);
        glColor3f(0.6f, 0.6f, 0.6f);      // weapon color
        weaponModel.render();
        glPopMatrix();
    }

    /*private void drawMuzzleFlash() {
//        if (!muzzleFlashActive) return;

        glPushAttrib(GL_ALL_ATTRIB_BITS); // Save OpenGL state
        glPushMatrix();

        glLoadIdentity();

        // Translate to position in front of weapon
        glTranslatef(0.3f, -0.4f, -1.5f); // Tweak these values as needed

        // Add a random rotation for flicker effect
        glRotatef((float)(Math.random() * 360f), 0f, 0f, 1f);

        // Scale to desired size
        glScalef(0.1f, 0.1f, 1.0f); // Wide/flat muzzle flash

        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, muzzleFlashTex);

        // Enable additive blending for glow effect
        glDisable(GL_LIGHTING);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);

         Flash color (warm glow)
        float alpha = muzzleFlashTimer / muzzleFlashDuration;
        glColor4f(1.0f, 0.9f, 0.6f, alpha);


        // Draw the flash quad (billboard)
        glBegin(GL_QUADS);
        glVertex3f(-1, -1, 0);
        glVertex3f(1, -1, 0);
        glVertex3f(1, 1, 0);
        glVertex3f(-1, 1, 0);
        glEnd();*//*

        glColor4f(1f, 1f, 1f, 1f);

        glBegin(GL_QUADS);
        glTexCoord2f(0, 0); glVertex3f(-1, -1, 0);
        glTexCoord2f(1, 0); glVertex3f(1, -1, 0);
        glTexCoord2f(1, 1); glVertex3f(1, 1, 0);
        glTexCoord2f(0, 1); glVertex3f(-1, 1, 0);
        glEnd();

        glDisable(GL_TEXTURE_2D);

        glPopMatrix();
        glPopAttrib(); // Restore OpenGL state
    }*/




    private void drawMuzzleFlash() {
        if (!muzzleFlashActive || muzzleFlashTexture == 0) return;
//        System.out.println("Muzzle Flash Texture ID: " + muzzleFlashTexture);


        glPushAttrib(GL_ALL_ATTRIB_BITS);
        glPushMatrix();

        glLoadIdentity();

        // Position in front of weapon
        glTranslatef(0.3f, -0.4f, -1.5f);
        glRotatef((float)(Math.random() * 360f), 0f, 0f, 1f);
        glScalef(0.1f, 0.1f, 1.0f);

        // Enable texturing
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, muzzleFlashTexture);

        // Additive blending
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_LIGHTING);

        glColor4f(1.0f, 1.0f, 1.0f, 1.0f); // Use full brightness

        // Render textured quad
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0); glVertex3f(-1, -1, 0);
        glTexCoord2f(1, 0); glVertex3f( 1, -1, 0);
        glTexCoord2f(1, 1); glVertex3f( 1,  1, 0);
        glTexCoord2f(0, 1); glVertex3f(-1,  1, 0);
        glEnd();

        glDisable(GL_TEXTURE_2D); // Cleanup

        glPopMatrix();
        glPopAttrib();
    }







    public static void main(String[] args) {
        new Game().run();
    }
}

record VerticalCollisionResult(boolean colliding, float surfaceY) {}


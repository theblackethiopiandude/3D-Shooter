# 🔫 3D First-Person Shooting Arena (LWJGL)

A lightweight, fully functional 3D first-person shooting arena game built using **Java** and **LWJGL** (Lightweight Java Game Library). This project features a basic FPS controller, enemy AI, textured environments, weapon models, bullets, hit detection, jumping physics, and a HUD system.

## 🚀 Features

- ✅ First-person camera control (mouse + WASD movement)
- ✅ Jumping and gravity mechanics
- ✅ Textured weapon model with recoil animation
- ✅ Muzzle flash effect using textured billboard
- ✅ Shooting and bullet system with collision detection
- ✅ Dynamic enemies (PanEnemies) with sine-wave movement and respawn system
- ✅ Score and ammo display HUD
- ✅ Reloading mechanic with sound effects
- ✅ World building using textured walls, floors, and obstacles
- ✅ Custom OBJ model loading
- ✅ Sound effects (shooting, reload, hit)
- ✅ Crosshair overlay and visual HUD
- ✅ Collision detection with AABB (Axis-Aligned Bounding Boxes)
- ✅ Toggleable flashlight placeholder system

## 🧱 Technologies

- **Java 17+**
- **LWJGL 3** – for OpenGL bindings
- **OpenGL** – rendering
- **Wavefront OBJ** – model loading
- **WAV Audio** – sound effects
- **PNG/JPG** – textures

## 🎮 Controls

| Key / Mouse      | Action                    |
|------------------|---------------------------|
| `W A S D`        | Move forward, left, back, right |
| `SPACE`          | Jump                      |
| `SHIFT`          | Sprint                    |
| `Left Click`     | Shoot                     |
| `Right Click`    | Force Reload              |
| `Mouse Movement` | Look around               |

## 📂 Project Structure

```
src/
├── main/
│   ├── java/systems/binarydreamers/
│   │   ├── Game.java         # Main game loop and logic
│   │   ├── Bullet.java       # Bullet movement and rendering
│   │   ├── PanEnemy.java     # Enemy AI
│   │   ├── Wall.java         # Wall with texture
│   │   ├── Model.java        # OBJ model loader
│   │   └── TextureLoader.java# Texture loading utility
│   └── resources/
│       ├── models/           # 3D models (.obj)
│       ├── textures/         # Ground, muzzle flash, walls
│       └── sounds/           # shoot.wav, reload.wav, hit.wav
```

## 🛠️ Requirements

- Java 17+
- LWJGL 3
- OpenGL-capable system

## 🧪 To Run

1. Clone the repo:

   ```bash
   git clone https://github.com/theblackethiopiandude/3D-Shooter.git
   cd 3D-Shooter
   ```

2. Set up LWJGL (include native bindings in your IDE or build tool).
3. Set  VM Option flag for the 
4. Run `Game.java` as a Java application.

### macOS Note

If you are running this project on **macOS**, you must add the following VM option `-XstartOnFirstThread` when launching the game.
This is required for LWJGL/GLFW to work correctly due to macOS's windowing system restrictions.


## 📸 Screenshots


![Screenshot 2025-05-31 at 11.41.04 AM.png](src/main/resources/screenshots/Screenshot%202025-05-31%20at%2011.41.04%E2%80%AFAM.png)
![Screenshot 2025-05-31 at 11.41.30 AM.png](src/main/resources/screenshots/Screenshot%202025-05-31%20at%2011.41.30%E2%80%AFAM.png)

## 🗂️ Assets

- [Weapon OBJ model](#)
- [PanEnemy model](#)
- [Muzzle flash texture](#)
- Sound effects by you or from free resources

## 📃 License

This project is for educational/demo purposes. Please respect third-party asset licenses if included.

---

👾 **Built with love and code by Eyosiyas**

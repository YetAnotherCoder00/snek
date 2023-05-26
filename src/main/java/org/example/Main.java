package org.example;

import java.util.ArrayList;
import java.util.Random;

import static com.raylib.Jaylib.MAROON;
import static com.raylib.Jaylib.RAYWHITE;
import static com.raylib.Jaylib.BLACK;
import static com.raylib.Jaylib.DARKBLUE;

import static com.raylib.Raylib.*;

public class Main {

    static ArrayList<Vector2> positions = new ArrayList<>();

    public static void init(){
        positions.clear();
        Vector2 headPos = new Vector2().x(200).y(200);
        positions.add(headPos);
        positions.add(Vector2Add(headPos, new Vector2().x(-20)));
        positions.add(Vector2Add(headPos, new Vector2().x(-40)));
        positions.add(Vector2Add(headPos, new Vector2().x(-60)));
        positions.add(Vector2Add(headPos, new Vector2().x(-80)));
    }
    public static short moveSnake(float deltaTime, short direction, short lastDirection, float tickSpeed){

        if (deltaTime >= tickSpeed) {
            for (int i = positions.size() - 1; i >= 0; i--) {
                if (i > 0) positions.set(i, positions.get(i - 1));
            }
            if (direction == 0) {
                positions.set(0, Vector2Add(positions.get(0), new Vector2().x(20)));
                lastDirection = 0;
            } else if (direction == 1) {
                positions.set(0, Vector2Add(positions.get(0), new Vector2().y(20)));
                lastDirection = 1;
            } else if (direction == 2) {
                positions.set(0, Vector2Add(positions.get(0), new Vector2().x(-20)));
                lastDirection = 2;
            } else {
                positions.set(0, Vector2Add(positions.get(0), new Vector2().y(-20)));
                lastDirection = 3;
            }
        }

        return lastDirection;
    }

    public static short handleInput(short direction, short lastDirection){

        if (IsKeyPressed(KEY_LEFT) && lastDirection != 0) {
            direction = 2;
        } else if (IsKeyPressed(KEY_RIGHT) && lastDirection != 2) {
            direction = 0;
        } else if (IsKeyPressed(KEY_UP) && lastDirection != 1) {
            direction = 3;
        } else if (IsKeyPressed(KEY_DOWN) && lastDirection != 3) {
            direction = 1;
        }
        return direction;
    }

    public static int[] appleEaten(){
        System.out.println("apple eaten");
        int applePosX;
        int applePosY;
        Random rand = new Random();
        applePosX = rand.nextInt(1, 64) * 20;
        applePosY = rand.nextInt(1, 36) * 20;
        if (Vector2Subtract(positions.get(positions.size() - 1), positions.get(positions.size() - 2)).x() > 0) {
            positions.add(Vector2Add(positions.get(positions.size() - 1), new Vector2().x(-20)));
        }
        if (Vector2Subtract(positions.get(positions.size() - 1), positions.get(positions.size() - 2)).x() < 0) {
            positions.add(Vector2Add(positions.get(positions.size() - 1), new Vector2().x(20)));
        }
        if (Vector2Subtract(positions.get(positions.size() - 1), positions.get(positions.size() - 2)).y() > 0) {
            positions.add(Vector2Add(positions.get(positions.size() - 1), new Vector2().y(-20)));
        }
        if (Vector2Subtract(positions.get(positions.size() - 1), positions.get(positions.size() - 2)).y() < 0) {
            positions.add(Vector2Add(positions.get(positions.size() - 1), new Vector2().y(20)));
        }
        return new int[] {applePosX, applePosY};
    }

    public static boolean fruitCollide(int[] applePos){
        return CheckCollisionRecs(new Rectangle()
                        .x(positions.get(0).x())
                        .y(positions.get(0).y())
                        .width(20)
                        .height(20),
                new Rectangle()
                        .x(applePos[0])
                        .y(applePos[1])
                        .width(20)
                        .height(20));
    }

    public static boolean snakeCollide(){

        for (int i = positions.size() - 1; i > 0; i--) {
            if (CheckCollisionRecs(new Rectangle()
                            .x(positions.get(0).x())
                            .y(positions.get(0).y())
                            .width(20)
                            .height(20),
                    new Rectangle()
                            .x(positions.get(i).x())
                            .y(positions.get(i).y())
                            .width(20)
                            .height(20))) {
                return true;
            }
        }
        return false;
    }

    public static void draw(int score, int[] applePos){
        DrawRectangle(applePos[0], applePos[1], 20, 20, MAROON);

        for (int i = positions.size() - 1; i >= 0; i--) {
            if (i == 0) {
                DrawRectangle((int) (positions.get(i).x()),
                        (int) (positions.get(i).y()), 20, 20, DARKBLUE);
            } else {
                DrawRectangle((int) (positions.get(i).x()),
                        (int) (positions.get(i).y()), 20, 20, BLACK);
            }
        }

        BeginDrawing();
        ClearBackground(RAYWHITE);
        DrawText("Score: " + score, 600, 0, 20, BLACK);
        EndDrawing();
    }

    public static void wallCollision(){
        if (positions.get(0).x() < 0) {
            positions.set(0, new Vector2().x(1260).y(positions.get(0).y()));
        }
        if (positions.get(0).x() > 1260) {
            positions.set(0, new Vector2().x(0).y(positions.get(0).y()));
        }
        if (positions.get(0).y() < 0) {
            positions.set(0, new Vector2().x(positions.get(0).x()).y(700));
        }
        if (positions.get(0).y() > 700) {
            positions.set(0, new Vector2().x(positions.get(0).x()).y(0));
        }
    }

    public static boolean menu() {
        if (!IsKeyPressed(KEY_ESCAPE) && !WindowShouldClose()) {


            BeginDrawing();
            ClearBackground(RAYWHITE);
            DrawText("Welcome to Snek", 500, 200, 20, BLACK);
            EndDrawing();
            return GuiButton(new Rectangle().x(500).y(300).width(200).height(50),
                    "Press here to start");
        }
        return false;
    }

    public static void gameLoop(short direction, short lastDirection){
        InitWindow(1280, 720, "Snek");
        SetExitKey(0);
        SetTargetFPS(60);
        while (!WindowShouldClose() && !IsKeyPressed(KEY_ESCAPE)) {
            if(menu()){
                init();
                int score = 0;
                float tickSpeed = 0.5F;
                float deltaTime = 0;
                int[] applePos = {300, 200};
                while (true) {
                    deltaTime += GetFrameTime();
                    direction = handleInput(direction, lastDirection);
                    draw(score, applePos);
                    lastDirection = moveSnake(deltaTime, direction, lastDirection, tickSpeed);
                    if (snakeCollide()) break;
                    wallCollision();
                    if (fruitCollide(applePos)) {
                        applePos = appleEaten();
                        score += 10;
                        if (score <= 100) tickSpeed = 0.5F;
                        else if (score <= 200) tickSpeed = 0.4F;
                        else if (score <= 300) tickSpeed = 0.3F;
                        else if (score <= 400) tickSpeed = 0.2F;
                        else if (score <= 500) tickSpeed = 0.1F;
                        else if (score <= 600) tickSpeed = 0.05F;
                        else tickSpeed = 0.005F;
                    }
                    if (deltaTime >= tickSpeed) {
                        deltaTime = 0;
                    }
                    if (IsKeyPressed(KEY_ESCAPE) || WindowShouldClose()) {
                        break;
                    }
                }
            }
        }
    }


    public static void main(String[] args) {

        short startDirection = 0; // 0 = right, 1 = down, 2 = left, 3 = up
        short startLastDirection = 2;

        gameLoop(startDirection, startLastDirection);
    }
}
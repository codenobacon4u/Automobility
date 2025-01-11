package io.github.foundationgames.automobility.automobile;

import java.util.List;

import org.joml.Vector3f;

import io.github.foundationgames.automobility.automobile.AutomobileEngine.ExhaustPos;

public class CustomEngine {
    public String name;
    public String modelId;
    public String soundPath;
    public String texturePath;
    public String modelPath;
    public float torque;
    public float speed;
    public List<ExhaustPos> exhaustPos;
    public Vector3f scale;
    public float yRot;
}

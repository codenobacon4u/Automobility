package io.github.foundationgames.automobility.automobile;

import java.util.List;

import org.joml.Vector3f;

import io.github.foundationgames.automobility.automobile.WheelBase.WheelPos;

public class CustomFrame {
    public String name;
    public String modelId;
    public String texturePath;
    public String modelPath;
    public float weight;
    public List<WheelPos> wheelBase;
    public float lengthPx;
    public float seatHeight;
    public float enginePosBack;
    public float enginePosUp;
    public float rearAttachmentPos;
    public float frontAttachmentPos;
    public Vector3f scale;
    public float yRot;
}
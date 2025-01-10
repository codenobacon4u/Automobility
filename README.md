![Automobility](./md/banner.png)

### A Minecraft mod adding customizable vehicles.
**Automobility is currently in BETA!** <br/>
The mod isn't feature-complete, and any feature requests would be greatly appreciated. Discuss features on the mod's GitHub Discussions page, or in the [Discord](https://discord.gg/7Aw3y4RtY9).

## Available for Fabric/Quilt and Forge
- Requires **[Fabric API](https://modrinth.com/mod/fabric-api)** (Fabric) or **[QSL](https://modrinth.com/mod/qsl)** (Quilt)
- No additional mods required for Forge

**Currently maintained for version 1.19.2, older versions are retired.**

## Getting Started
- **Recipes:** Crafting recipes can be viewed using [**EMI**](https://www.curseforge.com/minecraft/mc-mods/emi) (Fabric/Quilt) or [**JEI**](https://www.curseforge.com/minecraft/mc-mods/jei) (Forge).
- **Automobile Parts:** Craft an Auto Mechanic Table. Use the GUI to craft the frame, engine, and wheels for your automobile. You can optionally craft an attachment.
- **Building your Automobile:** Craft an Automobile Assembler, as well as a Crowbar. Place parts on the assembler until the vehicle is complete. Use a crowbar to destroy the vehicle.
- **Enhancing your Automobile:** Use your Auto Mechanic Table to craft attachments, which can be placed on your Automobile to add exciting functionality or utility.
- **Building:** You can use Slopes, Dash Panels, Off-Road tiles and more to build roads, racetracks, or obstacle courses.

![Automobile Construction](./md/construction.png)
![Automobile Types](./md/parking.png)

## Driving
- W - Accelerate
- S - Brake/Reverse/Burnout
- A/D - Steer left/right
- Space - Drift/Deploy

**Controller Support (*Fabric and Quilt Exclusive*):** when using [MidnightControls](https://www.curseforge.com/minecraft/mc-mods/midnightcontrols), you will be able to control automobiles with the following default controls:
- A - Accelerate
- B - Brake/Reverse
- LStick - Steer left/right
- RTrigger - Drift/Deploy

![Driving](./md/driving.png)

## Custom Models
Custom automobile models can be added through resourcepacks.

*NOTE:*
- If you disable the resource pack while a custom model is in the world your game will crash
- Currently only custom frames are supported.

### Creating custom models
To add a custom model, it must be exported into [JsonEM](https://github.com/FoundationGames/JsonEM) format. 
- Follow the instructions [here](https://github.com/FoundationGames/JsonEM?tab=readme-ov-file#edit-models-in-blockbench) to export the model to the correct format in Blockbench.
- Ensure that the exported file is saved as `assets/automobility/models/entity/automobile/<part_type>/<model_name>/main.json` in the resource pack
- Save the texture to `assets/automobility/textures/entity/automobile/<part_type>/<texture_name>.png` in the resource pack (we will need this location for later)
- Add a `assets/automobility/custom/<part_type>` folder to the resource pack
- Add a new json file with the definition of the part. For example, for a frame defined in `sportscar.json`:
```json
{
    "name": "<model_name>", // e.g: sportscar
    "modelId": "<model_id>", // e.g: frame_sportscar
    "texturePath": "textures/entity/automobile/<part_type>/<texture_name>.png", // from previous step
    "modelPath": "automobile/<part_type>/<model>", // e.g.: automobile/frame/sportscar
    "weight": "0.9f", // how heavy the car is
    "wheelBase": [ // defines where the wheels should be rendered relative to the center
        {
            "end": "BACK",
            "side": "LEFT",
            "forward": -12,
            "right": -6.8,
            "scale": 1,
            "yaw": 0
        },
        {
            "end": "BACK",
            "side": "RIGHT",
            "forward": -12,
            "right": 6.8,
            "scale": 1,
            "yaw": 180
        },
        {
            "end": "FRONT",
            "side": "LEFT",
            "forward": 12,
            "right": -6.8,
            "scale": 1,
            "yaw": 0
        },
        {
            "end": "FRONT",
            "side": "RIGHT",
            "forward": 12,
            "right": 6.8,
            "scale": 1,
            "yaw": 180
        }
    ],
    "lengthPx": "45", // length of frame
    "seatHeight": "7", // how high the player sits
    "enginePosBack": "9", // forward/back location of the engine
    "enginePosUp": "2", // height of the engine
    "rearAttachmentPos": "18", // offset for rear attachments
    "frontAttachmentPos": "19", // offset for front attachments
    "yRot": -90, // optional: if the model needs to be rotated to look right
    "scale": { // optional: if the model needs to be scaled up to look right
        "x": 1.2,
        "y": 1.2,
        "z": 1.2
    }
}
```


### Credit: Audio
All sound effects used (originals licensed under CC0) from [freesound.org](https://freesound.org/): <br/>
- [ENGINE~1.WAV](https://freesound.org/people/MarlonHJ/sounds/242739/) *by MarlonHJ* <br/>
- [Marine diesel engine](https://freesound.org/people/AugustSandberg/sounds/264864/) *by AugustSandberg* <br/>
- [metal_ring_01.wav](https://freesound.org/people/Department64/sounds/95272/) *by Department64* <br/>
- [metalbang0.wav](https://freesound.org/people/SamsterBirdies/sounds/435699/) *by SamsterBirdies* <br/>
- [Hollow Bang](https://freesound.org/people/qubodup/sounds/157609/) *by qubodup* <br/>
- [car park skiding corner.wav](https://freesound.org/people/martian/sounds/178889/) *by martian* <br/>
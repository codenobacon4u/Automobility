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

***When creating a resource pack with custom models, make sure to put the zip file in both your resource pack folder and datapack folder so the new models and recipes are loaded.***

*NOTE:*
- If you disable the resource pack while a custom model is in the world your game will crash
- Currently only custom frames are supported.

### Creating custom models
To add a custom model, it must be exported into [JsonEM](https://github.com/FoundationGames/JsonEM) format. 
- Follow the instructions [here](https://github.com/FoundationGames/JsonEM?tab=readme-ov-file#edit-models-in-blockbench) to export the model to the correct format in Blockbench.
- Ensure that the exported file is saved as `assets/automobility/models/entity/automobile/<part_type>/<model_name>/main.json` in the resource pack
- Save the texture to `assets/automobility/textures/entity/automobile/<part_type>/<texture_name>.png` in the resource pack (we will need this location for later)
- Add a `assets/automobility/custom/<part_type>` folder to the resource pack
- Add a new json file with the definition of the part. 

#### Custom Frame Example (`sportscar.json`):
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

#### Custom Engine Example (`v8.json`):
```json
{
    "name": "<model_name>", // e.g: v8
    "modelId": "<model_id>", // e.g: engine_v8
    "soundPath": "entity.automobile.<name>", // see common/src/main/resources/assets/automobility/sounds.json and sounds folder for more info on this
    "texturePath": "textures/entity/automobile/<part_type>/<texture_name>.png", // from previous step
    "modelPath": "automobile/<part_type>/<model>", // e.g.: automobile/engine/v8
    "torque": "0.9f", // how fast the car gets up to speed
    "speed": "1.0f", // how fast the car is
    "exhaustPos": [ // defines how many exhaust pipes the engine has and where they are positioned
        {
            "x": 3,
            "y": 3.8,
            "z": -7.6,
            "pitch": 40,
            "yaw": 0
        },
        {
            "x": -3,
            "y": 3.8,
            "z": -7.6,
            "pitch": 1,
            "yaw": 10
        },
        {
            "x": 4,
            "y": 7.075,
            "z": -4.95,
            "pitch": 40,
            "yaw": 10
        },
        {
            "x": -4,
            "y": 7.075,
            "z": -4.95,
            "pitch": 40,
            "yaw": 0
        }
    ],
    "yRot": -90, // optional: if the model needs to be rotated to look right
    "scale": { // optional: if the model needs to be scaled up to look right
        "x": 1.2,
        "y": 1.2,
        "z": 1.2
    }
}
```

#### Custom Wheel Example (`monster.json`):
```json
{
    "name": "<model_name>", // e.g: monster
    "modelId": "<model_id>", // e.g: wheel_monster
    "texturePath": "textures/entity/automobile/<part_type>/<texture_name>.png", // from previous step
    "modelPath": "automobile/<part_type>/<model>", // e.g.: automobile/wheel/monster
    "size": "2.0f", // how big the wheel should be (for collions and block step-ups)
    "grip": "1.0f", // how much the car will drift
    "radius": "9.5f", // radius of the wheel model
    "width": "6.0f", // width of the wheel model
    "yRot": -90, // optional: if the model needs to be rotated to look right
    "scale": { // optional: if the model needs to be scaled up to look right
        "x": 2,
        "y": 2,
        "z": 2
    }
}
```

### Adding recipes
Recipes can be added to the same zip file
- Add recipes to the `data/automobility/recipes/<part_type>/` folder as a json file
- All auto mechanic recipes (frame, engine, wheel) follow the following template (example for a frame): 
```json
{
	"type": "automobility:auto_mechanic_table",
	"category": "automobility:frames",
	"sortnum": 508,
	"ingredients": [
			{"item": "minecraft:copper_block"},
			{"item": "minecraft:red_dye"},
			{"item": "minecraft:leather"},
			{"item": "minecraft:iron_ingot"},
			{"item": "minecraft:bucket"}
	],
	"result": {
			"item": "automobility:automobile_frame",
			"component": "automobility:sportscar_red"
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
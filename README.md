# Custom Dimension Spawn (Fabric)

- Add custom coordinate ranges and biome checks. It will check if the player died in a certain coordinate range and if `dimensionId` is present it will also check if the player died in that dimension then respawn them to coord(positionX, positionY, positionZ, yaw, pitch, dimensionRespawn(If not present then it will check for dimensionId, and if that is also that is not present it will just use the dimension that the player died in)). Then it will check if the player died in a certain biome and if `dimensionId` is present it will also check if the player died in that dimension then respawn them to coord(positionX, positionY, positionZ, yaw, pitch, dimensionRespawn(If not present then it will check for dimensionId, and if that is also that is not present it will just use the dimension that the player died in)). Then lastly it will check if the player dies in `dimensionId` then respawn them to coord(positionX, positionY, positionZ, yaw, pitch, dimensionRespawn(If not present then it will check for dimensionId, and if that is also that is not present it will just use the dimension that the player died in))
```json
[
    {
        "dimensionId": "minecraft:overworld",
        "dimensionRespawn": "minecraft:the_nether",
        "coordinateRangeList": [
            {
                "startX": 100.0,
                "startY": 100.0,
                "startZ": 100.0,
                "endX": -100.0,
                "endY": -100.0,
                "endZ": -100.0
            },
            {
                "startX": 250.0,
                "startY": 250.0,
                "startZ": 250.0,
                "endX": 500.0,
                "endY": 500.0,
                "endZ": 500.0
            }
        ],
        "biomeList": [
            "minecraft:plains",
            "minecraft:ocean"
        ]
        "positionX": 0.0,
        "positionY": 129.0,
        "positionZ": 0.0,
        "yaw": 0.0,
        "pitch": 0.0
    }
]
```
- Add ability to configure spawner block break xp drop per dimension
```json
{
    "minecraft:the_nether": false,
    "custom:custom_dimension": false
}
```
- Add an advancement granter after a block entity with certain NBT are met.
```json
{
  "{SpawnCount:4s,MaxNearbyEntities:6s}": "example:advancement",
  "{SpawnCount:4s}": "minecraft:adventure/avoid_vibration",
  "{SpawnCount:0}": "example:advancement3"
}
```
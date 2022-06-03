# Getting started

## Folder structure

- `./machines.dat` all the metadata stored in machines
- `./pack` contains all the additions and the generated resourcepack to include in the client
    - `./pack/crafts/<modname>` contains all the crafts for a mod
        - `./pack/crafts/<modname>/<craft-id>.json` A file representing the craft
          See [CRAFTS](#Crafts)
        - `./pack/items/<modname>` contains all the items for a given mod
            - `./pack/items/<modname>/<item-id>.json` A file representing the item
              See [ITEMS](#Items)
        - `./pack/machines/<modname>` contains all the machines for a given mod
            - `./pack/machines/<modname>/<item-id>.json` A file representing the machine
              See [MACHINES](#Machines)
            - `./pack/textures/<modname>` contains all the items for a given mod
                - `./pack/textures/<modname>/<item-id>.png` Will place a texture on <item-id> in <mod-name>

## Json object types

### Item

```json 
"<namespace: String>:<identifier: String>"
```

Example:

```json 
"mymod:anitem"
```

### ItemStack

Can be an item or a json object like:

```json 
{
    "type": <item: Item>,
    "amount": <amount: UInt>, // Optional
    "properties": <properties: Properties>, // Optional
}
```

### Properties

Properties are fundamental to how McMachines work they encode all the information related to a machine.

All the properties used in McMachines

| Properties identifier | Type | Description                                                          |
|-----------------------|------|----------------------------------------------------------------------|
| id                    | Item | An id describe what an machine or an item is                         |
| energy_out            | UInt | The amount of energy a machine can output at a given time            |
| energy_in             | UInt | The amount of energy a machine can input at a given time             |
| energy_content        | UInt | The amount of energy stored                                          |
| energy_max            | UInt | The maximum amount of energy                                         |
| hold_resistance       | UInt | The /1000 of energy lost each second by a machine storing energy     |
| charge_per_tick       | UInt | The amount of energy a charger can force into an item per second     |
| gen_per_second        | UInt | The amount of energy a generator will generate in one second         |
| gen_remaining_secs    | UInt | The number of seconds the generator will continue to generate energy |

Example of properties in JSON

```json
{
  "energy_max": 1000,
  "energy_in": 100
}
```

### ItemStack

## Crafts

### `minecraft:crafting_table`

The file is as follows:

```json
{
  "on": "minecraft:crafting_table",
  "output": <output_itemstack
  :
  ItemStack>,
  "inputs": [
    [
      <item1
      :
      Item>,
      <item2
      :
      Item>,
      <item3
      :
      Item>
    ],
    [
      <item4
      :
      Item>,
      <item5
      :
      Item>,
      <item6
      :
      Item>
    ],
    [
      <item7
      :
      Item>,
      <item8
      :
      Item>,
      <item9
      :
      Item>
    ]
  ]
}
```

**Note:** You can use smaller grid sizes the array should just remain a square

Example:

```json 
{
  "on": "minecraft:crafting_table",
  "output": "base:atomic_disassembler",
  "inputs": [
    [
      "minecraft:iron_block",
      "base:graphite",
      "minecraft:iron_block"
    ],
    [
      "base:graphite",
      "minecraft:diamond_axe",
      "base:graphite"
    ],
    [
      "minecraft:iron_block",
      "base:graphite",
      "minecraft:iron_block"
    ]
  ]
}
```

### Transformer craft

```json
{
  "on": <machine_id
  :
  Item>,
  "energy_use": <energy-usage
  :
  UInt>,
  "inputs": [
    [
      <input_item_1
      :
      Item>,
      <input_item_1_number
      :
      UInt>
    ]
    // More input items are allowed
  ],
  "outputs": [
    [
      <output_itemstack_1
      :
      ItemStack>,
      <output_itemstack_1_percentage_of_drop
      :
      UInt>
    ]
    // More output items are allowed
  ]
}
```

Example:

```json
{
  "on": "base:crusher",
  "energy_use": 1000,
  "inputs": [
    [
      "minecraft:coal",
      3
    ]
  ],
  "outputs": [
    [
      "base:graphite",
      120
    ],
    [
      {
        "type": "minecraft:coal",
        "amount": 2
      },
      50
    ]
  ]
}
```

### Generator craft

```json
{
  "on": <machine_id
  :
  Item>,
  "energy_production": <amount_of_energy
  :
  UInt>,
  "tick_duration": <time_in_seconds
  :
  UInt>,
  "inputs": [
    [
      <input_item_1
      :
      Item>,
      <input_item_1_number
      :
      UInt>
    ]
  ],
  "outputs": [
    [
      <output_itemstack_1
      :
      ItemStack>,
      <output_itemstack_1_percentage_of_drop
      :
      UInt>
    ]
  ]
}
```

Example:

```json
{
  "on": "base:generator",
  "energy_production": 400,
  "tick_duration": 36,
  "inputs": [
    [
      "minecraft:lava_bucket",
      1
    ]
  ],
  "outputs": [
    [
      "minecraft:bucket",
      100
    ]
  ]
}
```

## Items

```json 
{
  "name": <item_name: String>,
  "base": <minecraft_item: Item>,
  "handler": [ // Optional
    <handler: String>
    // More than one handler can be used
  ],
  "properties": <properties: Properties> // Optional
}
```

Example:

```json 
{
  "name": "&aAtomic Disassembler",
  "base": "minecraft:diamond_axe",
  "handler": [
    "atomicDisassembler"
  ],
  "properties": {
    "energy_max": 100000
  }
}
```

## Machines

```json 
{
  "name": <machine_name: String>,
  "mode": "SimpleCharger" | "SimpleTransformer" | "BurningGenerator" | "ConstantGenerator",
  "properties": <properties: Properties>
}
```

```json 
{
  "name": "&7Charger",
  "mode": "SimpleCharger",
  "properties": {
    "energy_max": 10000,
    "energy_in": 2000,
    "charge_per_tick": 1000
  }
}
```
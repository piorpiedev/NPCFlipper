# NPCFlipper
A simple 1.21 mod that automatically gets items from a buy order, and mass sells them to an NPC

All delays are randomized, the macro is automatically disabled on movement and location changes, 
and mouse grabbing is disabled while the macro is running, so it's entirely possible to run the macro in windowed mode

The code is highly modular, so to make it very easy to create new macros, or edit existing ones

> [!Note]
> A Booster Cookie is required to open bz and sell the items

## Usage
There is currently only one command: `/npcflip <command> <CMD|GUI> <itemType>`
- `<command>`: The command used to open the bazaar (ex: `/bz` or `/pickupstash`). The leading slash is added by default if omitted
- `<CMD|GUI>`: Either "gui" if the previous command opened the bazaar gui, or "cmd" if the macro should assume the command was enough to pick up the items
- `<itemType>` The raw minecraft vanilla name of the item to be flipped. No need for the "minecraft:prefix"

package com.piorpie.npcflipper.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.piorpie.npcflipper.client.macro.MacroRunner;
import com.piorpie.npcflipper.client.macro.Mode;
import com.piorpie.npcflipper.client.macro.Utils;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class MainClient implements ClientModInitializer {
    public static final KeyBinding toggleMacroKey = new KeyBinding(
            "key.npcflipper.togglemacro",
            GLFW.GLFW_KEY_F9,
            "category.npcflipper"
    );

    public static final MacroRunner macroRunner = new MacroRunner();

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(toggleMacroKey);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleMacroKey.wasPressed()) {
                macroRunner.stop(null);
            }
            macroRunner.tick();
        });
        ClientCommandRegistrationCallback.EVENT.register(this::registerCommands);
        ClientPlayConnectionEvents.JOIN.register(((handler, sender, client) -> {
            if (macroRunner.isRunning()) macroRunner.stop("The player has changed dimension!");
        }));
    }

    private void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(ClientCommandManager.literal("npcflip")
                .executes(ctx -> {
                    Utils.printMsg(MinecraftClient.getInstance(), "§c[NPCFlipper] Usage: /npcflip <command> <CMD|GUI> <itemType...>");
                    return 0;
                })
                .then(ClientCommandManager.argument("command", StringArgumentType.word())
                        .executes(ctx -> {
                            Utils.printMsg(MinecraftClient.getInstance(), "§c[NPCFlipper] Usage: /npcflip <command> <CMD|GUI> <itemType...>");
                            return 0;
                        })
                        .then(ClientCommandManager.argument("mode", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    Utils.printMsg(MinecraftClient.getInstance(), "§c[NPCFlipper] Usage: /npcflip <command> <CMD|GUI> <itemType...>");
                                    return 0;
                                })
                                .then(ClientCommandManager.argument("item", StringArgumentType.word())
                                    .executes(ctx -> {
                                        String itemType = StringArgumentType.getString(ctx, "item").replace(' ', '_');
                                        Item item;
                                        try {
                                            item = Registries.ITEM.get(Identifier.of("minecraft", itemType));
                                            if (item == Items.AIR) throw new Exception("Invalid item type");
                                        } catch (Exception e) {
                                            Utils.printMsg(MinecraftClient.getInstance(), "§c[NPCFlipper] Invalid item type (Use vanilla types!)");
                                            return 0;
                                        }

                                        String cmd = StringArgumentType.getString(ctx, "command");
                                        cmd = cmd.startsWith("/") ? cmd : "/" + cmd;
                                        String modeS = StringArgumentType.getString(ctx, "mode");
                                        Mode mode = Mode.valueOf(modeS);

                                        macroRunner.start(cmd, mode, item);
                                        return 1;
                                    })
                                )
                        )
                )
        );
    }
}

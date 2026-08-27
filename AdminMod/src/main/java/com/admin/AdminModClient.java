package com.admin;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.ArgumentBuilder.argument;

public class AdminModClient implements ClientModInitializer {

    private static boolean isFollowing = false;
    private static boolean isMining = false;
    private static String targetAdminName = "Admin";

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            
            dispatcher.register(literal("admin")
                .then(argument("name", word())
                    .executes(context -> {
                        targetAdminName = com.mojang.brigadier.arguments.StringArgumentType.getString(context, "name");
                        context.getSource().sendFeedback(Text.literal("§b[AdminMod] Hedef admin ayarlandı: " + targetAdminName));
                        return 1;
                    })
                )
            );

            dispatcher.register(literal("takip").executes(context -> {
                isFollowing = !isFollowing;
                isMining = false;
                context.getSource().sendFeedback(Text.literal("§a[AdminMod] Takip Modu (" + targetAdminName + "): " + (isFollowing ? "Aktif" : "Kapalı")));
                return 1;
            }));

            dispatcher.register(literal("kaz").executes(context -> {
                isMining = !isMining;
                isFollowing = false;
                context.getSource().sendFeedback(Text.literal("§a[AdminMod] 10x10 Kazı Modu: " + (isMining ? "Aktif" : "Kapalı")));
                return 1;
            }));

            dispatcher.register(literal("birak").executes(context -> {
                dropJunkItems(MinecraftClient.getInstance());
                context.getSource().sendFeedback(Text.literal("§e[AdminMod] Çöpler " + targetAdminName + " üstüne bırakılıyor."));
                return 1;
            }));
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;
            if (client.player.getHungerManager().getFoodLevel() < 10) return;
            // Kod döngüleri
        });
    }

    private static void dropJunkItems(MinecraftClient client) {
        if (client.player == null) return;
        for (int i = 0; i < client.player.getInventory().size(); i++) {
            ItemStack stack = client.player.getInventory().getStack(i);
            if (stack.isOf(Items.COBBLESTONE) || stack.isOf(Items.DIORITE) || 
                stack.isOf(Items.DIRT) || stack.isOf(Items.GRAVEL)) {
                // Eşya bırakma simülasyonu
            }
        }
    }
}
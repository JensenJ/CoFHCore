package cofh.core.command;

import cofh.core.effect.PanaceaEffect;
import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

import static cofh.lib.util.constants.Constants.CMD_TARGETS;
import static cofh.lib.util.constants.Constants.MAX_FOOD_LEVEL;

public class SubCommandHeal {

    public static int permissionLevel = 2;

    static ArgumentBuilder<CommandSourceStack, ?> register() {

        return Commands.literal("heal")
                .requires(source -> source.hasPermission(permissionLevel))
                // Self
                .executes(context -> healEntities(context.getSource(), ImmutableList.of(context.getSource().getPlayerOrException())))
                // Targets Specified
                .then(Commands.argument(CMD_TARGETS, EntityArgument.players())
                        .executes(context -> healEntities(context.getSource(), EntityArgument.getPlayers(context, CMD_TARGETS))));
    }

    private static int healEntities(CommandSourceStack source, Collection<? extends ServerPlayer> targets) {

        for (ServerPlayer entity : targets) {
            // Extinguish Fire
            entity.clearFire();
            // Clear all negative effects
            PanaceaEffect.clearHarmfulEffects(entity);
            // Set to Max Air
            entity.setAirSupply(entity.getMaxAirSupply());
            // Set to Max Food
            entity.getFoodData().eat(MAX_FOOD_LEVEL, 5.0F);
            // Heal to Max Health
            entity.setHealth(entity.getMaxHealth());
        }
        if (targets.size() == 1) {
            source.sendSuccess(Component.translatable("commands.cofh.heal.success.single", targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendSuccess(Component.translatable("commands.cofh.heal.success.multiple", targets.size()), true);
        }
        return targets.size();
    }

}

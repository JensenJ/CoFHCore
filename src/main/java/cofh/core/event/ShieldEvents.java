package cofh.core.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static cofh.lib.capability.CapabilityShieldItem.SHIELD_ITEM_CAPABILITY;
import static cofh.lib.util.constants.Constants.ID_COFH_CORE;

@Mod.EventBusSubscriber (modid = ID_COFH_CORE)
public class ShieldEvents {

    private ShieldEvents() {

    }

    @SubscribeEvent (priority = EventPriority.HIGH)
    public static void handleLivingAttackEvent(LivingAttackEvent event) {

        if (event.isCanceled()) {
            return;
        }
        LivingEntity entity = event.getEntityLiving();
        DamageSource source = event.getSource();

        if (!canBlockDamageSource(entity, source)) {
            return;
        }
        if (source instanceof EntityDamageSource && ((EntityDamageSource) source).isThorns()) {
            return;
        }
        ItemStack shield = entity.getUseItem();
        shield.getCapability(SHIELD_ITEM_CAPABILITY).ifPresent(cap -> cap.onBlock(entity, source, event.getAmount()));
    }

    // region HELPERS
    public static boolean canBlockDamageSource(LivingEntity living, DamageSource source) {

        Entity entity = source.getDirectEntity();
        if (entity instanceof AbstractArrowEntity) {
            AbstractArrowEntity arrow = (AbstractArrowEntity) entity;
            if (arrow.getPierceLevel() > 0) {
                return false;
            }
        }
        if (!source.isBypassArmor() && living.isBlocking()) {
            return canBlockDamagePosition(living, source.getSourcePosition());
        }
        return false;
    }

    public static boolean canBlockDamagePosition(LivingEntity living, Vector3d sourcePos) {

        if (sourcePos != null) {
            return new Vector3d(living.getX() - sourcePos.x(), 0.0D, living.getZ() - sourcePos.z()).dot(living.getViewVector(1.0F)) < 0.0D;
        }
        return false;
    }
    // endregion
}

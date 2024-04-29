package gay.pridecraft.joymod.mixin;

import gay.pridecraft.joymod.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow public abstract ItemStack getStackInHand(Hand hand);

    @Shadow public abstract void setHealth(float health);

    @Shadow public abstract boolean clearStatusEffects();

    @Shadow public abstract boolean addStatusEffect(StatusEffectInstance effect);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(at = @At("HEAD"), method = "tryUseTotem", cancellable = true)
    public void useCustomTotem(DamageSource damageSource, CallbackInfoReturnable<Boolean> callback) {
        LivingEntityMixin entity = this;

        ItemStack offhandStack = entity.getStackInHand(Hand.OFF_HAND);
        ItemStack mainhandStack = entity.getStackInHand(Hand.MAIN_HAND);

        if (offhandStack.isOf(ModItems.TOTEM_OF_PRIDE) || mainhandStack.isOf(ModItems.TOTEM_OF_PRIDE)) {
                if (offhandStack.isOf(ModItems.TOTEM_OF_PRIDE)) {
                    offhandStack.decrement(1);
                } else if (mainhandStack.isOf(ModItems.TOTEM_OF_PRIDE)) {
                    mainhandStack.decrement(1);
                }

                this.setHealth(1.0F);
                this.clearStatusEffects();
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 125, 2));
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 350, 4));
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 100, 2));
                this.getWorld().sendEntityStatus(entity, (byte) 35);

                callback.setReturnValue(true);
        }
    }
}

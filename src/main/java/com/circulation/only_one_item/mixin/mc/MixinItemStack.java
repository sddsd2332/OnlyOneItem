package com.circulation.only_one_item.mixin.mc;

import com.circulation.only_one_item.conversion.ItemConversionTarget;
import com.circulation.only_one_item.handler.MatchItemHandler;
import com.circulation.only_one_item.util.OOIItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class MixinItemStack implements OOIItemStack {

    @Mutable
    @Shadow
    @Final
    private Item item;

    @Shadow(remap = false)
    private net.minecraftforge.registries.IRegistryDelegate<Item> delegate;

    @Shadow
    int itemDamage;

    @Shadow
    public abstract Item getItem();

    @Shadow
    public abstract boolean isEmpty();

    @Unique
    private static boolean ooi$init = false;

    @Inject(method = "forgeInit",at = @At("TAIL"),remap = false)
    private void forgeInit(CallbackInfo ci) {
        if (ooi$init){
            ooi$ooiInit();
        } else if (!this.isEmpty()) {
            MatchItemHandler.addPreItemStack(this);
        }
    }

    @Override
    public void ooi$init(){
        ooi$init = true;
    }

    @Override
    public void ooi$ooiInit(){
        ItemConversionTarget target = MatchItemHandler.match(item,itemDamage);

        if (target != null){
            item = target.getTarget();
            delegate = item.delegate;
            itemDamage = target.getTargetMeta();
        }
    }

}

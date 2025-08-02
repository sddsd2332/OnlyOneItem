package com.circulation.only_one_item.mixin;

import com.circulation.only_one_item.OOIConfig;
import com.circulation.only_one_item.util.ItemConversionTarget;
import com.circulation.only_one_item.util.MatchItemHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {

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

    @Inject(method = "forgeInit",at = @At("TAIL"),remap = false)
    private void forgeInit(CallbackInfo ci) {
        for (ItemConversionTarget conversion : OOIConfig.items) {
            if (MatchItemHandler.match(conversion,this)){
                item = conversion.getTarget();
                delegate = item.delegate;
                itemDamage = conversion.getTargetMeta();
                break;
            }
        }
    }

}

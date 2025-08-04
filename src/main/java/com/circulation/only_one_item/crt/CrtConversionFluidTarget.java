package com.circulation.only_one_item.crt;

import com.circulation.only_one_item.conversion.FluidConversionTarget;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.liquid.ILiquidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ZenRegister
@ZenClass("mods.ooi.ConversionFluid")
public class CrtConversionFluidTarget {

    public static final List<FluidConversionTarget> list = new ArrayList<>();

    private final Set<String> matchFluids = new HashSet<>();
    private final String targetID;

    public CrtConversionFluidTarget(String id){
        this.targetID = id;
    }

    @ZenMethod
    public static CrtConversionFluidTarget create(ILiquidStack target){
        return new CrtConversionFluidTarget(target.getName());
    }

    @ZenMethod
    public CrtConversionFluidTarget addMatchFluid(ILiquidStack... stacks){
        for (ILiquidStack stack : stacks) {
            matchFluids.add(stack.getName());
        }
        return this;
    }

    @ZenMethod
    public void register(){
        list.add(new FluidConversionTarget(targetID).setMatchFluids(matchFluids));
    }
}

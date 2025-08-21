package com.circulation.only_one_item.conversion;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.Set;

public final class FluidConversionTarget {

    private Set<String> matchFluids;

    private final String targetID;

    public FluidConversionTarget(String targetID){
        this.targetID = targetID;
    }

    public FluidConversionTarget setMatchFluids(Set<String> matchFluids) {
        this.matchFluids = matchFluids;
        return this;
    }

    public FluidConversionTarget addMatchFluid(String... stacks){
        if (matchFluids == null){
            matchFluids = new ObjectOpenHashSet<>();
        }
        matchFluids.addAll(Arrays.asList(stacks));
        return this;
    }

    public FluidConversionTarget addMatchFluid(FluidStack... stacks){
        if (matchFluids == null){
            matchFluids = new ObjectOpenHashSet<>();
        }
        for (FluidStack stack : stacks) {
            matchFluids.add(stack.getFluid().getName());
        }
        return this;
    }

    public Fluid getTarget() {
        return FluidRegistry.getFluid(targetID);
    }

    public String getTargetID() {
        return targetID;
    }

    public Set<String> getMatchFluids() {
        return matchFluids;
    }
}

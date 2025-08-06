package com.circulation.only_one_item.util;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IRegistryDelegate;

public interface OOIFluidStack {

    void ooi$init();

    void ooi$ooiInit(Fluid fluid);
    IRegistryDelegate<Fluid> ooi$getFluidDelegate(FluidStack fluid);
}

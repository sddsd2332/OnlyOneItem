package com.circulation.only_one_item.handler;

import com.circulation.only_one_item.OOIConfig;
import com.circulation.only_one_item.conversion.FluidConversionTarget;
import com.circulation.only_one_item.crt.CrtConversionFluidTarget;
import com.circulation.only_one_item.util.OOIFluidStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Optional;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MatchFluidHandler {

    private static ArrayList<WeakReference<OOIFluidStack>> list = new ArrayList<>();

    private static final Map<String, Fluid> fluidNameToTargetMap = new HashMap<>();

    public static void preFluidStackInit() {
        if (list == null)
            throw new RuntimeException("[OOI] Initialization should not be performed multiple times");
        ((OOIFluidStack) new FluidStack(FluidRegistry.WATER,1)).ooi$init();
        list.parallelStream()
                .forEach(ref -> {
                    var fluid = ref.get();
                    if (fluid != null) fluid.ooi$ooiInit();
                });
        list.clear();
        list = null;
    }

    public static synchronized void addPreFluidStack(OOIFluidStack i){
        if (list == null)
            throw new RuntimeException("[OOI] It should not be added again after initialization");
        list.add(new WeakReference<>(i));
    }

    public static Fluid match(Object obj) {
        if (!(obj instanceof FluidStack stack))return null;
        Fluid fluid = stack.getFluid();
        if (stack.getFluid() == null)return null;
        return fluidNameToTargetMap.get(fluid.getName());
    }

    public static void Init() {
        for (FluidConversionTarget t : OOIConfig.fluids) {
            for (String fluid : t.getMatchFluids()) {
                if (t.getTarget() != null){
                    fluidNameToTargetMap.put(fluid,t.getTarget());
                }
            }
        }
        OOIConfig.fluids.clear();
    }

    @Optional.Method(modid = "crafttweaker")
    public static void CrtInit(){
        for (FluidConversionTarget t : CrtConversionFluidTarget.list) {
            for (String fluid : t.getMatchFluids()) {
                if (t.getTarget() != null){
                    fluidNameToTargetMap.put(fluid,t.getTarget());
                }
            }
        }
        CrtConversionFluidTarget.list.clear();
    }
}

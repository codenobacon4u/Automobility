package io.github.foundationgames.automobility.fabric.controller.controlify;

import dev.isxander.controlify.api.ControlifyApi;
import dev.isxander.controlify.api.bind.ControlifyBindApi;
import dev.isxander.controlify.api.bind.InputBindingSupplier;
import dev.isxander.controlify.api.entrypoint.ControlifyEntrypoint;
import dev.isxander.controlify.bindings.BindContext;
import dev.isxander.controlify.controller.input.GamepadInputs;
import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.HashSet;
import java.util.Set;

public class ControlifyCompat implements ControlifyEntrypoint {
    public static final Set<InputBindingSupplier> AUTOMOBILITY_BINDINGS = new HashSet<>();

    private boolean isContextValid(Minecraft mc) {
        var minecraft = Minecraft.getInstance();
        var player = minecraft.player;
        if (player != null && player.getVehicle() instanceof AutomobileEntity auto && auto.getControllingPassenger() == player && minecraft.screen == null) {
            return true;
        }
        return false;
    }

    @Override
    public void onControlifyPreInit(ControlifyApi controlify) {
        BindContext drivingCtx = new BindContext(Automobility.rl("driving"), this::isContextValid);
        Component category = Component.translatable("controlify.binding_category.driving");

        ControlifyController.accelerateBinding = ControlifyBindApi.get().registerBinding(builder -> builder
                .id(Automobility.rl("accelerate_automobile"))
                .defaultInput(GamepadInputs.getBind(GamepadInputs.EAST_BUTTON))
                .allowedContexts(drivingCtx)
                .category(category));
        ControlifyController.brakeBinding = ControlifyBindApi.get().registerBinding(builder -> builder
                .id(Automobility.rl("brake_automobile"))
                .defaultInput(GamepadInputs.getBind(GamepadInputs.SOUTH_BUTTON))
                .allowedContexts(drivingCtx)
                .category(category));
        ControlifyController.driftBinding = ControlifyBindApi.get().registerBinding(builder -> builder
                .id(Automobility.rl("drift_automobile"))
                .defaultInput(GamepadInputs.getBind(GamepadInputs.RIGHT_TRIGGER_AXIS))
                .allowedContexts(drivingCtx)
                .category(category));

        AUTOMOBILITY_BINDINGS.clear();
        AUTOMOBILITY_BINDINGS.add(ControlifyController.accelerateBinding);
        AUTOMOBILITY_BINDINGS.add(ControlifyController.brakeBinding);
        AUTOMOBILITY_BINDINGS.add(ControlifyController.driftBinding);

        // ControlifyEvents.INGAME_GUIDE_REGISTRY.register((event) -> {
        //     ControllerEntity bindings = event.bindings();
        //     IngameGuideRegistry registry = event.registry();
        //     var accelerate = bindings.getComponent(Automobility.rl("accelerate_automobile"));
        //     var brake = bindings.getComponent(Automobility.rl("brake_automobile"));
        //     var drift = bindings.getComponent(Automobility.rl("drift_automobile"));

        //     registry.registerGuideAction(accelerate, ActionLocation.LEFT, ActionPriority.LOW, ctx -> {
        //         if (ctx.player().getVehicle() instanceof AutomobileEntity)
        //             return Optional.of(Component.translatable("controlify.binding.automobility.accelerate_automobile"));
        //         return Optional.empty();
        //     });
        //     registry.registerGuideAction(brake, ActionLocation.LEFT, ActionPriority.LOW, ctx -> {
        //         if (ctx.player().getVehicle() instanceof AutomobileEntity)
        //             return Optional.of(Component.translatable("controlify.binding.automobility.brake_automobile"));
        //         return Optional.empty();
        //     });
        //     registry.registerGuideAction(drift, ActionLocation.LEFT, ActionPriority.LOW, ctx -> {
        //         if (ctx.player().getVehicle() instanceof AutomobileEntity)
        //             return Optional.of(Component.translatable("controlify.binding.automobility.drift_automobile"));
        //         return Optional.empty();
        //     });
        // });
    }

    @Override
    public void onControllersDiscovered(ControlifyApi controlify) {

    }
}

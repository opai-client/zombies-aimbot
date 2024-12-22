package win.cuteguimc.opai.aimbot;

import today.opai.api.dataset.PositionData;
import today.opai.api.enums.EnumModuleCategory;
import today.opai.api.events.EventRender3D;
import today.opai.api.features.ExtensionModule;
import today.opai.api.interfaces.EventHandler;
import today.opai.api.interfaces.game.entity.Entity;
import today.opai.api.interfaces.game.entity.LivingEntity;
import today.opai.api.interfaces.game.entity.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static win.cuteguimc.opai.aimbot.AimbotExtension.openAPI;

public class MobESPModule extends ExtensionModule implements EventHandler {
    public MobESPModule() {
        super("MobESP", "Where is the last zombie?", EnumModuleCategory.VISUAL);
        this.setEventHandler(this);
    }
    
    private List<LivingEntity> getMobs() {
        List<LivingEntity> entities = new ArrayList<>();
        for (Entity entity: openAPI.getWorld().getLoadedEntities()) {
            if (!(entity instanceof LivingEntity) || entity instanceof Player) continue;
            entities.add((LivingEntity) entity);
        }
        return entities;
    }

    @Override
    public void onRender3D(EventRender3D event) {
        getMobs().forEach(entity -> {
            openAPI.getRenderUtil().drawBoundingBox(entity, new Color(56, 199, 231));
        });
    }
}

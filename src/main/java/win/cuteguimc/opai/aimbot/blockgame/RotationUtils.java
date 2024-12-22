package win.cuteguimc.opai.aimbot.blockgame;

import today.opai.api.dataset.PositionData;
import today.opai.api.interfaces.game.entity.LivingEntity;

import static win.cuteguimc.opai.aimbot.AimbotExtension.openAPI;

public class RotationUtils {
    public static float[] getRotations(LivingEntity entity) {
        PositionData positionData = openAPI.getLocalPlayer().getPosition();
        double pX = positionData.getX();
        double pY = positionData.getY() + (double) openAPI.getLocalPlayer().getEyeHeight();
        double pZ = positionData.getZ();
        PositionData entityPosition = entity.getPosition();
        double eX = entityPosition.getX();
        double eY = entityPosition.getY() + (double) (entity.getEyeHeight() / 2.0f);
        double eZ = entityPosition.getZ();
        double dX = pX - eX;
        double dY = pY - eY;
        double dZ = pZ - eZ;
        double dH = Math.sqrt(Math.pow(dX, 2.0) + Math.pow(dZ, 2.0));
        double yaw = Math.toDegrees(Math.atan2(dZ, dX)) + 90.0;
        double pitch = Math.toDegrees(Math.atan2(dH, dY));
        return new float[]{(float) yaw, (float) (90.0 - pitch)};
    }
}

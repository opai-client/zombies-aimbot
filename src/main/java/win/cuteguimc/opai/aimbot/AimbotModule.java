package win.cuteguimc.opai.aimbot;

import today.opai.api.dataset.PositionData;
import today.opai.api.dataset.RotationData;
import today.opai.api.enums.EnumDirection;
import today.opai.api.enums.EnumModuleCategory;
import today.opai.api.features.ExtensionModule;
import today.opai.api.interfaces.EventHandler;
import today.opai.api.interfaces.game.entity.Entity;
import today.opai.api.interfaces.game.entity.LivingEntity;
import today.opai.api.interfaces.modules.values.NumberValue;
import win.cuteguimc.opai.aimbot.blockgame.BlockPos;
import win.cuteguimc.opai.aimbot.blockgame.Blocks;
import win.cuteguimc.opai.aimbot.blockgame.RotationUtils;
import win.cuteguimc.opai.aimbot.blockgame.Vec3;

import java.util.Arrays;
import java.util.List;

import static win.cuteguimc.opai.aimbot.AimbotExtension.openAPI;

public class AimbotModule extends ExtensionModule implements EventHandler {
    private final NumberValue predictSize = openAPI.getValueManager().createDouble("Predict Size", 1.2, 0, 8, 0.1);

    private LivingEntity target;
    
    public AimbotModule() {
        super("ZombiesAimbot", "Use your pussy to play hypixel zombies", EnumModuleCategory.COMBAT);
        this.addValues(predictSize);
        setEventHandler(this);
    }
    
    public LivingEntity getBestTarget() {
        float distance = 50f;
        List<Entity> entities = openAPI.getWorld().getLoadedEntities();
        PositionData positionData = openAPI.getLocalPlayer().getPosition();
        for (Entity entity: entities) {
            if (!(entity instanceof LivingEntity)) continue;
            float[] rot = RotationUtils.getRotations((LivingEntity) entity);
            if (entity.getDistanceToPosition(positionData) <= distance &&
                    ((LivingEntity) entity).getHealth() > 0 &&
                    // how to detect villagers?
                    Math.abs(entity.getMotion().getY()) <= 1 &&
                    canEntityBeSeen((LivingEntity) entity)) {
                distance = (float) entity.getDistanceToPosition(openAPI.getLocalPlayer().getPosition());
                target = (LivingEntity) entity;
            }
        }
        return target;
    }

    @Override
    public void onTick() {
        if (openAPI.getLocalPlayer() != null && openAPI.getWorld() != null) {
            getBestTarget();
            if (target != null) {
                float[] rotations = RotationUtils.getRotations(target);
                openAPI.getRotationManager().applyRotation(new RotationData(rotations[0], rotations[1]), 180, true);
            }
        }
    }

    @Override
    public String getSuffix() {
        return predictSize.getValue().toString();
    }

    public double[] getPredictPos(LivingEntity entity) {
        PositionData playerPosition = openAPI.getLocalPlayer().getPosition();
        PositionData positionData = entity.getPosition();
        PositionData lastPositionData = entity.getLastTickPosition();
        double x = positionData.getX() + calcPredict(positionData.getX(), lastPositionData.getX());
        double y = positionData.getY() + (calcPredict(positionData.getY(), positionData.getY()) / 5.0) + entity.getEyeHeight();
        double z = positionData.getZ() + calcPredict(positionData.getZ(), positionData.getZ()); // @on
        for (float i = 0; i < 1; i = i + 0.01f) {
            y -= entity.getEyeHeight() * 0.01;
            if (!rayTraceBlocks(new Vec3(playerPosition.getX(), playerPosition.getY() + (double) openAPI.getLocalPlayer().getEyeHeight(), playerPosition.getZ()), new Vec3(x, y, z), false, true, false)) {
                return new double[]{x, y, z};
            }
        }
        return new double[]{x, y, z};
    }

    public double calcPredict(double a, double lastTick) {
        if (a - lastTick >= getPredict() * 2.0) return getPredict() * 2.0;
        if (a - lastTick <= getPredict() * -2.0) return getPredict() * -2.0;
        return (a - lastTick) * getPredict();
    }

    public double getPredict() {
        return predictSize.getValue();
    }

    public boolean canEntityBeSeen(LivingEntity entity) {
        double[] d1 = getPredictPos(entity);
        PositionData positionData = openAPI.getLocalPlayer().getPosition();
        return !rayTraceBlocks(new Vec3(positionData.getX(), positionData.getY() + (double) openAPI.getLocalPlayer().getEyeHeight(), positionData.getZ()), new Vec3(d1[0], d1[1], d1[2]), false, true, false);
    }

    public boolean rayTraceBlocks(Vec3 vec31, Vec3 vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock)
    {
        if (!Double.isNaN(vec31.xCoord) && !Double.isNaN(vec31.yCoord) && !Double.isNaN(vec31.zCoord))
        {
            if (!Double.isNaN(vec32.xCoord) && !Double.isNaN(vec32.yCoord) && !Double.isNaN(vec32.zCoord))
            {
                int i = (int) Math.floor(vec32.xCoord);
                int j = (int) Math.floor(vec32.yCoord);
                int k = (int) Math.floor(vec32.zCoord);
                int l = (int) Math.floor(vec31.xCoord);
                int i1 = (int) Math.floor(vec31.yCoord);
                int j1 = (int) Math.floor(vec31.zCoord);
                BlockPos blockpos = new BlockPos(l, i1, j1);
                int blockId = openAPI.getWorld().getBlock(blockpos.toBlockPosition());
                if (Arrays.stream(Blocks.passibleBlocks).noneMatch(id -> id == blockId)) {
                    return true;
                }
                
                int k1 = 200;

                while (k1-- >= 0)
                {
                    if (Double.isNaN(vec31.xCoord) || Double.isNaN(vec31.yCoord) || Double.isNaN(vec31.zCoord))
                    {
                        return false;
                    }

                    if (l == i && i1 == j && j1 == k)
                    {
                        return returnLastUncollidableBlock ? true : false;
                    }

                    boolean flag2 = true;
                    boolean flag = true;
                    boolean flag1 = true;
                    double d0 = 999.0D;
                    double d1 = 999.0D;
                    double d2 = 999.0D;

                    if (i > l)
                    {
                        d0 = (double)l + 1.0D;
                    }
                    else if (i < l)
                    {
                        d0 = (double)l + 0.0D;
                    }
                    else
                    {
                        flag2 = false;
                    }

                    if (j > i1)
                    {
                        d1 = (double)i1 + 1.0D;
                    }
                    else if (j < i1)
                    {
                        d1 = (double)i1 + 0.0D;
                    }
                    else
                    {
                        flag = false;
                    }

                    if (k > j1)
                    {
                        d2 = (double)j1 + 1.0D;
                    }
                    else if (k < j1)
                    {
                        d2 = (double)j1 + 0.0D;
                    }
                    else
                    {
                        flag1 = false;
                    }

                    double d3 = 999.0D;
                    double d4 = 999.0D;
                    double d5 = 999.0D;
                    double d6 = vec32.xCoord - vec31.xCoord;
                    double d7 = vec32.yCoord - vec31.yCoord;
                    double d8 = vec32.zCoord - vec31.zCoord;

                    if (flag2)
                    {
                        d3 = (d0 - vec31.xCoord) / d6;
                    }

                    if (flag)
                    {
                        d4 = (d1 - vec31.yCoord) / d7;
                    }

                    if (flag1)
                    {
                        d5 = (d2 - vec31.zCoord) / d8;
                    }

                    if (d3 == -0.0D)
                    {
                        d3 = -1.0E-4D;
                    }

                    if (d4 == -0.0D)
                    {
                        d4 = -1.0E-4D;
                    }

                    if (d5 == -0.0D)
                    {
                        d5 = -1.0E-4D;
                    }

                    EnumDirection enumDirection;

                    if (d3 < d4 && d3 < d5)
                    {
                        enumDirection = i > l ? EnumDirection.WEST : EnumDirection.EAST;
                        vec31 = new Vec3(d0, vec31.yCoord + d7 * d3, vec31.zCoord + d8 * d3);
                    }
                    else if (d4 < d5)
                    {
                        enumDirection = j > i1 ? EnumDirection.DOWN : EnumDirection.UP;
                        vec31 = new Vec3(vec31.xCoord + d6 * d4, d1, vec31.zCoord + d8 * d4);
                    }
                    else
                    {
                        enumDirection = k > j1 ? EnumDirection.NORTH : EnumDirection.SOUTH;
                        vec31 = new Vec3(vec31.xCoord + d6 * d5, vec31.yCoord + d7 * d5, d2);
                    }

                    l = (int) (Math.floor(vec31.xCoord) - (enumDirection == EnumDirection.EAST ? 1 : 0));
                    i1 = (int) (Math.floor(vec31.yCoord) - (enumDirection == EnumDirection.UP ? 1 : 0));
                    j1 = (int) (Math.floor(vec31.zCoord) - (enumDirection == EnumDirection.SOUTH ? 1 : 0));
                    blockpos = new BlockPos(l, i1, j1);
                    int blockId1 = openAPI.getWorld().getBlock(blockpos.toBlockPosition());
                    if (Arrays.stream(Blocks.passibleBlocks).noneMatch(id -> id == blockId1)) {
                        return true;
                    }
                }

                return false;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }
}

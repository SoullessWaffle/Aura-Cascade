package pixlepix.auracascade.block.entity;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.world.World;
import pixlepix.auracascade.AuraCascade;
import pixlepix.auracascade.network.PacketBurst;

import java.util.Random;

/**
 * Created by pixlepix on 12/16/14.
 */
public class EntityBaitFairy extends EntityFairy {
    public EntityBaitFairy(World p_i1582_1_) {
        super(p_i1582_1_);
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
        if (!worldObj.isRemote) {
            if (new Random().nextInt(3600) == 0) {
                Random random = new Random();
                Entity entity;
                switch (random.nextInt(4)) {
                    case 0:
                        entity = new EntityCow(worldObj);
                        break;

                    case 1:
                        entity = new EntityChicken(worldObj);
                        break;

                    case 2:
                        entity = new EntityPig(worldObj);
                        break;

                    default:
                        entity = new EntitySheep(worldObj);
                        break;
                }

                entity.setPosition(posX, posY, posZ);
                worldObj.spawnEntityInWorld(entity);

                AuraCascade.proxy.networkWrapper.sendToAllAround(new PacketBurst(5, entity.posX, entity.posY, entity.posZ), new NetworkRegistry.TargetPoint(entity.worldObj.provider.getDimensionId(), entity.posX, entity.posY, entity.posZ, 32));

            }
        }
    }
}



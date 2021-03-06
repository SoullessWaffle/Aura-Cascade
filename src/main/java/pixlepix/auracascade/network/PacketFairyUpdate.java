package pixlepix.auracascade.network;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import pixlepix.auracascade.AuraCascade;
import pixlepix.auracascade.block.entity.EntityFairy;

/**
 * Created by pixlepix on 12/8/14.
 */
public class PacketFairyUpdate implements IMessage, IMessageHandler<PacketFairyUpdate, IMessage> {

    public EntityFairy fairy;
    double theta;
    double rho;
    double phi;
    double dPhi;
    double dTheta;
    EntityPlayer player;

    public PacketFairyUpdate(EntityFairy fairy) {
        this.fairy = fairy;
    }

    public PacketFairyUpdate() {

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        Entity entity = AuraCascade.proxy.getWorld().getEntityByID(buf.readInt());
        if (entity instanceof EntityFairy) {
            this.fairy = (EntityFairy) entity;
            player = (EntityPlayer) AuraCascade.proxy.getWorld().getEntityByID(buf.readInt());
            theta = buf.readDouble();
            rho = buf.readDouble();
            phi = buf.readDouble();
            dPhi = buf.readDouble();
            dTheta = buf.readDouble();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(fairy.getEntityId());
        buf.writeInt(fairy.player.getEntityId());
        buf.writeDouble(fairy.theta);
        buf.writeDouble(fairy.rho);
        buf.writeDouble(fairy.phi);
        buf.writeDouble(fairy.dPhi);
        buf.writeDouble(fairy.dTheta);
    }

    @Override
    public IMessage onMessage(final PacketFairyUpdate msg, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                if (msg.fairy != null) {
                    msg.fairy.theta = msg.theta;
                    msg.fairy.rho = msg.rho;
                    msg.fairy.dPhi = msg.dPhi;
                    msg.fairy.dTheta = msg.dTheta;
                    msg.fairy.phi = msg.phi;
                    msg.fairy.player = msg.player;
                }
            }
        });
        return null;
    }
}

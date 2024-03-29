package es.allblue.lizardon.net.client;


import com.google.common.base.Charsets;
import es.allblue.lizardon.Lizardon;
import es.allblue.lizardon.event.wungill.CarreraEventsClient;
import es.allblue.lizardon.pixelmon.battle.LizardonBattleController;
import es.allblue.lizardon.pixelmon.frentebatalla.TorreBatallaController;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CMessageRunJS implements Runnable{
    private String str;
    private PlayerEntity player;

    public CMessageRunJS(String str){
        this.str = str;
    }

    @Override
    public void run() {
        Lizardon.PROXY.runJS(str);
    }

    public static CMessageRunJS decode(PacketBuffer buf) {
        CMessageRunJS message = new CMessageRunJS(buf.toString(Charsets.UTF_8));
        return message;
    }

    public void encode(PacketBuffer buf) {
        buf.writeCharSequence(str, Charsets.UTF_8);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        player = contextSupplier.get().getSender();
        contextSupplier.get().enqueueWork((Runnable) this);
        contextSupplier.get().setPacketHandled(true);
    }

}

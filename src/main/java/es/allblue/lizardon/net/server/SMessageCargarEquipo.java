package es.allblue.lizardon.net.server;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import es.allblue.lizardon.Lizardon;
import es.allblue.lizardon.net.Messages;
import es.allblue.lizardon.net.client.CMessageReturn;
import es.allblue.lizardon.objects.pixelmon.frentebatalla.GetEquipo;
import es.allblue.lizardon.pixelmon.battle.TeamManager;
import es.allblue.lizardon.util.MessageHelper;
import es.allblue.lizardon.util.PersistentDataFields;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.montoyo.mcef.api.IJSQueryCallback;

import java.util.List;
import java.util.function.Supplier;

public class SMessageCargarEquipo implements Runnable{
    private String str;
    private ServerPlayerEntity player;
    private IJSQueryCallback callback;

    public SMessageCargarEquipo(String str){
        this.str = str;
    }

    @Override
    public void run() {
        Gson gson = new Gson();
        GetEquipo getEquipo = gson.fromJson(str, GetEquipo.class);

        Lizardon.getLBC().guardarEquipo(player, "equipo");

        List<GetEquipo.PkmSlot> slots = getEquipo.getEquipo();

        TeamManager.loadFromSlots(player, slots);

        MessageHelper.enviarMensaje(player, "Equipo cargado correctamente");
        player.getPersistentData().putBoolean(PersistentDataFields.FB_ACTIVO.label, true);
        player.getPersistentData().putString(PersistentDataFields.EQUIPO_ACTIVO.label, getEquipo.getTipo());

        Messages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new CMessageReturn("Equipo cargado correctamente"));
    }

    public static SMessageCargarEquipo decode(PacketBuffer buf) {
        SMessageCargarEquipo message = new SMessageCargarEquipo(buf.toString(Charsets.UTF_8));
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

package es.allblue.lizardon.net.server;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import es.allblue.lizardon.Lizardon;
import es.allblue.lizardon.net.Messages;
import es.allblue.lizardon.net.client.CMessageVerMisiones;
import es.allblue.lizardon.objects.Mision;
import es.allblue.lizardon.objects.RecompensaMision;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.montoyo.mcef.api.IJSQueryCallback;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.data.IPixelmonPlayerData;
import noppes.npcs.api.handler.IQuestHandler;
import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.api.handler.data.IQuestCategory;
import noppes.npcs.api.handler.data.IQuestObjective;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.PlayerWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SMessageVerMisiones implements Runnable{
    private String str;
    private ServerPlayerEntity player;
    private IJSQueryCallback callback;

    public SMessageVerMisiones(String str){
        this.str = str;
    }

    @Override
    public void run() {
        NpcAPI api = NpcAPI.Instance();
        PlayerWrapper wrapper = new PlayerWrapper(player);
        IQuestHandler questHandler = api.getQuests();

        Map<Integer, Mision> misiones = new HashMap<>();
        for ( IQuestCategory category : questHandler.categories()) {
            for ( IQuest quest : category.quests()) {

                System.out.println("======== " + quest.getName() +" ========");
                Mision mision = new Mision();
                IQuest siguienteMision = quest.getNextQuest();
                if(siguienteMision == null) {
                    mision.setSiguienteMision(-1);
                } else {
                    mision.setSiguienteMision(quest.getNextQuest().getId());
                }



                mision.setCategoria(category.getName());
                mision.setNombre(quest.getName());
                mision.setTipo(quest.getType());
                mision.setTextoCompletar(quest.getCompleteText());
                mision.setTextoLog(quest.getLogText());
                mision.setNombreNPC(quest.getNpcName());
                mision.setRepetible(quest.getIsRepeatable());


                for(int i = 0; i < quest.getRewards().getItems().length; i++){
                    IItemStack recompensa = quest.getRewards().getItems()[i];
                    ArrayList<RecompensaMision> recompensasMision = new ArrayList<RecompensaMision>();
                    if(recompensa.getStackSize() != 0){
                        RecompensaMision recompensaMision = new RecompensaMision();
                        recompensaMision.setObjeto(recompensa.getName());
                        recompensaMision.setCantidad(recompensa.getStackSize());

                        recompensasMision.add(recompensaMision);
                    }
                    mision.setRecompensas(recompensasMision);
                }


                
                misiones.put(quest.getId(), mision);
            }
        }

        Gson gson = new Gson();
        String res = gson.toJson(misiones);
        System.out.println(res);
        Messages.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new CMessageVerMisiones(res));
    }

    public static SMessageVerMisiones decode(PacketBuffer buf) {
        SMessageVerMisiones message = new SMessageVerMisiones(buf.toString(Charsets.UTF_8));
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

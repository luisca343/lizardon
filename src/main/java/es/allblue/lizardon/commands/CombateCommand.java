package es.allblue.lizardon.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import es.allblue.lizardon.Lizardon;
import es.allblue.lizardon.pixelmon.battle.Combate;
import es.allblue.lizardon.objects.pixelmon.ConfigCombate;
import es.allblue.lizardon.util.Reader;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;

public class CombateCommand {
    public CombateCommand(CommandDispatcher<CommandSource> dispatcher){
        dispatcher.register(Commands.literal("combate")
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("url", StringArgumentType.string())
                                .executes((command) -> {
                                    return iniciarCombate(command);

                                })
                        )));

        dispatcher.register(Commands.literal("encuentro")
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("url", StringArgumentType.string())
                                .executes((command) -> {
                                    return iniciarEncuentro(command);
                                })
                        )));


    }

    private int iniciarEncuentro(CommandContext<CommandSource> command) {
        try{
            ServerPlayerEntity player = EntityArgument.getPlayer(command, "player");
            String url = StringArgumentType.getString(command, "url");
            ConfigCombate configSalvaje = Reader.getDatosEncuentro(url);
            Combate combate = new Combate(player, configSalvaje);
            combate.iniciarCombate();
        } catch (CommandSyntaxException e){
            Lizardon.LOGGER.error("Error al iniciar el combate");
        }
        return 1;
    }

    public int iniciarCombate(CommandContext<CommandSource> command) {
        try{
            ServerPlayerEntity player = EntityArgument.getPlayer(command, "player");
            String url = StringArgumentType.getString(command, "url");
            ConfigCombate configCombateEntrenador = Reader.getDatosNPC(url);
            Combate combate = new Combate(player, configCombateEntrenador);
            combate.iniciarCombate();
        }catch(CommandSyntaxException e){
            Lizardon.LOGGER.error("Error al iniciar el combate");
        }

        return 1;
    }



}

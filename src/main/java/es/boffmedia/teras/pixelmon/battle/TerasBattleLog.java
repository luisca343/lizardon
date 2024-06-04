package es.boffmedia.teras.pixelmon.battle;

import com.pixelmonmod.pixelmon.api.pokemon.stats.BattleStatsType;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.battles.controller.BattleController;
import com.pixelmonmod.pixelmon.battles.controller.log.MoveResults;
import com.pixelmonmod.pixelmon.battles.controller.log.action.BattleAction;
import com.pixelmonmod.pixelmon.battles.controller.log.action.type.*;
import com.pixelmonmod.pixelmon.battles.controller.log.action.type.AttackAction;
import com.pixelmonmod.pixelmon.battles.controller.log.action.type.BattleEndAction;
import com.pixelmonmod.pixelmon.battles.controller.log.action.type.StatChangeAction;
import com.pixelmonmod.pixelmon.battles.controller.log.action.type.StatusAddAction;
import com.pixelmonmod.pixelmon.battles.controller.log.action.type.SwitchAction;
import com.pixelmonmod.pixelmon.battles.controller.log.action.type.TurnBeginAction;
import com.pixelmonmod.pixelmon.battles.controller.log.action.type.WeatherChangeAction;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.battles.status.GlobalStatusBase;
import com.pixelmonmod.pixelmon.battles.status.StatusBase;
import com.pixelmonmod.pixelmon.battles.status.StatusType;
import com.pixelmonmod.pixelmon.battles.status.Weather;
import es.boffmedia.teras.Teras;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.*;

public class TerasBattleLog {

    //public static HashMap<Integer, HashMap<String, List<PixelmonWrapper>>> activePokemon = new HashMap<>();

    public static void initLog(TerasBattle battle){
        battle.setLog("");
    }
    final char[] teamPos = {'a', 'b', 'c', 'd', 'e', 'f'};

    public static String getBattleLog(TerasBattle battle){
        if(battle.getLog().isEmpty()) {
            initLog(battle);
        }
        return battle.getLog();
    }

    public static void appendLine(TerasBattle battle, String log){
        //Teras.getLogger().warn("LOGGING: " + log);
        battle.setLog(battle.getLog() + log + "\n");
    }

    public static void printLog(TerasBattle battle){
        Teras.LOGGER.info(getBattleLog(battle));
    }

    public static void logEvent(BattleAction action, TerasBattle battle) {
        if (action instanceof TurnBeginAction) turnBeginAction((TurnBeginAction) action, battle);
        else if (action instanceof BattleEndAction) { battleEndAction((BattleEndAction) action, battle); }
        else if (action instanceof SwitchAction) switchAction((SwitchAction) action, battle);
        else if (action instanceof AttackAction) attackAction((AttackAction) action, battle);
        //else if (action instanceof StatChangeAction) statChangeAction((StatChangeAction) action, battle);
        else if (action instanceof WeatherChangeAction) weatherChangeAction((WeatherChangeAction) action, battle);
        else if (action instanceof StatusAddAction) statusAddAction((StatusAddAction) action, battle);

        /*




        else if (action instanceof BattleMessageAction) battleMessageAction((BattleMessageAction) action, battle);
        else if (action instanceof TurnEndAction) turnEndAction((TurnEndAction) action, battle);
        else Teras.LOGGER.info("Unknown action: " + action.getClass().getSimpleName());
        */
        else Teras.LOGGER.warn("=== Log event: " + action.toString() + " ===");



        /* ====== TO DO ======
         * Añadir |faint|p1a: Mega Salamanca
         * Arreglar el switch cuando entra un pokemon después de debilitar a otro (es un switch normal)
         * ====== TO DO ======
         */
    }

    /* Actions */
    private static void battleEndAction(BattleEndAction action, TerasBattle terasBattle){

       /* BattleController bc = terasBattle.battle;
        Teras.LOGGER.error("===Battle End===");

        String winner = bc.participants.stream()
                .filter((participant) -> participant.controlledPokemon.size() > 0).findFirst().get().getDisplayName();

       */
    }



    public static void turnBeginAction(TurnBeginAction action, TerasBattle terasBattle){
        BattleController bc = terasBattle.battle;
        int turn = (int) getProtectedProperty("turn", action);
        if(turn == 0) appendStartBattle(terasBattle);


        appendLine(terasBattle,"|turn|" + (turn+1));
        //appendLine(bc.battleIndex,"|callback|decision");
        appendLine(terasBattle,"|");
        appendLine(terasBattle, "|t:|" + System.currentTimeMillis() / 1000);

        List<BattleParticipant> participantsList = new ArrayList<>(bc.participants);
        Collections.reverse(participantsList);

        int participantIndex = 1;
        for (BattleParticipant participant : participantsList) {
            for (int i = 0; i < participant.controlledPokemon.size(); i++) {
                PixelmonWrapper pokemon = participant.controlledPokemon.get(i);
                if(!terasBattle.getActivePokemon(participantIndex, i).equals(pokemon)){
                    terasBattle.swapPokemon(participantIndex, i, pokemon);
                    appendLine( terasBattle,"|switch|" + terasBattle.getPositionString(pokemon) + ": "
                            +pokemon.getNickname()  + "|" + pokemon.getSpecies().getName() + ", L"
                            + pokemon.getPokemonLevel().getPokemonLevel() + "|" + pokemon.getHealth() + "\\/" + pokemon.getMaxHealth());
                }
            }
            participantIndex++;
        }

    }

    public static void switchAction(SwitchAction action, TerasBattle terasBattle){
        BattleController bc = terasBattle.battle;
        Teras.LOGGER.info("------------------ Switch action ------------------");
        PixelmonWrapper pokemon = (PixelmonWrapper) getProtectedProperty("pokemon", action);
        PixelmonWrapper switchingTo = (PixelmonWrapper) getProtectedProperty("switchingTo", action);

        if(!terasBattle.swapPokemon(pokemon, switchingTo)) return;
        appendLine(terasBattle, "|switch|" + terasBattle.getPositionString(switchingTo) + ": "
                +switchingTo.getNickname()  + "|" + switchingTo.getSpecies().getName() + ", L"
                + switchingTo.getPokemonLevel().getPokemonLevel()
                + "|" + switchingTo.getHealth() + "\\/" + switchingTo.getMaxHealth());

    }



    public static void attackAction(AttackAction action, TerasBattle terasBattle){
        PixelmonWrapper pokemon = (PixelmonWrapper) getProtectedProperty("pokemon", action);
        Attack attack = (Attack) getProtectedProperty("attack", action);
        boolean wildPokemon = (boolean) getProtectedProperty("wildPokemon", action);
        String pokemonName = (String) getProtectedProperty("pokemonName", action);
        String[] targets = (String[]) getProtectedProperty("targets", action);
        MoveResults[] moveResults = (MoveResults[]) getProtectedProperty("moveResults", action);

        // The attack action is a bit more complex, as it can target multiple pokemon
        // move|p2a: Calyrex|Astral Barrage|p1a: Kyogren't|[spread] p1a,p1b

        String move = attack.getActualMove().getAttackName();
        String source = terasBattle.getPositionString(pokemon);

        String attackStr =  "|move|" + getPositionAndNameString(pokemon, terasBattle) + "|" + move + "|";
        String mainTargetStr = getPositionAndNameString(moveResults[0].target, terasBattle);

        attackStr += mainTargetStr + "|";

        if(moveResults.length > 1){
            attackStr += "[spread] ";
            attackStr += Arrays.stream(moveResults).map((target) -> terasBattle.getPositionString(target.target)).reduce((a, b) -> a + "," + b).get();
        }

        appendLine(terasBattle, attackStr);

        for (MoveResults moveResult : moveResults) {
            PixelmonWrapper target = moveResult.target;
            String targetName = target.getNickname();

            int currentHealth = moveResult.getTarget().getHealth();
            int maxHealth = moveResult.getTarget().getMaxHealth();

            if(currentHealth == 0){
                appendLine(terasBattle, "|-damage|" + terasBattle.getPositionString(target) + ": " + targetName + "|0 fnt");
            }else{
                appendLine(terasBattle, "|-damage|" + terasBattle.getPositionString(target) + ": " + targetName + "|" + currentHealth + "\\/" + maxHealth);
            }


        }

        // after faint: |faint|p2b: Wugtrio, we loop again
        for (MoveResults moveResult : moveResults) {
            PixelmonWrapper target = moveResult.target;
            if(moveResult.getTarget().getHealth() == 0){
                appendLine(terasBattle, "|faint|" + terasBattle.getPositionString(target) + ": " + target.getNickname());
            }
        }
    }

    public static void statChangeAction(StatChangeAction action, TerasBattle terasBattle){
        int[] oldStats = (int[]) getProtectedProperty("oldStats", action);
        int[] newStats = (int[]) getProtectedProperty("newStats", action);
        String pokemonName = (String) getProtectedProperty("pokemonName", action);

        PixelmonWrapper pokemon = (PixelmonWrapper) getProtectedProperty("pokemon", action);

        //Teras.getLogger().error("+++Pokemon: " + pokemon.getSpecies().getName() +" " + pokemon.getPokemonName() +" " + pokemon.getPokemonUUID());

        int[] delta = new int[oldStats.length];
        for (int i = 0; i < oldStats.length; i++) {
            delta[i] = newStats[i] - oldStats[i];
        }

        for (int i = 0; i < delta.length; i++) {
            if (delta[i] == 0 || oldStats[i] == 0) continue;

            BattleStatsType stat = BattleStatsType.fromIndex(i);
            Teras.LOGGER.info(pokemonName + " " + stat.name() + " " + delta[i]);

            String boostType = delta[i] > 0 ? "|-boost|" : "|-unboost|";
            String statName = stat.name().toLowerCase();
            String boost = boostType + terasBattle.getPositionString(pokemon) + "|" + statName + "|" + Math.abs(delta[i]);
            appendLine(terasBattle, boost);
        }
    }


    public static void weatherChangeAction(WeatherChangeAction action, TerasBattle terasBattle){
        BattleController bc = terasBattle.battle;
        //|-weather|SunnyDay|[from] ability: Drought|[of] p1a: Kyogren't
        GlobalStatusBase newGlobalStatus = (GlobalStatusBase) getProtectedProperty("newWeather", action);
        GlobalStatusBase oldGlobalStatus = (GlobalStatusBase) getProtectedProperty("oldWeather", action);
        PixelmonWrapper pokemon = (PixelmonWrapper) getProtectedProperty("pokemon", action);

        if(newGlobalStatus instanceof Weather){
            Weather newWeather = (Weather) newGlobalStatus;
            String weatherName;
            switch(newWeather.type) {
                case Sunny:
                    weatherName = "SunnyDay";
                    break;
                case Rainy:
                    weatherName = "RainDance";
                    break;
                case Sandstorm:
                    weatherName = "Sandstorm";
                    break;
                case Hail:
                    weatherName = "Hail";
                    break;
                default:
                    weatherName = "UnknownWeather";
                    break;
            }
            appendLine(terasBattle, "|-weather|" + weatherName + "|");
        }
    }


    public static void statusAddAction(StatusAddAction action, TerasBattle terasBattle){
        StatusBase status = (StatusBase) getProtectedProperty("status", action);
        PixelmonWrapper pokemon = (PixelmonWrapper) getProtectedProperty("pokemon", action);

        if(status.type == StatusType.ParadoxBoost){
            appendLine(terasBattle,"|-activate|" + getPositionAndNameString(pokemon, terasBattle) + "|ability: " + pokemon.getAbility().getName());
        }
    }

    /*
    public static PokemonPosition getPosition(PixelmonWrapper pokemon, TerasBattle terasBattle){
        BattleController bc = terasBattle.battle;
        int participantIndex = 0;
        for (BattleParticipant participant : bc.participants) {
            int index = participant.getTeamPokemon().indexOf(pokemon);
            if(index != -1){
                return new PokemonPosition(participantIndex, index);
            }
            participantIndex++;
        }

        return null;
    }

    public static void turnEndAction(TurnEndAction action,  TerasBattle terasBattle){
        BattleController bc = terasBattle.battle;

    }

    public static void battleMessageAction(BattleMessageAction action, TerasBattle terasBattle){
        String message = (String) getProtectedProperty("message", action);
        Teras.getLogger().error(message);
    }

*/
    // Helpers
    public static String getPositionAndNameString(PixelmonWrapper pokemon, TerasBattle terasBattle){
        return terasBattle.getPositionString(pokemon) + ": " + pokemon.getNickname();
    }

    public static String getPositionString(PixelmonWrapper pokemon, BattleController bc){
        // Returns the position of the pokemon in the team
        // The position consists of the letter p, the participant number and the position in the team (a, b, c, d, e, f...)



        for (BattleParticipant participant : bc.participants) {
            int index = participant.getTeamPokemon().indexOf(pokemon);
            if(index != -1){
                return "p" + (bc.participants.indexOf(participant) + 1) + getPositionLetter(index);
            }
        }
        return null;
    }

    public static char getPositionLetter(int index){
        final char[] letters = {'a', 'b', 'c', 'd', 'e', 'f'};

        return letters[index];
    }

    public static void appendStartBattle(TerasBattle terasBattle){
        BattleController bc = terasBattle.battle;
        initLog(terasBattle);
        // Mover esto DENTRO de la clase TerasBattle

        appendLine(terasBattle,"|join|"+ terasBattle.getName1());
        appendLine(terasBattle,"|join|"+terasBattle.getName2());
        appendLine(terasBattle,"|player|p1|"+terasBattle.getName1());
        appendLine(terasBattle,"|player|p2|"+terasBattle.getName2());
        appendLine(terasBattle,"|gametype|doubles");
        appendLine(terasBattle,"|gen|9");
        appendLine(terasBattle,"|tier|Circuito de Gimnasios de Teras");

        final int[] index = {1};

        List<BattleParticipant> participantsList = new ArrayList<>(bc.participants);
        Collections.reverse(participantsList);

        participantsList.forEach((participant) -> {

            for(int i = 0; i < participant.allPokemon.length; i++){
                PixelmonWrapper pokemon = participant.allPokemon[i];

                if(pokemon != null) {
                    terasBattle.swapPokemon(index[0], i, pokemon);
                    terasBattle.getPokemonInit().add(bc.battleIndex, "|poke|p" + index[0] + "|" + pokemon.getSpecies().getName()  + ", L"+pokemon.getPokemonLevel().getPokemonLevel()+"|");
                }
            }


            final int[] positionIndex = {0};
            int max = participant.numControlledPokemon;

            HashMap<Integer, PixelmonWrapper> team = terasBattle.getActiveTeam(index[0]);

            team.forEach((key, pw) -> {
                if(positionIndex[0] >= terasBattle.getBattleConfig().getModalidad()[index[0] - 1]) return;
                if(pw != null) {
                    terasBattle.getSwitchInit().add(bc.battleIndex, "|switch|" + terasBattle.getPositionString(pw) + ": "
                            +pw.getNickname()  + "|" + pw.getSpecies().getName() + ", L"
                            + pw.getPokemonLevel().getPokemonLevel() + "|" + pw.getHealth() + "\\/" + pw.getMaxHealth());
                };
                positionIndex[0]++;
            });

            /*
            for(int i = 0; i < max; i++){
                PixelmonWrapper pw = (PixelmonWrapper) participant.allPokemon[i];
                //appendLine(terasBattle, "|switch|" + getPositionString(pokemon, bc) + ": " +switchingTo.getNickname()  + "|" + switchingTo.getSpecies().getName() + ", L" + switchingTo.getPokemonLevel() + "|" + switchingTo.getHealth() + "\\/" + switchingTo.getMaxHealth());
                if(pw != null) {
                    switchInit.add(bc.battleIndex, "|switch|" + getPositionString(pw, bc) + ": "
                            +pw.getNickname()  + "|" + pw.getSpecies().getName() + ", L"
                            + pw.getPokemonLevel().getPokemonLevel() + "|" + pw.getHealth() + "\\/" + pw.getMaxHealth());
                };
                positionIndex[0]++;

                //finalActive.put(participant.getDisplayName(), Arrays.asList(participant.allPokemon));
            }*/

            index[0]++;
        });




        terasBattle.getPokemonInit().forEach((line) -> appendLine(terasBattle, line));
        appendLine(terasBattle, "|start");
        terasBattle.getSwitchInit().forEach((line) -> appendLine(terasBattle, line));



    }


    // Reflection
    public static Object getProtectedProperty(String property, Object obj){
        Field f = FieldUtils.getField(obj.getClass(), property, true);
        f.setAccessible(true);

        try {
            return f.get(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

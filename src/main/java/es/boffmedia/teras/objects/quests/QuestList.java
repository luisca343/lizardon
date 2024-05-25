package es.boffmedia.teras.objects.quests;

import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.handler.IQuestHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuestList {
    ArrayList<QuestData> questDataList;
    Map<Integer, String> categories;

    public QuestList() {
        NpcAPI npcApi = NpcAPI.Instance();
        IQuestHandler quests = npcApi.getQuests();

        questDataList = new ArrayList<>();
        categories = new HashMap<>();


        quests.categories().forEach(category -> {
            category.quests().forEach(quest -> {
                QuestData questData = new QuestData(quest);
                questDataList.add(questData);
                categories.put(questData.getId(), questData.getCategory());
            });
        });

    }
}

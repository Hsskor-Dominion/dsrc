package script.stardust.systems;

import script.library.*;
import script.library.utils;
import script.library.xp;
import script.*;

import java.util.Enumeration;

public class entertainer_quest2 extends script.base_script
{
    public entertainer_quest2()
    {
    }
    public boolean isQuestReady(obj_id player, obj_id npc) throws InterruptedException
    {
        return (getLevel(player) >= 1);
    }
    public void action_grantEntQuest2(obj_id player, obj_id npc) throws InterruptedException
    {
        int questId = questGetQuestId("quest/event_cantina_lando");
        groundquests.grantQuest(questId, player, npc, true);
    }
    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info mi) throws InterruptedException
    {
        if (hasObjVar(self, "intUsed"))
        {
            return SCRIPT_CONTINUE;
        }
        menu_info_data mid = mi.getMenuItemByType(menu_info_types.EXAMINE);
        if (mid != null)
        {
            mid.setServerNotify(true);
        }
        mid = mi.getMenuItemByType(menu_info_types.ITEM_USE);
        if (mid != null)
        {
            mid.setServerNotify(true);
        }
        return SCRIPT_CONTINUE;
    }
    public int OnObjectMenuSelect(obj_id self, obj_id player, int item) throws InterruptedException
    {
        if (item == menu_info_types.ITEM_USE)
        {
                sendSystemMessage(player, new string_id("stardust/quest", "ent_quest2"));
                action_grantEntQuest2(player, self);
                return SCRIPT_CONTINUE;
        }
        return SCRIPT_CONTINUE;
    }
}

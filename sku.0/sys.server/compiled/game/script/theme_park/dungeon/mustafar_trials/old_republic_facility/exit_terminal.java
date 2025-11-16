package script.theme_park.dungeon.mustafar_trials.old_republic_facility;

import script.library.instance;
import script.*;

public class exit_terminal extends script.base_script
{
    public exit_terminal()
    {
    }
    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info mi) throws InterruptedException
    {
        menu_info_data mid = mi.getMenuItemByType(menu_info_types.ITEM_USE);
        mid.setLabel(new string_id("mustafar/old_republic_facility", "exit_terminal_use"));
        mid.setServerNotify(true);
        return SCRIPT_CONTINUE;
    }
    public int OnObjectMenuSelect(obj_id self, obj_id player, int item) throws InterruptedException
    {
        if (item == menu_info_types.ITEM_USE)
        {
            if (hasObjVar(player, "stardust.return_to_naboo"))
            {
                // Warp player to Naboo and clear the flag
                removeObjVar(player, "stardust.return_to_naboo");
                warpPlayer(player, "naboo", -4901f, 6f, 4216f, null, 0, 0, 0, "", true);
                sendSystemMessageTestingOnly(player, "You return to surface level, Theed, Naboo.");
            }
            else
            {
                // Default instance exit
                instance.requestExitPlayer("old_republic_facility", player);
            }
        }
        return SCRIPT_CONTINUE;
    }
}

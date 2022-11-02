package script.systems.loot;

import script.library.collection;
import script.library.loot;
import script.library.static_item;
import script.library.utils;
import script.menu_info;
import script.menu_info_types;
import script.obj_id;
import script.string_id;

import java.util.ArrayList;
import java.util.List;

public class contraband_spice_chest extends script.base_script {
    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info mi) throws InterruptedException {
        if (utils.getContainingPlayer(self) == player) {
            int mnu2 = mi.addRootMenu(menu_info_types.ITEM_USE, new string_id("npe", "steal_spice"));
        }
        return SCRIPT_CONTINUE;
    }

    public int OnObjectMenuSelect(obj_id self, obj_id player, int item) throws InterruptedException {
        sendDirtyObjectMenuNotification(self);
        if (item == menu_info_types.ITEM_USE)
        {
                            stealSpice(self, player);
        }
        return SCRIPT_CONTINUE; 
    }
    public void stealSpice(obj_id self, obj_id player) throws InterruptedException
    {
        int which_one = rand(1, 3);
        String poster = "";
        switch (which_one)
        {
            case 1:
            poster = "item_roadmap_spice_shadowpaw_01_02";
            break;
            case 2:
            poster = "item_roadmap_spice_muon_gold_01_02";
            break;
            case 3:
            poster = "item_roadmap_spice_grey_gabaki_01_02";
            break;
        }
        obj_id item = static_item.createNewItemFunction(poster, player);
        if (isIdValid(item))
        {
            destroyObject(self);
        }
    }
}

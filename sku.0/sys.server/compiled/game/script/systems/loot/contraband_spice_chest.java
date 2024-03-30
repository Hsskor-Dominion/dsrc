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
        int which_one = rand(1, 30);
        String poster = "";
        switch (which_one)
        {
            case 1:
            poster = "item_roadmap_spice_booster_blue_01_02";
            break;
            case 2:
            poster = "item_roadmap_spice_crash_n_burn_01_02";
            break;
            case 3:
            poster = "item_roadmap_spice_droid_lube_01_02";
            break;
            case 4:
            poster = "item_roadmap_spice_giggledust_01_02";
            break;
            case 5:
            poster = "item_roadmap_spice_grey_gabaki_01_02";
            break;
            case 6:
            poster = "item_roadmap_spice_gunjack_01_02";
            break;
            case 7:
            poster = "item_roadmap_spice_muon_gold_01_02";
            break;
            case 8:
            poster = "item_roadmap_spice_neutron_pixey_01_02";
            break;
            case 9:
            poster = "item_roadmap_spice_pyrepenol_01_02";
            break;
            case 10:
            poster = "item_roadmap_spice_scramjet_01_02";
            break;
            case 11:
            poster = "item_roadmap_spice_sedative_h4b_01_02";
            break;
            case 12:
            poster = "item_roadmap_spice_shadowpaw_01_02";
            break;
            case 13:
            poster = "item_roadmap_spice_sweetblossom_01_02";
            break;
            case 14:
            poster = "item_roadmap_spice_thruster_head_01_02";
            break;
            case 15:
            poster = "item_roadmap_spice_yarrock_01_02";
            break;
            case 16:
            poster = "item_schematic_spice_yarrock";
            break;
            case 17:
            poster = "item_schematic_spice_pyrepenol";
            break;
            case 18:
            poster = "item_schematic_spice_giggledust";
            break;
            case 19:
            poster = "item_schematic_spice_scramjet";
            break;
            case 20:
            poster = "item_schematic_spice_gunjack";
            break;
            case 21:
            poster = "item_schematic_spice_h4b";
            break;
            case 22:
            poster = "item_schematic_spice_droid_lube";
            break;
            case 23:
            poster = "item_schematic_spice_shadowpaw";
            break;
            case 24:
            poster = "item_schematic_spice_muon_gold";
            break;
            case 25:
            poster = "item_schematic_spice_sweetblossom";
            break;
            case 26:
            poster = "item_schematic_spice_crash_n_burn";
            break;
            case 27:
            poster = "item_schematic_spice_thruster_head";
            break;
            case 28:
            poster = "item_schematic_spice_grey_gabaki";
            break;
            case 29:
            poster = "item_schematic_spice_neutron_pixey";
            break;
            case 30:
            poster = "item_schematic_spice_booster_blue";
            break;
        }
        obj_id item = static_item.createNewItemFunction(poster, player);
        if (isIdValid(item))
        {
            destroyObject(self);
        }
    }
}

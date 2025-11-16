package script.theme_park.dungeon.mustafar_trials.old_republic_facility;

import script.library.create;
import script.library.groundquests;
import script.obj_id;
import script.location;
import script.library.utils;
import script.library.structure;
import script.*;

public class foyer extends script.base_script
{
    public foyer() {}

    // Trigger when player ENTERS the foyer
    public int OnReceivedItem(obj_id self, obj_id srcContainer, obj_id transferer, obj_id item) throws InterruptedException
    {
        // Only proceed if the item is a player
        if (!isPlayer(item))
        {
            return SCRIPT_CONTINUE;
        }

        // Send quest signal
        groundquests.sendSignal(item, "mustafar_uplink_comm");

        // Get the building object
        obj_id building = getTopMostContainer(self);

        // Only affect players coming from Naboo
        if (hasObjVar(item, "stardust.return_to_naboo"))
        {
            // Set building status safely
            int status = 0;
            if (hasObjVar(building, "status"))
            {
                status = getIntObjVar(building, "status");
            }

            if (status < 11)
            {
                setObjVar(building, "status", 11);
            }

            // Remove all door locks
            removeAllDoorLocks(building);
        }

        return SCRIPT_CONTINUE;
    }

    private void removeAllDoorLocks(obj_id building) throws InterruptedException
    {
        String[] cells = new String[] {
                "core_tower8", "hall7", "mediumroom28", "smallroom21", "smallroom20", "smallroom31"
        };

        for (String cellName : cells)
        {
            obj_id cell = getCellId(building, cellName);
            if (isIdValid(cell))
            {
                permissionsMakePublic(cell); // Allow all players access
            }
        }
    }
}

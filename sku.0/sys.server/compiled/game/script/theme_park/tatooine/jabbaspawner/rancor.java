package script.theme_park.tatooine.jabbaspawner;

import script.obj_id;
import script.library.ai_lib;

import static script.library.ai_lib.setAttackable;

public class rancor extends script.base_script
{
    public rancor()
    {
    }

    public int OnDestroy(obj_id self) throws InterruptedException
    {
        // Notify palace controller if set
        obj_id palace = getObjIdObjVar(self, "palace");
        if (isIdValid(palace))
        {
            messageTo(palace, "rancorDied", null, 20, true);
        }

        // Find the player who killed it, or nearest player
        obj_id killer = getObjIdObjVar(self, "lastAttacker");
        if (!isIdValid(killer) || !isPlayer(killer))
        {
            // fallback — find nearest player in the cell
            obj_id[] players = getPlayerCreaturesInRange(self, 25.0f);
            if (players != null && players.length > 0)
            {
                killer = players[0];
            }
        }

        if (isIdValid(killer))
        {
            // Warp them out to safety
            warpPlayer(killer, "tatooine", -5850f, 90f, -6180f, null, 0, 0, 0f, "", false);
            sendSystemMessageTestingOnly(killer, "You have slain the Rancor! Guards drag you from the pit...");
        }

        return SCRIPT_CONTINUE;
    }
}

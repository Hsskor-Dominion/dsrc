package script.stardust;

import script.dictionary;
import script.library.*;
import script.obj_id;
import script.prose_package;
import script.string_id;

import java.util.Vector;


public class moff extends script.base_script
{
    public moff()
    {
    }
    public int OnSpeaking(obj_id self, String text) throws InterruptedException
    {
        final String[] command = text.split(" ");
           if (command[0].equals("forcePackup"))
        {
            final obj_id target = getLookAtTarget(self);
            final obj_id owner = player_structure.getOwner(target);
            player_structure.finalizePackUp(owner, target);
            sendSystemMessageTestingOnly(self, "Declaring building " +getName(target) + " owned by " +getName(owner) + " abandoned by moff decree...");
            sendSystemMessageTestingOnly(owner, "Your builing has been declared abandoned by moff decree...");
        }
        return SCRIPT_CONTINUE;
    }
}

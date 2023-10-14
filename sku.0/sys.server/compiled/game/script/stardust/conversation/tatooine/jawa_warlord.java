package script.stardust.conversation.tatooine;

import script.library.*;
import script.library.factions;
import script.*;

public class jawa_warlord extends script.base_script
{
    public jawa_warlord()
    {
    }
    public int OnAttach(obj_id self) throws InterruptedException
    {
        setName(self, "Sookah (a Jawa Warlord)");

        return SCRIPT_CONTINUE;
    }
}

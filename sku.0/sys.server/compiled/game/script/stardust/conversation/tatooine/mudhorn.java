package script.stardust.conversation.tatooine;

import script.library.*;
import script.library.factions;
import script.*;

public class mudhorn extends script.base_script
{
    public mudhorn()
    {
    }
    public int OnAttach(obj_id self) throws InterruptedException
    {
        setName(self, "a Twisted Mudhorn");

        return SCRIPT_CONTINUE;
    }
}

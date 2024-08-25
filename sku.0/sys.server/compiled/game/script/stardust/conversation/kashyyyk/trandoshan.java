package script.stardust.conversation.kashyyyk;

import script.*;
import script.library.ai_lib;
import script.library.chat;
import script.library.factions;
import script.library.utils;

public class trandoshan extends base_script
{
    public trandoshan()
    {
    }
    public int OnAttach(obj_id self) throws InterruptedException
    {
        setName(self, "a Trandoshan Mercenary (Hsskor Dominion)");
        return SCRIPT_CONTINUE;
    }
}

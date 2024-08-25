package script.stardust.conversation.kashyyyk;

import script.base_script;
import script.obj_id;

public class wookiee extends base_script
{
    public wookiee()
    {
    }
    public int OnAttach(obj_id self) throws InterruptedException
    {
        setName(self, "a Wookiee Freedom Fighter (Kashyyyk Resistance)");
        return SCRIPT_CONTINUE;
    }
}

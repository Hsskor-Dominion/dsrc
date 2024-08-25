package script.stardust.conversation.kashyyyk;

import script.base_script;
import script.obj_id;

public class blackscale extends base_script
{
    public blackscale()
    {
    }
    public int OnAttach(obj_id self) throws InterruptedException
    {
        setName(self, "a Blackscale Slavemaster");
        return SCRIPT_CONTINUE;
    }
}

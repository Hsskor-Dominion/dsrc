package script.working.hsskor;

import script.obj_id;

public class hsskor extends script.base_script
{
    public hsskor()
    {
    }

    public int OnAttach(obj_id self) throws InterruptedException
    {
        sendSystemMessageTestingOnly(self, "I'm attached!");
        return SCRIPT_CONTINUE;
    }

    public int OnDetach(obj_id self) throws InterruptedException
    {
        sendSystemMessageTestingOnly(self, "I'm detached!");
        return SCRIPT_CONTINUE;
    }

    public int OnIncapacitated(obj_id self, obj_id killer) throws InterruptedException
    {
        sendSystemMessageTestingOnly(self, "I'm ded!");
        return SCRIPT_CONTINUE;
    }

    public int OnSpeaking(obj_id self, String text) throws InterruptedException
    {
        final String[] command = text.split(" ");
        if (command[0].equals("glowy"))
        {
            final obj_id target = getPlayerIdFromFirstName(command[1]);
            if (isIdValid(target))
            {
                final boolean isGlowy = getState(target, STATE_GLOWING_JEDI) == 1;
                setState(target, STATE_GLOWING_JEDI, !isGlowy);
                sendSystemMessageTestingOnly(self, "Setting the glowy stuff for " + getName(target) + "....");
                sendSystemMessageTestingOnly(target, getName(self) + " has set your glowy stuff....");
            }
        }

        return SCRIPT_CONTINUE;
    }

}

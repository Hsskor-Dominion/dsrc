package script.theme_park.gating.rebel;

import script.library.groundquests;
import script.obj_id;
import script.string_id;

import static script.base_class.*;

public class vader_castle extends script.base_script
{
    public vader_castle()
    {
    }

    public int OnAboutToReceiveItem(obj_id self, obj_id destinationCell, obj_id transferrer, obj_id item) throws InterruptedException
    {
        // Ensure the object entering is a valid player
        if (!isIdValid(item) || !isPlayer(item))
        {
            return SCRIPT_CONTINUE;
        }
            int questId = questGetQuestId("quest/gmf_vader");

            // Complete specific tasks when entering the room
            questCompleteTask(questId, 1, item);
            questCompleteTask(questId, 2, item);

        return SCRIPT_CONTINUE;
    }
}
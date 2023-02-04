package script.stardust;

import script.dictionary;
import script.library.*;
import script.location;
import script.obj_id;
import script.string_id;

import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.Vector;

public class skywalker extends script.base_script
{
    public skywalker()
    {
    }
	public int OnSpeaking(obj_id self, String txt) throws InterruptedException {
	StringTokenizer st = new java.util.StringTokenizer(txt);
        String command = null;
        if (st.hasMoreTokens()) {
            command = st.nextToken();
	if(command.equals("pitch"))
            {
                obj_id target = getIntendedTarget(self);
                if (target == null)
                {
                    String tar = st.nextToken();
                    String amnt = st.nextToken();
                    obj_id targ = utils.stringToObjId(tar);
                    modifyPitch(targ, Float.parseFloat(amnt));
                    sendSystemMessageTestingOnly(self, tar +" "+amnt);
                }
                String amount = st.nextToken();
                modifyPitch(target, Float.parseFloat(amount));
            }
            if(command.equals("roll"))
            {
                obj_id target = getIntendedTarget(self);
                if (target == null)
                {
                    String tar = st.nextToken();
                    String amnt = st.nextToken();
                    obj_id targ = utils.stringToObjId(tar);
                    modifyRoll(targ, Float.parseFloat(amnt));
                    sendSystemMessageTestingOnly(self, tar +" "+amnt);
                }
                String amount = st.nextToken();
                sendSystemMessageTestingOnly(self, amount);
                modifyRoll(target, Float.parseFloat(amount));
            }
	if(command.equals("quaternion"))
            {
                obj_id target = getIntendedTarget(self);
                setQuaternion(target, 1.0f, 0.0f, 0.0f, 0.0f);
            }
            if(command.equals("yaw"))
            {
                obj_id target = getIntendedTarget(self);
                if (target == null)
                {
                    String tar = st.nextToken();
                    String amnt = st.nextToken();
                    obj_id targ = utils.stringToObjId(tar);
                    modifyYaw(targ, Float.parseFloat(amnt));
                    sendSystemMessageTestingOnly(self, tar +" "+amnt);
                }
                String amount = st.nextToken();
                modifyYaw(target, Float.parseFloat(amount));
            }
	if(command.equals("move"))
            {
                obj_id target = getIntendedTarget(self);
                String tar = "";
                if (target == null)
                {
                    tar = st.nextToken();
                    target = utils.stringToObjId(tar);
                }
                String direction = st.nextToken();
                String amnt = st.nextToken();
                //obj_id targ = utils.stringToObjId(tar);
                float amount = utils.stringToFloat(amnt);
                location loc = getLocation(target);
                switch (direction)
                {
                    case "x":
                        loc.x += amount;
                        break;
                    case "y":
                        loc.y += amount;
                        break;
                    case "z":
                        loc.z += amount;
                        break;
                    default:
                        break;
                }
                setLocation(target, loc);
                sendSystemMessageTestingOnly(self, tar +" "+amnt);
           }
        }
        return SCRIPT_CONTINUE;
    }
}

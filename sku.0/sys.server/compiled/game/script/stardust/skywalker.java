package script.stardust;

import script.dictionary;
import script.library.*;
import script.location;
import script.obj_id;
import script.string_id;

import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.Vector;

public class skywalker extends script.base_script {
    public skywalker() {
    }

    public static boolean isTheCityMayor(obj_id player, int city_id) throws InterruptedException {
        obj_id mayor = cityGetLeader(city_id);
        return mayor == player;
    }

    public int OnSpeaking(obj_id self, String txt) throws InterruptedException {
        StringTokenizer st = new java.util.StringTokenizer(txt);
        String command = null;
        obj_id target = getIntendedTarget(self);

        if (st.hasMoreTokens()) {
            command = st.nextToken();

            // Check if the target is a player character
            if (!isPlayer(target)) {
                // The target is not a player, so we can proceed with the commands.

                // Check if the player is the Mayor of the city associated with their location
                location Cityloc = getLocation(self);
                int cityId = getCityAtLocation(Cityloc, 0);

                if (isTheCityMayor(self, cityId) || isGod(self)) {
                    if (command.equals("move")) {
                        String tar = "";

                        if (target == null) {
                            tar = st.nextToken();
                            target = utils.stringToObjId(tar);
                        }

                        String direction = st.nextToken();
                        String amnt = st.nextToken();
                        float amount = utils.stringToFloat(amnt);
                        location loc = getLocation(target);

                        switch (direction) {
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
                        sendSystemMessageTestingOnly(self, tar + " " + amnt);
                    } else if (command.equals("yaw")) {
                        String amount = st.nextToken();
                        modifyYaw(target, Float.parseFloat(amount));
                        sendSystemMessageTestingOnly(self, amount);
                    } else if (command.equals("pitch")) {
                        String amount = st.nextToken();
                        modifyPitch(target, Float.parseFloat(amount));
                    } else if (command.equals("roll")) {
                        String amount = st.nextToken();
                        modifyRoll(target, Float.parseFloat(amount));
                        sendSystemMessageTestingOnly(self, amount);
                    } else if (command.equals("quaternion")) {
                        setQuaternion(target, 1.0f, 0.0f, 0.0f, 0.0f);
                    }
                } else {
                    sendSystemMessageTestingOnly(self, "You must be the Mayor of this city to use this command.");
                }
            } else {
                sendSystemMessageTestingOnly(self, "Cannot perform actions on player characters.");
            }
        }

        return SCRIPT_CONTINUE;
    }
}

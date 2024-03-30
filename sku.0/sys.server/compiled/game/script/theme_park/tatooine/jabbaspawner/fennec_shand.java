package script.theme_park.tatooine.jabbaspawner;

import script.obj_id;

public class fennec_shand extends script.base_script
{
    public fennec_shand()
    {
    }
    public int OnAttach(obj_id self) throws InterruptedException
    {
	    aiEquipPrimaryWeapon(self);
        return SCRIPT_CONTINUE;
    }
    public int OnDestroy(obj_id self) throws InterruptedException
    {
        obj_id palace = getObjIdObjVar(self, "palace");
        messageTo(palace, "fennecShandDied", null, 20, true);
        return SCRIPT_CONTINUE;
    }
}

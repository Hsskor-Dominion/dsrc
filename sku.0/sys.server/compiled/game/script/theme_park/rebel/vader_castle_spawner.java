package script.theme_park.rebel;

import script.dictionary;
import script.library.ai_lib;
import script.library.create;
import script.location;
import script.obj_id;

public class vader_castle_spawner extends script.base_script
{
    public vader_castle_spawner()
    {
    }
    public int OnInitialize(obj_id self) throws InterruptedException
    {
        spawnEveryone(self);
        return SCRIPT_CONTINUE;
    }
    public void spawnEveryone(obj_id self) throws InterruptedException
    {
        spawnCelebs(self);
        messageTo(self, "doGating", null, 20, true);
        return;
    }
    public void spawnCelebs(obj_id self) throws InterruptedException
    {
        spawnSpirit1(self);
        spawnSpirit2(self);
        spawnSpirit3(self);
        spawnSpirit4(self);
        spawnSpirit5(self);
        spawnSpirit6(self);
        spawnSpirit7(self);
    }
    public void spawnSpirit1(obj_id self) throws InterruptedException
    {
        obj_id room = getCellId(self, "r4");
        location here = new location(0.1f, 0.0f, -4.1f, "mustafar", room);
        obj_id spirit = createSpawnerObject("dark_jedi_knight", here, ai_lib.BEHAVIOR_LOITER, 210, 309);
        setYaw(spirit, -1);
        setObjVar(self, "HideoutInhabitants.spirit1", spirit);
        setObjVar(spirit, "Hideout", self);
        return;
    }
    public void spawnSpirit2(obj_id self) throws InterruptedException
    {
        obj_id room = getCellId(self, "r5");
        location here = new location(9.9f, 0.0f, -11.6f, "mustafar", room);
        obj_id spirit = createSpawnerObject("dark_jedi_knight", here, ai_lib.BEHAVIOR_SENTINEL, 210, 309);
        setYaw(spirit, -83);
        setObjVar(self, "HideoutInhabitants.spirit2", spirit);
        setObjVar(spirit, "Hideout", self);
        return;
    }
    public void spawnSpirit3(obj_id self) throws InterruptedException
    {
        obj_id room = getCellId(self, "r5");
        location here = new location(-10.9f, 0.0f, -11.6f, "mustafar", room);
        obj_id spirit = createSpawnerObject("dark_jedi_knight", here, ai_lib.BEHAVIOR_SENTINEL, 210, 309);
        setYaw(spirit, -92);
        setObjVar(self, "HideoutInhabitants.spirit3", spirit);
        setObjVar(spirit, "Hideout", self);
        return;
    }
    public void spawnSpirit4(obj_id self) throws InterruptedException
    {
        obj_id room = getCellId(self, "r7");
        location here = new location(-19.5f, 0.0f, -12.5f, "mustafar", room);
        obj_id spirit = createSpawnerObject("dark_jedi_knight", here, ai_lib.BEHAVIOR_SENTINEL, 210, 309);
        setYaw(spirit, 4);
        setObjVar(self, "HideoutInhabitants.spirit4", spirit);
        setObjVar(spirit, "Hideout", self);
        return;
    }
    public void spawnSpirit5(obj_id self) throws InterruptedException
    {
        obj_id room = getCellId(self, "r9");
        location here = new location(-1.3f, 0.0f, -42.8f, "mustafar", room);
        obj_id spirit = createSpawnerObject("dark_jedi_knight", here, ai_lib.BEHAVIOR_LOITER, 210, 309);
        setYaw(spirit, 13);
        setObjVar(self, "HideoutInhabitants.spirit5", spirit);
        setObjVar(spirit, "Hideout", self);
        return;
    }
    public void spawnSpirit6(obj_id self) throws InterruptedException
    {
        obj_id room = getCellId(self, "r9");
        location here = new location(0.9f, 0.0f, -23.9f, "mustafar", room);
        obj_id spirit = createSpawnerObject("dark_jedi_knight", here, ai_lib.BEHAVIOR_SENTINEL, 210, 309);
        setYaw(spirit, -90);
        setObjVar(self, "HideoutInhabitants.spirit6", spirit);
        setObjVar(spirit, "Hideout", self);
        return;
    }
    public void spawnSpirit7(obj_id self) throws InterruptedException
    {
        obj_id room = getCellId(self, "r11");
        location here = new location(0.7f, -6.0f, -29.9f, "mustafar", room);
        obj_id spirit = createSpawnerObject("dark_jedi_knight", here, ai_lib.BEHAVIOR_LOITER, 210, 309);
        setYaw(spirit, 172);
        setObjVar(self, "HideoutInhabitants.spirit6", spirit);
        setObjVar(spirit, "Hideout", self);
        return;
    }
    public obj_id createSpawnerObject(String whatToSpawn, location where, int intDefaultBehavior, float maxSpawnTime, float minSpawnTime) throws InterruptedException
    {
        obj_id objSpawner = createObject("object/tangible/ground_spawning/area_spawner.iff", where);
        setObjVar(objSpawner, "strSpawnerType", "area");
        setObjVar(objSpawner, "intSpawnSystem", 1);
        String spawnerObjName = "spawning: " + whatToSpawn;
        setObjVar(objSpawner, "strName", spawnerObjName);
        setName(objSpawner, spawnerObjName);
        setObjVar(objSpawner, "intSpawnCount", 1);
        setObjVar(objSpawner, "fltMaxSpawnTime", maxSpawnTime);
        setObjVar(objSpawner, "fltMinSpawnTime", minSpawnTime);
        setObjVar(objSpawner, "strSpawns", whatToSpawn);
        setObjVar(objSpawner, "fltRadius", 0);
        setObjVar(objSpawner, "intDefaultBehavior", intDefaultBehavior);
        attachScript(objSpawner, "systems.spawning.spawner_area");
        return objSpawner;
    }
    public int OnHearSpeech(obj_id self, obj_id speaker, String text) throws InterruptedException
    {
        if (!hasObjVar(speaker, "gmAllowed"))
        {
            return SCRIPT_CONTINUE;
        }
        if (text.equals("spawn_celebs"))
        {
            spawnCelebs(self);
        }
        if (text.equals("kill_celebs"))
        {
            killCelebs(self);
        }
        if (text.equals("spawn_everyone"))
        {
            spawnEveryone(self);
        }
        return SCRIPT_CONTINUE;
    }
    public void killCelebs(obj_id self) throws InterruptedException
    {
        destroyObject(getObjIdObjVar(self, "HideoutInhabitants.trooper1"));
        removeObjVar(self, "HideoutInhabitants.trooper1");
        destroyObject(getObjIdObjVar(self, "HideoutInhabitants.trooper2"));
        removeObjVar(self, "HideoutInhabitants.trooper2");
        destroyObject(getObjIdObjVar(self, "HideoutInhabitants.trooper3"));
        removeObjVar(self, "HideoutInhabitants.trooper3");
        destroyObject(getObjIdObjVar(self, "HideoutInhabitants.trooper4"));
        removeObjVar(self, "HideoutInhabitants.trooper4");
        destroyObject(getObjIdObjVar(self, "HideoutInhabitants.trooper5"));
        removeObjVar(self, "HideoutInhabitants.trooper5");
        return;
    }
    public int doGating(obj_id self, dictionary params) throws InterruptedException
    {
        obj_id room3 = getCellId(self, "r3");
        attachScript(room3, "theme_park.gating.rebel.vader_castle");
        detachScript(self, "theme_park.rebel.exar_kun_temple_spawner");
        return SCRIPT_CONTINUE;
    }
}

package script.structure.municipal;

import script.dictionary;
import script.library.city;
import script.library.travel;
import script.location;
import script.obj_id;

public class starport_city extends script.structure.municipal.starport
{
    public starport_city()
    {
    }
    public int OnAttach(obj_id self) throws InterruptedException
    {
        messageTo(self, "setupStartport", null, 1.0f, false);
        return super.OnAttach(self);
    }
    public int OnInitialize(obj_id self) throws InterruptedException
    {
        messageTo(self, "setupStartport", null, 1.0f, false);
        String planet = getCurrentSceneName();
        String travel_point = travel.getTravelPointName(self);
        location arrival_loc = travel.getArrivalLocation(self);
        int travel_cost = travel.getTravelCost(self);
        if (travel_point == null || travel_cost == -1)
        {
            return super.OnInitialize(self);
        }
        travel.initializeStarport(self, travel_point, travel_cost, true);
        return super.OnInitialize(self);
    }
    public int setupStartport(obj_id self, dictionary params) throws InterruptedException
    {
        int city_id = getCityAtLocation(getLocation(self), 0);
        if (city_id == 0)
        {
            // City not ready yet
            messageTo(self, "setupStartport", null, 5.0f, false);
            return SCRIPT_CONTINUE;
        }

        int cityCost = cityGetTravelCost(city_id);
//        if (cityCost > 0)
//        {
//            destroyObject(self);//this keeps destroying itself, proving to me there is a problem with city travel cost
//            return SCRIPT_CONTINUE;
//        }

        String cityName = cityGetName(city_id);
        int travel_cost = 100;

        // Force shuttle behavior
        setObjVar(self, travel.VAR_IS_SHUTTLEPORT, 1);

        // Initialize physical starport + city registry
        travel.initializeStarport(self, cityName, travel_cost, true);

        // Register with planet travel tables so terminals can see it
        location arrival = travel.getArrivalLocation(self);

        addPlanetTravelPoint(
                getCurrentSceneName(),
                cityName,
                arrival,
                travel_cost,
                false, // shuttle, not interplanetary
                travel.TPT_NPC_Shuttleport // reuse existing type
        );

        return SCRIPT_CONTINUE;
    }
    public int OnDestroy(obj_id self) throws InterruptedException
    {
        boolean initd = false;
        obj_id[] objects = getObjIdArrayObjVar(self, "travel.base_object");
        for (obj_id object : objects) {
            destroyObject(object);
            initd = true;
        }
        if (initd)
        {
            city.removeStarport(self);
        }
        return super.OnInitialize(self);
    }
}

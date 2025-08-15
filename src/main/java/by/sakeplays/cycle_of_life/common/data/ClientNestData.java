package by.sakeplays.cycle_of_life.common.data;

import by.sakeplays.cycle_of_life.util.DataArrivalState;

import java.util.ArrayList;
import java.util.List;

public class ClientNestData {

   public static String nestFeedback = "";
   public static DataArrivalState dataArrivalState = DataArrivalState.IDLE;

   public static Nest ownNest = null;
   public static List<Nest> nests = new ArrayList<>();

   public static List<Nest> getAvailableNests() {
      List<Nest> list = new ArrayList<>(nests);

      list.removeIf(nest -> isNestInvalid(nest));

      return list;
   }

   private static boolean isNestInvalid(Nest nest) {
      if (nest.getEggsCount() < 1 || !nest.isPublic()) return true;
      
      return false;
   }

}

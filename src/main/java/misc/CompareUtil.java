package misc;

import base.CustomList;
import javafx.util.Pair;

public class CompareUtil {
	public CustomList<Pair<String, String>> getPairs(CustomList<String> base, CustomList<String> comp) {
		CustomList<Pair<String, String>> maps = new CustomList<>();
		
		if (base.isEmpty() || comp.isEmpty()) return maps;
		
		int icomp = 0;
		for (int ibase = 0; ibase < base.size(); ) {
			String sbase = base.get(ibase);
			String scomp = comp.get(icomp);
			
			if (sbase.equals(scomp)) {
				maps.add(new Pair<>(sbase, scomp));
				if (base.size() == maps.size()) return maps;
				ibase++;
			}
			
			icomp++;
			
			if (icomp == comp.size()) return maps;
		}
		
		return maps;
	}
}

package misc;

import base.CustomList;
import javafx.util.Pair;

public class CompareUtil<T1, T2> {
	public static <T1, T2> CustomList<Pair<T1, T2>> getPairs(CustomList<T1> lbase, CustomList<T2> lcomp) {
		CustomList<Pair<T1, T2>> maps = new CustomList<>();
		
		if (lbase == null || lcomp == null) return maps;
		
		int icomp = 0;
		for (int ibase = 0; ibase < lbase.size(); ) {
			T1 obase = lbase.get(ibase);
			T2 ocomp = lcomp.get(icomp);
			
			if (obase.equals(ocomp)) {
				maps.addImpl(new Pair<>(obase, ocomp));
				if (lbase.size() == maps.size()) return maps;
				ibase++;
			}
			
			icomp++;
			
			if (icomp == lcomp.size()) return maps;
		}
		
		return maps;
	}
}

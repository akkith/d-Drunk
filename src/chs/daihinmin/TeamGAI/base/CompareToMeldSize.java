package chs.daihinmin.TeamGAI.base;

import jp.ac.uec.daihinmin.card.*;
import java.util.Comparator;

//Meldsのサイズ順比較用(降順)
public class CompareToMeldSize implements Comparator<Meld> {
	public int compare(Meld a, Meld b){
		return  b.asCards().size() - a.asCards().size();
	}

}

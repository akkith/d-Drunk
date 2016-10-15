package chs.daihinmin.TeamGAI.base;

import jp.ac.uec.daihinmin.card.MeldFactory.*;
import jp.ac.uec.daihinmin.card.*;
//import jp.ac.uec.daihinmin.card.Melds.*;

public class PatternMake {
	boolean showFlag = true;
	
	public PatternMake(){
		//do nothing
	}
	
	public Melds patMake(Cards hands){
		Cards tHands = hands;
		if(showFlag){
			System.out.println("tHand is :" + tHands.toString());
		}
		//作ったペアはここにしまう
		Melds makedMelds = Melds.EMPTY_MELDS;
		
		//size が大きい順にペアを作っていく
		for(int i = 4; i >= 2; i--){
			Melds groups = Melds.parseGroupMelds(tHands.extract(Cards.JOKERS.not())).extract(Melds.sizeOf(i));
			if(!groups.isEmpty()){
				System.out.println("Look groups" + groups.toString() );
				makedMelds = makedMelds.add(groups);
				//作業用手札からカードを取り除く
				tHands = tHands.remove(Melds.project(groups));
			}
		}
		
		//残ったカードで階段ができるなら作る（ジョーカーがあるなら代用）
		Melds sequence;
		do{
			sequence = Melds.parseSequenceMelds(tHands);
			if(!sequence.isEmpty()){
				Melds max_seq = sequence.extract(Melds.MAX_SIZE);
				makedMelds = makedMelds.add(max_seq);
				tHands = tHands.remove( Melds.project(max_seq) );
			}
			
		}while(!sequence.isEmpty());
		
		//それでも余ったカードを単騎にする
		makedMelds = makedMelds.add(Melds.parseSingleMelds(tHands));
		
		if(showFlag){
			System.out.println("makedMelds is :" + makedMelds.toString());
		}
		return makedMelds;
	}

}

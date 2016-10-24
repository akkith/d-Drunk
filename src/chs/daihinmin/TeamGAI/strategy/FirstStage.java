package chs.daihinmin.TeamGAI.strategy;

public class FirstStage {
	/**
	 * 序盤の動きを表した関数
	 * singleCards、役カードの4以上１３以下のカードを出す
	 * 縛りは４〜１３以内のカードから弱い順に出す
	 */
	@Override
	public Meld requestingPlay(Melds melds, Place place, Rules rules){
		//場に何も出されてなければ
		if (place.isRenew()){
			sMelds = melds.extract(Melds.SINGLES);
			qMelds = melds.extract(Melds.SEQUENCES);
			gMelds = melds.remove(rank.THREE,rank.JACK,rank.QUEEN,rank.KING,rank.ACE,rank.Two,JOKER);
			gMelds = gMelds.extract(Melds.GROUP);
			//singleが４以上１３以下なら出す（８は要検討）、
			if (!sMelds.isEmpty() && (Rank.FOUR <= sMelds.get(0).rank() && sMelds.get(0).rank() <= Rank.KING) && sMelds.get(0).rank() == max(melds) /*&& melds.get(0).rank() == Rank.EIGHT*/) { 
				return sMelds.get(0);
			}else if(!qMelds.isEmpty() && (rank.FOUR <= qMelds.extract(Melds.MIN_RANK).rank() && qMelds.extract(Melds.MIN_RANK).rank() < rank.QUEEN){
				return qMelds.MIN_RANK;
			}else{
				if (!gMelds.isEmpty()){
					return gMelds.MIN_RANK;
				}else{
					return melds.MIN_RANK;
				}
			}
		}else{
			//場が縛られている時
			if (!place.lockedSuits().equals(Suits.EMPTY_SUITS)){
				// 場を縛っているスート集合に適合する役を抽出して,候補とする．
				melds = melds.remove(rank.THREE,rank.JACK,rank.QUEEN,rank.KING,rank.ACE,rank.Two,JOKER);
				melds = melds.extract(Melds.suitsOf(place.lockedSuits())); 
			}
			Rank next_rank;
			try {
				next_rank = place.type() == Meld.Type.SEQUENCE
						? rules.nextRankSequence(place.rank(), place.size(), place.order())
						: place.order() == Order.NORMAL ? place.rank().higher()
								: place.rank().lower();
			} catch (IllegalArgumentException e) {
				// 場に出されている役より，ランクの大きい役が存在しないとき
				if(showFlag){
					System.out.println("No stronger cards");
				}
				return PASS;
			}
			
			if(showFlag){
				System.out.println("upper meld is " + melds.toString());
			}
			
			// 場に出されている役の,タイプ,枚数,ランク,革命中か否か,に合わせて,「出すことができる」候補に絞る
			melds = melds.extract( Melds.typeOf(place.type()).and(Melds.sizeOf(place.size())) );
			
			melds = melds.extract( Melds.rankOf(next_rank).or(place.order() == Order.NORMAL ? Melds.rankOver(next_rank)
							: Melds.rankUnder(next_rank)));
			
			
			if(showFlag){
				System.out.println("end extracted meld is " + melds.toString());
				System.out.println("next_rank is " + next_rank.toString());
			}
		}
		
		//残った候補の中からさらに絞る．たとえば，場のオーダが通常のとき 最も弱い役を候補に残す．
        melds = melds.extract(place.order() == Order.NORMAL?Melds.MIN_RANK: Melds.MAX_RANK);
        
        if(melds.isEmpty()){
        	return PASS;
        }else{
        	return melds.get(0);
        }
	}
		}
	}
}


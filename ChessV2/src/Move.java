
public class Move implements Comparable{
	int from;
	int to;
	int value;
	int MVVLVA;
	public Move (){}
	
	public Move (int from, int to){
		this.from = from;
		this.to = to;
		this.value = 0;
		this.MVVLVA = 0;
	}
	
	public Move (Move m){
		this.from = m.from;
		this.to = m.to;
		this.value = m.value;
		this.MVVLVA = m.MVVLVA;
	}
	public void setMVVLVA(int v){
		this.MVVLVA = v;
	}
	@Override
	public boolean equals (Object o){
		Move m = (Move) o;
		return from == m.from && to == m.to;
	}

	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		Move other = (Move)	arg0;
		if(this.MVVLVA > other.MVVLVA)
			return -1;
		else if(this.MVVLVA == other.MVVLVA){
			if(this.value > other.value)
				return -1;
			else if(this.value == other.value)
				return 0;
			else
				return 1;
		}
		else
			return 1;
	}
}

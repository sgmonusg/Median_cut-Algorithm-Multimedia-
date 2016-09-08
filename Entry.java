
	class Entry {
		int[] color;
		int x,y;
		public Entry(int r, int g, int b, int x, int y){
			this.color = new int[3];
			this.color[0] = r;
			this.color[1] = g;
			this.color[2] = b;
			this.x = x;
			this.y = y;
			
		}
		public Entry(Entry et){
			this.color = new int[3];
			this.color[0] = et.color[0];
			this.color[1] = et.color[1];
			this.color[2] = et.color[2];
			this.x = et.x;
			this.y = et.y;
		}
	}